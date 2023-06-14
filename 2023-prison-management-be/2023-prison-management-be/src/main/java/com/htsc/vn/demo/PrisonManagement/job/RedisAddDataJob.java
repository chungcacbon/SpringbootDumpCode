package com.htsc.vn.demo.PrisonManagement.job;

import com.htsc.vn.demo.PrisonManagement.dto.CheckingLogDTO;
import com.htsc.vn.demo.PrisonManagement.dto.UserBuilder;
import com.htsc.vn.demo.PrisonManagement.dto.UserCheckInOutInfo;
import com.htsc.vn.demo.PrisonManagement.redis.model.CheckingLogDTORedis;
import com.htsc.vn.demo.PrisonManagement.redis.service.InOutRedisService;
import com.htsc.vn.demo.PrisonManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedisAddDataJob {

    @Autowired
    private UserService userService;
    @Autowired
    private InOutRedisService inOutRedisService;

    //@Scheduled(cron = "0 10 16 * * ?")
    public void addDataToRedis() throws ParseException {
        List<CheckingLogDTORedis> checkingLogDTOList = mapperDataToCheckingLogDTO();
        for (CheckingLogDTORedis c: checkingLogDTOList) {
            inOutRedisService.saveOrUpdateUser(c);
            System.out.println("Save OK");
        }

    }
    public List<CheckingLogDTORedis> mapperDataToCheckingLogDTO() throws ParseException {

        List<UserCheckInOutInfo> userCheckInOutInfoList = userService.getUsersWithCheckinAndCheckoutInfo(new UserBuilder());
        List<CheckingLogDTORedis> checkingLogDTORedies = new ArrayList<>();
        for (UserCheckInOutInfo u: userCheckInOutInfoList) {
            CheckingLogDTORedis c = new CheckingLogDTORedis();
            c.setId(u.getId());
            c.setName(u.getName());
            c.setImage(u.getImage());
            c.setGroupUserId(u.getGroupUserId());
            c.setGroupUserName(u.getGroupName());
            c.setTypeUserId(u.getTypeUserId());
            c.setUserStatus(-1);
            checkingLogDTORedies.add(c);
        }
        return checkingLogDTORedies;
    }
}
