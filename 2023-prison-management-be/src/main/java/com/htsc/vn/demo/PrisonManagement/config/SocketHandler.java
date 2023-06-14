package com.htsc.vn.demo.PrisonManagement.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.htsc.vn.demo.PrisonManagement.Uils.Common;
import com.htsc.vn.demo.PrisonManagement.Uils.StatusUser;
import com.htsc.vn.demo.PrisonManagement.dto.CheckingLogDTO;
import com.htsc.vn.demo.PrisonManagement.model.*;
import com.htsc.vn.demo.PrisonManagement.mapper.CheckInOutInOutRedis;
import com.htsc.vn.demo.PrisonManagement.redis.model.CheckingLogDTORedis;
import com.htsc.vn.demo.PrisonManagement.redis.service.InOutRedisService;
import com.htsc.vn.demo.PrisonManagement.service.CheckInService;
import com.htsc.vn.demo.PrisonManagement.service.CheckOutService;
import io.netty.util.internal.StringUtil;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
public class SocketHandler extends TextWebSocketHandler {

    private static final String CHECKIN = "checkins";
    private static final String CHECKOUT = "checkouts";
    private static final String RENDER_CLIENT = "init-data";
    private final CheckInService checkInService;
    private final CheckOutService checkOutService;
    private final InOutRedisService inOutRedisService;
    public List<WebSocketSession> sessions = new ArrayList<>();

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

    public SocketHandler(CheckInService checkInService, CheckOutService checkOutService,InOutRedisService inOutRedisService,MongoTemplate mongoTemplate) {
        this.checkInService = checkInService;
        this.checkOutService = checkOutService;
        this.inOutRedisService = inOutRedisService;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println(message.getPayload());
            String payload = message.getPayload();
            Gson gson = new GsonBuilder().serializeNulls().create();
        try {
            AIModel aiModel = objectMapper.readValue(payload, AIModel.class);
            CheckOut checkOut = null;
            CheckIn checkIn = null;
            StatusUser statusUser = null;
            InOutResponse response = null;
            if (aiModel.getTypeEvent().equals(RENDER_CLIENT)) {
                response = inOutRedisService.getDataRenderFromRedis(Common.REDIS_KEY_RENDER_DATA_CLIENT);
                broadcast(gson.toJson(response));
                //System.out.println("RenderData: "+response);
                return;
            }
            switch (aiModel.getTypeEvent()) {
                case CHECKOUT:
                    checkOut = mongoTemplate.findById(aiModel.getEventId(), CheckOut.class);
                    statusUser = StatusUser.CHECK_OUT;
                    break;
                case CHECKIN:
                    checkIn = mongoTemplate.findById(aiModel.getEventId(), CheckIn.class);
                    statusUser = StatusUser.CHECK_IN;
                    break;
                default:
                    throw new Exception("Type is only in checkins or checkouts");
            }
            if (Objects.isNull(checkOut) && Objects.isNull(checkIn)) {
                throw new AccountNotFoundException("EventId is not found in system!");
            }
            CheckingLog checkingLog = mongoTemplate.findById(checkOut != null ? checkOut.getUserId() : checkIn.getUserId(), CheckingLog.class);
            List<CheckingLogDTO> checkingLogDTOs = Collections.emptyList();
            if (checkingLog != null) {
                checkingLog.setUserStatusId(statusUser.getObjectId());
                mongoTemplate.save(checkingLog);
                checkingLogDTOs = mapperCheckingLog(checkingLog.getGroupUserId());
            }
            CheckingLogDTO finalCheckingLogDTO = getCheckingLogDTO(checkOut != null ? checkOut.getUserId() : checkIn.getUserId(),
                    checkOut != null ? checkOut.getTime() : checkIn.getTime(),
                    checkOut != null ? checkOut.getFaceImg() : checkIn.getFaceImg(),
                    checkingLogDTOs);
            response = getInOutResponse(finalCheckingLogDTO);

            broadcast(gson.toJson(response));
            inOutRedisService.addDataRenderTORedis(Common.REDIS_KEY_RENDER_DATA_CLIENT,response);
            System.out.println("Sent data: "+finalCheckingLogDTO.getName() +"-"+"StatusId: "+ finalCheckingLogDTO.getUserStatus());
        } catch (IOException e) {
            throw new IOException("Mapper Object fail:", e);
        }
    }
    private CheckingLogDTO getCheckingLogDTO(String userId, String time, String faceImg, List<CheckingLogDTO> checkingLogDTOs) {
        return checkingLogDTOs.stream()
                .filter(x -> x.getId().equals(userId))
                .findFirst()
                .map(checkingLogDTO -> {
                    checkingLogDTO.setTime(time);
                    checkingLogDTO.setFaceUrl(faceImg);
                    return checkingLogDTO;
                })
                .orElse(null);
    }



