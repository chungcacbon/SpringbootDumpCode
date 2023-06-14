package com.htsc.vn.demo.PrisonManagement.service;

import com.htsc.vn.demo.PrisonManagement.Entity.input.UserInput;
import com.htsc.vn.demo.PrisonManagement.Entity.output.UserOutput;
import com.htsc.vn.demo.PrisonManagement.Uils.Common;
import com.htsc.vn.demo.PrisonManagement.Uils.StatusUser;
import com.htsc.vn.demo.PrisonManagement.Uils.UilsDate;
import com.htsc.vn.demo.PrisonManagement.dto.CheckInOutDto;
import com.htsc.vn.demo.PrisonManagement.dto.UserBuilder;
import com.htsc.vn.demo.PrisonManagement.dto.UserCheckInOutInfo;
import com.htsc.vn.demo.PrisonManagement.exception.FeignClientException;
import com.htsc.vn.demo.PrisonManagement.exception.HtscException;
import com.htsc.vn.demo.PrisonManagement.feign.service.UsersFeignClientService;
import com.htsc.vn.demo.PrisonManagement.mapper.CheckInOutInOutRedis;
import com.htsc.vn.demo.PrisonManagement.model.*;
import com.htsc.vn.demo.PrisonManagement.model.feign.output.UsersFeignClientOutput;
import com.htsc.vn.demo.PrisonManagement.redis.model.CheckingLogDTORedis;
import com.htsc.vn.demo.PrisonManagement.redis.service.InOutRedisService;
import com.htsc.vn.demo.PrisonManagement.repository.UserRepository;
import feign.FeignException;
import feign.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UsersFeignClientService usersFeignClientService;
	@Autowired
	private InOutRedisService inOutRedisService;
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(UilsDate.DATE_TIME_FOMART);
	private static final SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat(UilsDate.DATE_TIME_FOMART);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(UilsDate.DATE_FOMART);

	@Override
	public List<UserCheckInOutInfo> getUsersWithCheckinAndCheckoutInfo(UserBuilder userBuilder) throws ParseException {
		String userIdTemp = (userBuilder.getUserId() != null && userBuilder.getUserId().isEmpty()) ? null
				: userBuilder.getUserId();
		String userNameTemp = (userBuilder.getUserName() != null && userBuilder.getUserName().isEmpty()) ? null
				: userBuilder.getUserName();
		String startDateTemp = (userBuilder.getStartDate() != null && userBuilder.getStartDate().isEmpty()) ? null
				: userBuilder.getStartDate();
		String endDateTemp = (userBuilder.getEndDate() != null && userBuilder.getEndDate().isEmpty()) ? null
				: userBuilder.getEndDate();
		String todayTemp = (userBuilder.getToday() != null && userBuilder.getToday().isEmpty()) ? null
				: userBuilder.getToday();
		List<CheckingLog> checkingLogs = mongoTemplate.findAll(CheckingLog.class);
		List<UserCheckInOutInfo> userCheckInOutInfos = new ArrayList<>();
		List<UserCheckInOutInfo> output = null;
		if (Objects.isNull(todayTemp)) {
			AggregationOperation matchCheckIn = Aggregation.match(Criteria.where("cam_id").is(Common.CAM_ID_CHECKIN));
			AggregationOperation matchCheckOut = Aggregation.match(Criteria.where("cam_id").is(Common.CAM_ID_CHECKOUT));

			ProjectionOperation projectCheckIn = Aggregation
					.project("_id", "user_id", "name", "time", "date", "cam_id", "face_img")
					.andInclude("user_id", "name", "time", "date", "cam_id", "face_img").andExpression("null")
					.as("timeCheckIn").andExpression("null").as("dateCheckIn").andExpression("null").as("timeCheckOut")
					.andExpression("null").as("dateCheckOut").andExpression("type").as("type");

			ProjectionOperation projectCheckOut = Aggregation
					.project("_id", "user_id", "name", "time", "date", "cam_id", "face_img")
					.andInclude("user_id", "name", "time", "date", "cam_id", "face_img").andExpression("null")
					.as("timeCheckIn").andExpression("null").as("dateCheckIn").andExpression("null").as("timeCheckOut")
					.andExpression("null").as("dateCheckOut").andExpression("type").as("type");

			Aggregation aggregationCheckIn = Aggregation.newAggregation(matchCheckIn, projectCheckIn);
			AggregationResults<CheckInOutDto> checkInResults = mongoTemplate.aggregate(aggregationCheckIn, "checkins",
					CheckInOutDto.class);
			List<CheckInOutDto> checkInOuts = new ArrayList<>(checkInResults.getMappedResults());
			Aggregation aggregationCheckOut = Aggregation.newAggregation(matchCheckOut, projectCheckOut);
			AggregationResults<CheckInOutDto> checkOutResults = mongoTemplate.aggregate(aggregationCheckOut,
					"checkouts", CheckInOutDto.class);
			List<CheckInOutDto> checkOuts = new ArrayList<>(checkOutResults.getMappedResults());
			checkInOuts.addAll(checkOuts);
			sortTime(checkInOuts);

			for (CheckInOutDto checkInOut : checkInOuts) {
				populateUserCheckInOutInfo(checkInOut, userCheckInOutInfos);
			}
			List<UserCheckInOutInfo> outInfoList = new ArrayList<>();
			int i = 0;
			while (i < userCheckInOutInfos.size()) {
				boolean removed = false;
				for (int j = 0; j < userCheckInOutInfos.size(); j++) {
					boolean isValid = (userCheckInOutInfos.get(i).getDateCheckin() == null
							&& userCheckInOutInfos.get(j).getDateCheckin() != null)
							|| (userCheckInOutInfos.get(i).getDateCheckin() != null
									&& userCheckInOutInfos.get(j).getDateCheckin() == null);
					if (userCheckInOutInfos.get(j).getId().equals(userCheckInOutInfos.get(i).getId()) && isValid) {
						if (Objects.isNull(userCheckInOutInfos.get(i).getDateCheckin())) {
							Date dateCheckin = SIMPLE_DATE_TIME_FORMAT.parse(userCheckInOutInfos.get(j).getDateCheckin()
									+ " " + userCheckInOutInfos.get(j).getCheckinTime());
							Date dateCheckout = SIMPLE_DATE_TIME_FORMAT
									.parse(userCheckInOutInfos.get(i).getDateCheckOut() + " "
											+ userCheckInOutInfos.get(i).getCheckoutTime());
							if (dateCheckin.compareTo(dateCheckout) > 0) {
								userCheckInOutInfos.get(i).setDateCheckin(userCheckInOutInfos.get(j).getDateCheckin());
								userCheckInOutInfos.get(i).setCheckinTime(userCheckInOutInfos.get(j).getCheckinTime());
								outInfoList.add(userCheckInOutInfos.get(i));
								userCheckInOutInfos.remove(i);
								userCheckInOutInfos.remove(j - 1);
								removed = true;
								break;
							}
						} else {
							Date dateCheckin = SIMPLE_DATE_TIME_FORMAT.parse(userCheckInOutInfos.get(i).getDateCheckin()
									+ " " + userCheckInOutInfos.get(i).getCheckinTime());
							Date dateCheckout = SIMPLE_DATE_TIME_FORMAT
									.parse(userCheckInOutInfos.get(j).getDateCheckOut() + " "
											+ userCheckInOutInfos.get(j).getCheckoutTime());
							if (dateCheckin.compareTo(dateCheckout) > 0) {
								userCheckInOutInfos.get(i)
										.setDateCheckOut(userCheckInOutInfos.get(j).getDateCheckOut());
								userCheckInOutInfos.get(i)
										.setCheckoutTime(userCheckInOutInfos.get(j).getCheckoutTime());
								outInfoList.add(userCheckInOutInfos.get(i));
								userCheckInOutInfos.remove(i);
								userCheckInOutInfos.remove(j - 1);
								removed = true;
								break;
							}
						}
					}
				}
				if (!removed) {
					outInfoList.add(userCheckInOutInfos.get(i));
					i++;
				}
			}

			userCheckInOutInfos = outInfoList;

		} else {
			for (CheckingLog checkingLog : checkingLogs) {

				Query query = new Query(Criteria.where("user_id").is(checkingLog.getId().toString()));
				query.addCriteria(Criteria.where("date").is(todayTemp));
				List<CheckIn> checkIns = mongoTemplate.find(query, CheckIn.class);
				List<CheckOut> checkOuts = mongoTemplate.find(query, CheckOut.class);
				if (!checkIns.isEmpty() && checkOuts.isEmpty()) {
					checkOuts = mongoTemplate.findAll(CheckOut.class);
				}

				CheckIn checkIn = sortUser(checkIns,
						ci -> LocalDateTime.parse(ci.getDate() + " " + ci.getTime(), DATE_TIME_FORMATTER), true);
				CheckOut checkOut = sortUser(checkOuts,
						co -> LocalDateTime.parse(co.getDate() + " " + co.getTime(), DATE_TIME_FORMATTER), false);
				Query queryGroup = new Query(Criteria.where("_id").is(checkingLog.getGroupUserId()));
				Query queryTypeUser = new Query(Criteria.where("_id").is(checkingLog.getTypeUserId()));
				GroupUser groupUser = mongoTemplate.findOne(queryGroup, GroupUser.class);
				TypeUser typeUser = mongoTemplate.findOne(queryTypeUser, TypeUser.class);
				Query queryStatusUser = new Query(Criteria.where("_id").is(checkingLog.getUserStatusId()));
				UserStatus userStatus = mongoTemplate.findOne(queryStatusUser, UserStatus.class);
				UserCheckInOutInfo userCheckInOutInfo = new UserCheckInOutInfo();
				userCheckInOutInfo.setCheckinTime(checkIn != null ? checkIn.getTime() : "");
				userCheckInOutInfo.setDateCheckin(checkIn != null ? checkIn.getDate() : "");
				userCheckInOutInfo.setCheckoutTime(checkOut != null ? checkOut.getTime() : "");
				userCheckInOutInfo.setDateCheckOut(checkOut != null ? checkOut.getDate() : "");
				userCheckInOutInfo.setGroupUserId(groupUser != null ? groupUser.getGroupId() : -1);
				userCheckInOutInfo.setGroupName(groupUser != null ? groupUser.getGroupName() : "");
				userCheckInOutInfo.setTypeUserId(typeUser != null ? typeUser.getTypeId() : -1);
				userCheckInOutInfo.setId(checkingLog.getId().toString());
				userCheckInOutInfo.setImage(Common.BASE_URL_IMG.concat("/").concat(checkingLog.getImage()));
				userCheckInOutInfo.setUserStatusId(userStatus != null ? userStatus.getStatusId() : -1);
				userCheckInOutInfo.setName(checkingLog.getName());
				userCheckInOutInfos.add(userCheckInOutInfo);
			}
		}


        output = userCheckInOutInfos.stream().filter(u ->(userIdTemp == null || u.getId().equalsIgnoreCase(userIdTemp)))
                .filter(n ->(userNameTemp == null || n.getName().replaceAll("\\s+", " ")
                        .toUpperCase(Locale.ROOT).trim().contains(userNameTemp.replaceAll("\\s+", " ").toUpperCase().trim())))
                .filter(g ->(userBuilder.getGroupId() == null || g.getGroupUserId().equals(userBuilder.getGroupId())))
                .filter(d -> {
                    if (startDateTemp != null && endDateTemp != null) {
                        try {
                            Date checkinDate = null;
                            Date checkoutDate = null;
                            if (d.getDateCheckin() != null) {
                                checkinDate = DATE_FORMAT.parse(d.getDateCheckin());
                            }
                            if (d.getDateCheckOut() != null) {
                                checkoutDate = DATE_FORMAT.parse(d.getDateCheckOut());
                            }

							Date fromDateObj = DATE_FORMAT.parse(startDateTemp);
							Date toDateObj = DATE_FORMAT.parse(endDateTemp);

							return (checkinDate != null && checkinDate.compareTo(fromDateObj) >= 0
									&& checkinDate.compareTo(toDateObj) <= 0)
									|| (checkoutDate != null && checkoutDate.compareTo(fromDateObj) >= 0
											&& checkoutDate.compareTo(toDateObj) <= 0);
						} catch (ParseException e) {
							return false;
						}
					}
					return true;
				})
				.filter(s -> (userBuilder.getStatus() == null
						|| Objects.equals(s.getUserStatusId(), userBuilder.getStatus())))
				.filter(t -> (todayTemp == null || t.getDateCheckin().equals(todayTemp)
						|| t.getDateCheckOut().equals(todayTemp)))
				.sorted((user1, user2) -> {
					Date dateCheckIn1 = parseDate(user1.getDateCheckin(), user1.getCheckinTime());
					Date dateCheckIn2 = parseDate(user2.getDateCheckin(), user2.getCheckinTime());
					Date dateCheckOut1 = parseDate(user1.getDateCheckOut(), user1.getCheckoutTime());
					Date dateCheckOut2 = parseDate(user2.getDateCheckOut(), user2.getCheckoutTime());

					Date maxDate1 = getMaxDate(dateCheckIn1, dateCheckOut1);
					Date maxDate2 = getMaxDate(dateCheckIn2, dateCheckOut2);

					return maxDate2.compareTo(maxDate1);
				}).collect(Collectors.toList());
		return output;
	}

	private static void sortTime(List<CheckInOutDto> checkInOuts) {
		checkInOuts.sort(new Comparator<CheckInOutDto>() {
			@Override
			public int compare(CheckInOutDto o1, CheckInOutDto o2) {
				try {
					Date dateTime1 = SIMPLE_DATE_TIME_FORMAT.parse(o1.getDate() + " " + o1.getTime());
					Date dateTime2 = SIMPLE_DATE_TIME_FORMAT.parse(o2.getDate() + " " + o2.getTime());
					return dateTime1.compareTo(dateTime2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}

	private static Date parseDate(String dateString, String timeString) {
		if (dateString == null || dateString.isEmpty() || timeString == null || timeString.isEmpty()) {
			return null;
		}
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateTimeString = dateString + " " + timeString;
		try {
			return dateFormat.parse(dateTimeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Date getMaxDate(Date date1, Date date2) {
		if (date1 == null) {
			return date2;
		} else if (date2 == null) {
			return date1;
		} else {
			return date1.after(date2) ? date1 : date2;
		}
	}

	private void populateUserCheckInOutInfo(CheckInOutDto checkInOut, List<UserCheckInOutInfo> userCheckInOutInfos) {
		Query queryUser = new Query(Criteria.where("_id").is(checkInOut.getUserId()));
		User user = mongoTemplate.findOne(queryUser, User.class);

		if (user == null)
			return;

		GroupUser groupUser = getGroupUser(user.getGroupUserId());
		TypeUser typeUser = getTypeUser(user.getTypeUserId());

		UserCheckInOutInfo inOutInfo = new UserCheckInOutInfo();
		inOutInfo.setId(checkInOut.getUserId());
		inOutInfo.setName(checkInOut.getName());
		inOutInfo.setImage(Common.BASE_URL_IMG.concat("/").concat(user.getImage() != null ? user.getImage() : ""));
		inOutInfo.setGroupUserId(groupUser != null ? groupUser.getGroupId() : null);
		inOutInfo.setGroupName(groupUser != null ? groupUser.getGroupName() : "");
		inOutInfo.setTypeUserId(typeUser != null ? typeUser.getTypeId() : null);
		if (checkInOut.getCamId().equals(Common.CHECK_IN)) {
			inOutInfo.setCheckinTime(checkInOut.getTime());
			inOutInfo.setDateCheckin(checkInOut.getDate());
		} else {
			inOutInfo.setCheckoutTime(checkInOut.getTime());
			inOutInfo.setDateCheckOut(checkInOut.getDate());
		}
		userCheckInOutInfos.add(inOutInfo);
	}

	// Helper method to retrieve GroupUser object
	private GroupUser getGroupUser(ObjectId groupUserId) {
		if (groupUserId == null)
			return null;

		Query queryGroup = new Query(Criteria.where("_id").is(groupUserId));
		return mongoTemplate.findOne(queryGroup, GroupUser.class);
	}

	// Helper method to retrieve TypeUser object
	private TypeUser getTypeUser(ObjectId typeUserId) {
		if (typeUserId == null)
			return null;

		Query queryTypeUser = new Query(Criteria.where("_id").is(typeUserId));
		return mongoTemplate.findOne(queryTypeUser, TypeUser.class);
	}

	@Override
	public void exportExcel(HttpServletResponse response) throws IOException, ParseException {
		// Lấy dữ liệu
		List<UserCheckInOutInfo> data = getUsersWithCheckinAndCheckoutInfo(new UserBuilder()).stream()
				.sorted(Comparator.comparingInt(UserCheckInOutInfo::getGroupUserId)).collect(Collectors.toList());
		;
		// Tạo workbook Excel
		Workbook workbook = new XSSFWorkbook();

		Map<Integer, String> userStatusMap = new HashMap<>();
		userStatusMap.put(-1, StatusUser.NOT_CHECKIN_OUT.getDescription());
		userStatusMap.put(0, StatusUser.CHECK_OUT.getDescription());
		userStatusMap.put(1, StatusUser.NOT_CHECKIN_OUT.getDescription());

		// Tạo các sheet dựa trên groupUserId
		TreeMap<Integer, Sheet> sheetMap = new TreeMap<>();
		for (UserCheckInOutInfo userCheckInOutInfo : data) {
			int groupUserId = userCheckInOutInfo.getGroupUserId();
			Sheet sheet = sheetMap.get(groupUserId);
			if (sheet == null) {
				sheet = workbook.createSheet("Tổ " + groupUserId);
				sheetMap.put(groupUserId, sheet);

				// Tạo header cho sheet
				Row headerRow = sheet.createRow(0);
				String[] columns = { "ID", "Tên", "Thời gian vào", "Thời gian ra", "Tổ" };
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				Font headerFont = workbook.createFont();
				headerFont.setColor(IndexedColors.WHITE.getIndex());
				headerCellStyle.setFont(headerFont);
				for (int i = 0; i < columns.length; i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columns[i]);
					cell.setCellStyle(headerCellStyle);
					sheet.autoSizeColumn(i);
				}
			}

			// Tạo row mới cho người dùng
			int rowNum = sheet.getLastRowNum() + 1;
			Row row = sheet.createRow(rowNum);
			row.createCell(0).setCellValue(userCheckInOutInfo.getId());
			row.createCell(1).setCellValue(userCheckInOutInfo.getName());
			row.createCell(2).setCellValue(Objects.nonNull(userCheckInOutInfo.getCheckinTime())
					? userCheckInOutInfo.getCheckinTime().concat(" ").concat(userCheckInOutInfo.getDateCheckin())
					: "");
			row.createCell(3).setCellValue(Objects.nonNull(userCheckInOutInfo.getCheckoutTime())
					? userCheckInOutInfo.getCheckoutTime().concat(" ").concat(userCheckInOutInfo.getDateCheckOut())
					: "");
			row.createCell(4).setCellValue(userCheckInOutInfo.getGroupUserId());

//            String userStatus = (!userCheckInOutInfo.getDateCheckin().equals("") && !userCheckInOutInfo.getDateCheckOut().equals(""))
//                    ? (LocalDate.parse(userCheckInOutInfo.getDateCheckin(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
//                    .isBefore(LocalDate.parse(userCheckInOutInfo.getDateCheckOut(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
//                    ? "Chưa về"
//                    : "Đã về"
//                    : (!userCheckInOutInfo.getDateCheckin().equals(""))
//                    ? "Đã về"
//                    : (!userCheckInOutInfo.getDateCheckOut().equals(""))
//                    ? "Chưa về"
//                    : "Chưa điểm danh";
//            row.createCell(5).setCellValue(userStatus);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				sheet.autoSizeColumn(i);
			}
			for (Cell cell : row) {
				CellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderBottom(BorderStyle.THIN);
				cellStyle.setBorderTop(BorderStyle.THIN);
				cellStyle.setBorderLeft(BorderStyle.THIN);
				cellStyle.setBorderRight(BorderStyle.THIN);
				cell.setCellStyle(cellStyle);
			}
		}

		// Thiết lập response header
		response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		// Ghi workbook vào response OutputStream
		OutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

	@Override
	public List<LatestUserInCamera> findLatestUserInCamera(String userId, String userName, Integer groupUserId) {
		String userIdtmp = Objects.nonNull(userId) ? userId : "";
		userName = Objects.nonNull(userName) ? userName : "";
		LatestUserInCamera latestUserInCamera = new LatestUserInCamera();
		List<LatestUserInCamera> latestUserInCameras = new ArrayList<>();
		if (!userIdtmp.isEmpty() && userName.isEmpty()) {
			Query query = new Query(Criteria.where("user_id").is(userIdtmp))
					.with(Sort.by(Sort.Direction.DESC, "date", "time"));

			List<Attendances> attendancesList = mongoTemplate.find(query, Attendances.class);
			Attendances attendances = sortUser(attendancesList,
					ci -> LocalDateTime.parse(ci.getDate() + " " + ci.getTime(), DATE_TIME_FORMATTER), true);
			if (Objects.nonNull(attendances)) {
				OutputData(attendances, latestUserInCamera);
				latestUserInCameras.add(latestUserInCamera);
			}
		}
		if (!userName.isEmpty()) {
			Query query = new Query();
			query.addCriteria(Criteria.where("name").regex(userName, "i"));
			query.with(Sort.by(Sort.Direction.DESC, "date", "time"));
			List<Attendances> attendances = mongoTemplate.find(query, Attendances.class, "attendances");
			attendances.sort(new Comparator<Attendances>() {
				@Override
				public int compare(Attendances o1, Attendances o2) {
					try {
						Date dateTime1 = DATE_FORMAT.parse(o1.getDate() + " " + o1.getTime());
						Date dateTime2 = DATE_FORMAT.parse(o2.getDate() + " " + o2.getTime());
						return dateTime2.compareTo(dateTime1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return 0;
				}
			});
			Set<String> processedUserIds = new HashSet<>();
			List<Attendances> uniqueRecords = new ArrayList<>();
			for (Attendances a : attendances) {
				if (!processedUserIds.contains(a.getUserId())) {
					uniqueRecords.add(a);
					processedUserIds.add(a.getUserId());
				}
			}
			for (Attendances userLatest : uniqueRecords) {
				LatestUserInCamera latestUserInCam = new LatestUserInCamera();
				OutputData(userLatest, latestUserInCam);
				latestUserInCameras.add(latestUserInCam);
			}
			if (!userIdtmp.isEmpty()) {
				Optional<LatestUserInCamera> userInCameraOptional = latestUserInCameras.stream()
						.filter(x -> x.getId().equals(userIdtmp)).findFirst();
				latestUserInCameras = userInCameraOptional.map(List::of).orElse(Collections.emptyList());
			}
			if (Objects.nonNull(groupUserId)) {
				Optional<LatestUserInCamera> userInCameraOptional = latestUserInCameras.stream()
						.filter(x -> x.getGroupUserId().equals(groupUserId)).findFirst();
				latestUserInCameras = userInCameraOptional.map(List::of).orElse(Collections.emptyList());
			}
		}
		return latestUserInCameras;
	}

	@Override
	public LatestUserInCamera detailAttendancesUser(String idRecord) {
		Query queryUser = new Query(Criteria.where("_id").is(idRecord));
		Attendances attendances = mongoTemplate.findOne(queryUser, Attendances.class, "attendances");
		LatestUserInCamera latestUserInCamera = new LatestUserInCamera();
		if (Objects.nonNull(attendances)) {
			OutputData(attendances, latestUserInCamera);
		}
		return latestUserInCamera;
	}

	private void OutputData(Attendances userLatest, LatestUserInCamera latestUserInCam) {
		latestUserInCam.setId(userLatest.getUserId());
		latestUserInCam.setIdRecord(userLatest.getId().toString());
		latestUserInCam.setName(userLatest.getName());
		latestUserInCam.setFaceUrl(Common.BASE_URL_IMG.concat("/").concat(userLatest.getFaceImg()));
		Query queryUser = new Query(Criteria.where("_id").is(userLatest.getUserId()));
		User user = mongoTemplate.findOne(queryUser, User.class);
		GroupUser groupUser = null;
		TypeUser typeUser = null;
		if (Objects.nonNull(user)) {
			Query queryGroup = new Query(Criteria.where("_id").is(user.getGroupUserId()));
			groupUser = mongoTemplate.findOne(queryGroup, GroupUser.class);
			latestUserInCam.setImage(Common.BASE_URL_IMG.concat("/").concat(user.getImage()));
			Query queryTypeUser = new Query(Criteria.where("_id").is(user.getTypeUserId()));
			typeUser = mongoTemplate.findOne(queryTypeUser, TypeUser.class);
			latestUserInCam.setTypeUserId(Objects.nonNull(typeUser) ? typeUser.getTypeId() : null);
		}
		latestUserInCam.setGroupUserId(Objects.nonNull(groupUser) ? groupUser.getGroupId() : null);
		latestUserInCam.setGroupName(Objects.nonNull(groupUser) ? groupUser.getGroupName() : null);
		latestUserInCam.setTime(userLatest.getTime());
		latestUserInCam.setDate(userLatest.getDate());
		latestUserInCam.setCamId(userLatest.getCamId());
	}

	public <T> T sortUser(List<T> lists, Function<T, LocalDateTime> dateTimeExtractor, boolean isMax) {
		Comparator<T> comparator = isMax ? Comparator.comparing(dateTimeExtractor)
				: Comparator.comparing(dateTimeExtractor).reversed();
		T lastUser = lists.stream().max(comparator).orElse(null);
		return lastUser;
	}

	@Override
	public List<UserClearInfo> getAllUser(){
		UserClearInfo user;
		List<UserClearInfo> listUserInfo = new ArrayList<>();
		List<User> listUser = userRepository.findAll();
		try {
			for (User u : listUser) {
				user = new UserClearInfo();
				user.setId(u.getId().toString());
				user.setName(u.getName());
				user.setImage(Common.BASE_URL_IMG.concat("/").concat(u.getImage()));
				user.setBirthday(u.getBirthday());
				user.setAddress(u.getAddress());
				user.setGroupUser(getUserByGroupId(u.getGroupUserId().toString()));
				user.setTypeUser(getTypeUserDesctiption(u.getTypeUserId().toString()));
				listUserInfo.add(user);
			}
		} catch (Exception e) {
			throw new HtscException(HttpStatus.BAD_REQUEST.value(), "Error to get paginated users");
		}
		return listUserInfo;
	}

	@Override
	public String getUserByGroupId(String id) {
		try {
			Query query = Query.query(Criteria.where("id").is(new ObjectId(id)));
			return mongoTemplate.findOne(query, GroupUser.class).getGroupName();
		} catch (Exception e) {
			throw new HtscException(HttpStatus.BAD_REQUEST.value(),"Invalid user group type!");
		}
	}

	@Override
	public int getUserByGroupTypeId(String id) {
		try {
			Query query = Query.query(Criteria.where("id").is(new ObjectId(id)));
			return mongoTemplate.findOne(query, TypeUser.class).getTypeId();
		} catch (Exception e) {
			throw new HtscException(HttpStatus.BAD_REQUEST.value(),"Invalid User Group Id!");
		}
	}

	@Override
	public String getTypeUserDesctiption(String id){
		try {
			Query query = Query.query(Criteria.where("typeId").is(new ObjectId(id)));
			return mongoTemplate.findOne(query, TypeUserDescription.class).getDescription();
		} catch (Exception e) {
			throw new HtscException(HttpStatus.BAD_REQUEST.value(),"Invalid User type!");
		}
	}

	@Override
	public Page<UserClearInfo> paginationUserClearInfo(int pageNumber, int sizePage) {
		UserClearInfo user;
		Pageable pageable = PageRequest.of(pageNumber, sizePage);
		List<UserClearInfo> listUserInfo = new ArrayList<>();
		Page<User> pageResult = userRepository.findAll(pageable);
		List<User> listUser = pageResult.getContent();
		for (User u : listUser) {
			user = new UserClearInfo();
			user.setId(u.getId().toString());
			user.setName(u.getName());
			user.setImage(Common.BASE_URL_IMG.concat("/").concat(u.getImage()));
			user.setBirthday(u.getBirthday());
			user.setAddress(u.getAddress());
			user.setGroupUser(getUserByGroupId(u.getGroupUserId().toString()));
			user.setTypeUser(getTypeUserDesctiption(u.getTypeUserId().toString()));
			listUserInfo.add(user);
		}
		long total = pageResult.getTotalElements();
		return new PageImpl<UserClearInfo>(listUserInfo, pageable, total);
	}

	@Override
	public void createUser(UserInput userInput) throws Exception {
		try {
			GroupUser groupUser = getGroupUser(userInput);
			TypeUser typeUser = getTypeUser(userInput);
			Query query = Query.query(
					Criteria.where("group_user_id").is(groupUser.getId()).and("type_user_id").is(typeUser.getId()));
			long count = mongoTemplate.count(query, User.class);
			if (count > 0 && userInput.getTypeUser() != 0) {
				throw new HtscException(HttpStatus.BAD_REQUEST.value(), String.format(
						"GroupId %d và TypeUser %d already exist", userInput.getGroupId(), userInput.getTypeUser()));
			}
			List<Double> features = getFeatures(userInput);
			User user = new User();
			user.setName(userInput.getUserName());
			user.setImage(userInput.getImage().getResource().getFilename());
			user.setBirthday(userInput.getBirthday());
			user.setAddress(userInput.getAddress());
			user.setFeature(features);
			user.setTypeUserId(typeUser.getId());
			user.setGroupUserId(groupUser.getId());
			user.setUserStatusId(StatusUser.NOT_CHECKIN_OUT.getObjectId());
			mongoTemplate.save(user);
			// save user to redis
			CheckingLogDTORedis checkingLogDTORedis = new CheckingLogDTORedis();
			checkingLogDTORedis.setId(user.getId().toString());
			checkingLogDTORedis.setName(user.getName());
			checkingLogDTORedis.setImage(user.getImage());
			checkingLogDTORedis.setGroupUserId(groupUser.getGroupId());
			checkingLogDTORedis.setGroupUserName(groupUser.getGroupName());
			checkingLogDTORedis.setTypeUserId(typeUser.getTypeId());
			checkingLogDTORedis.setUserStatus(StatusUser.NOT_CHECKIN_OUT.getId());
			inOutRedisService.saveOrUpdateUser(checkingLogDTORedis);
		} catch (Exception e) {
			throw new HtscException(HttpStatus.BAD_REQUEST.value(), "Save data fail.");
		}

	}

	private List<Double> getFeatures(UserInput userInput) {
		ResponseEntity<UsersFeignClientOutput> usersFeignClientOutput = usersFeignClientService
				.getFeatureUser(userInput.getImage());
		if (!usersFeignClientOutput.getStatusCode().is2xxSuccessful()) {
			throw new HtscException(HttpStatus.BAD_REQUEST.value(), "Call feignClient fail!");
		}

		List<Double> features = Optional.ofNullable(usersFeignClientOutput.getBody())
				.map(UsersFeignClientOutput::getFeature).filter(feature -> !feature.isEmpty())
				.orElseThrow(() -> new HtscException(HttpStatus.BAD_REQUEST.value(), "Can't find face in photo"));
		return features;
	}

	private GroupUser getGroupUser(UserInput userInput) {
		Query query = new Query(Criteria.where("group_id").is(userInput.getGroupId()));
		return mongoTemplate.findOne(query, GroupUser.class);
	}

	private TypeUser getTypeUser(UserInput userInput) {

		Query query = new Query(Criteria.where("typeId").is(userInput.getTypeUser()));

		return mongoTemplate.findOne(query, TypeUser.class);
	}

	@Override
	public void deleteUser(String userId) {
		User user;
		try {
			user = Optional.ofNullable(mongoTemplate.findById(new ObjectId(userId), User.class))
					.orElseThrow(() -> new HtscException(HttpStatus.NOT_FOUND.value(), "user not found"));
			mongoTemplate.remove(user);
			// xóa cache redis
			inOutRedisService.deleteUserById(userId);
		} catch (IllegalArgumentException e) {
			throw new HtscException(HttpStatus.BAD_REQUEST.value(), "userId format invalid");
		}
	}

    @Override
    public void updateUser(UserInput userInput) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.typeMap(UserInput.class, User.class)
                .addMappings(mapper -> mapper.skip(User::setImage));
        User user;
        try {
            user = Optional.ofNullable(mongoTemplate.findById(new ObjectId(userInput.getUserId()),User.class))
                    .orElseThrow(() ->new HtscException(HttpStatus.NOT_FOUND.value(), "user not found"));
            if (Objects.nonNull(userInput.getTypeUser())) {
                user.setTypeUserId(getTypeUser(userInput).getId());
            }
            if (Objects.nonNull(userInput.getGroupId())) {
                user.setGroupUserId(getGroupUser(userInput).getId());
            }
            user.setName(userInput.getUserName());
            user.setBirthday(userInput.getBirthday());
            user.setAddress(userInput.getAddress());
            if (Objects.nonNull(userInput.getImage())) {
                user.setImage(userInput.getImage().getResource().getFilename());
                user.setFeature(getFeatures(userInput));
            }
            modelMapper.map(userInput, user);
            mongoTemplate.save(user);
        }catch (IllegalArgumentException e) {
            throw new HtscException(HttpStatus.BAD_REQUEST.value(), "userId format invalid");
        }
    }

	@Override
	public UserOutput userDetail(String userId) {
		User user;
		try {
			user = Optional.ofNullable(mongoTemplate.findById(new ObjectId(userId), User.class))
					.orElseThrow(() -> new HtscException(HttpStatus.NOT_FOUND.value(), "user not found"));
			UserOutput userOutput = new UserOutput();
			userOutput.setId(user.getId().toString());
			userOutput.setName(user.getName());
			userOutput.setImage(Common.BASE_URL_IMG.concat("/").concat(user.getImage()));
			userOutput.setBirthday(user.getBirthday());
			userOutput.setAddress(user.getAddress());
			TypeUser typeUser = getTypeUser(user.getTypeUserId());
			if (Objects.nonNull(typeUser)) {
				userOutput.setTypeUserId(typeUser.getTypeId());
			}
			GroupUser groupUser = getGroupUser(user.getGroupUserId());
			if (Objects.nonNull(groupUser)) {
				userOutput.setGroupUserId(groupUser.getGroupId());
			}
			return userOutput;
		} catch (IllegalArgumentException e) {
			throw new HtscException(HttpStatus.BAD_REQUEST.value(), "userId format invalid");
		}
	}
}