    private InOutResponse getInOutResponse(CheckingLogDTO checkingLogDTO) {
        //lưu data vào redis khi có bản ghi mới từ AI
        CheckingLogDTORedis ck = CheckInOutInOutRedis.fromAI(checkingLogDTO);
        inOutRedisService.saveOrUpdateUser(ck);
        List<CheckingLogDTORedis> getData = inOutRedisService.getAllDataFromRedis();
        Map<Integer, List<CheckingLogDTORedis>> groupedData = getData.stream()
                .collect(Collectors.groupingBy(CheckingLogDTORedis::getGroupUserId));
        List<InOutResponse.TeamDTO> teamDTOList = groupedData.entrySet().stream()
                .map(entry -> {
                    InOutResponse.TeamDTO teamDTO = new InOutResponse.TeamDTO();
                    teamDTO.setGroupId(entry.getKey());
                    teamDTO.setGroupName(entry.getValue().get(0).getGroupUserName());
                    teamDTO.setUsers(entry.getValue());
                    return teamDTO;
                })
                .collect(Collectors.toList());
        List<InOutResponse.TeamDTO> output = teamDTOList.stream()
                .peek(data -> {
                    List<CheckingLogDTORedis> checkingLogDTORedis = data.getUsers();
                    int targetUserIndex = IntStream.range(0, checkingLogDTORedis.size())
                            .filter(i -> checkingLogDTORedis.get(i).getId().equals(ck.getId()))
                            .findFirst()
                            .orElse(-1);
                    if (targetUserIndex >= 0) {
                        CheckingLogDTORedis targetUser = checkingLogDTORedis.get(targetUserIndex);
                        checkingLogDTORedis.remove(targetUserIndex);
                        checkingLogDTORedis.add(0, targetUser);
                    }
                })
                .sorted(Comparator.comparing((InOutResponse.TeamDTO data) -> {
                    List<CheckingLogDTORedis> checkingLogDTORedis = data.getUsers();
                    return checkingLogDTORedis.stream()
                            .anyMatch(u -> Objects.equals(u.getId(), ck.getId()));
                }).reversed())
                .collect(Collectors.toList());

        InOutResponse inOutResponse = new InOutResponse();
        inOutResponse.setData(output);
        return inOutResponse;
    }

    private void broadcast(String message) throws IOException {
        Iterator<WebSocketSession> sessionIterator = sessions.iterator();
        while (sessionIterator.hasNext()) {
            WebSocketSession session = sessionIterator.next();
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
                //System.out.println(message);
            } else {
                sessionIterator.remove();
            }
        }
    }

    public List<CheckingLogDTO> mapperCheckingLog(ObjectId groupUserId) {
        Query query = new Query(Criteria.where("group_user_id").is(groupUserId));
        List<CheckingLog> checkingLogs = mongoTemplate.find(query,CheckingLog.class);
        List<CheckingLogDTO> checkingLogDTOS = new ArrayList<>();
        for (CheckingLog c: checkingLogs) {
            CheckingLogDTO checkingLogDTO = new CheckingLogDTO();
            checkingLogDTO.setId(c.getId().toString());
            checkingLogDTO.setName(c.getName());
            checkingLogDTO.setImage(Common.BASE_URL_IMG.concat("/").concat(c.getImage()));
            UserStatus userStatus = mongoTemplate.findById(c.getUserStatusId(),UserStatus.class);
            checkingLogDTO.setUserStatus(userStatus != null ? userStatus.getStatusId() : -2);
            TypeUser typeUser = mongoTemplate.findById(c.getTypeUserId(), TypeUser.class);
            checkingLogDTO.setTypeUserId(typeUser != null ? typeUser.getTypeId() : -2);
            GroupUser groupUser = mongoTemplate.findById(c.getGroupUserId(), GroupUser.class);
            checkingLogDTO.setGroupUserId(groupUser != null ? groupUser.getGroupId() : -2);
            checkingLogDTO.setGroupUserName(groupUser != null ? groupUser.getGroupName() : "");
            checkingLogDTOS.add(checkingLogDTO);
        }
        return checkingLogDTOS;
    }
}
