package com.htsc.vn.demo.PrisonManagement.service;

import com.htsc.vn.demo.PrisonManagement.Entity.input.UserInput;
import com.htsc.vn.demo.PrisonManagement.Entity.output.UserOutput;
import com.htsc.vn.demo.PrisonManagement.dto.UserBuilder;
import com.htsc.vn.demo.PrisonManagement.dto.UserCheckInOutInfo;
import com.htsc.vn.demo.PrisonManagement.model.LatestUserInCamera;
import com.htsc.vn.demo.PrisonManagement.model.User;
import com.htsc.vn.demo.PrisonManagement.model.UserClearInfo;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
public interface UserService {

     List<UserCheckInOutInfo> getUsersWithCheckinAndCheckoutInfo(UserBuilder userBuilder) throws ParseException;

    void exportExcel(HttpServletResponse response) throws IOException, ParseException;

    List<LatestUserInCamera> findLatestUserInCamera(String userId, String userName,Integer groupUserId);

    LatestUserInCamera detailAttendancesUser(String idRecord);


    List<UserClearInfo> getAllUser();
    
    String getUserByGroupId(String id);
    
    int getUserByGroupTypeId(String id);
    
    String getTypeUserDesctiption(String id);
    
    Page<UserClearInfo> paginationUserClearInfo(int size, int page);
    void createUser(UserInput userInput) throws Exception;
    void deleteUser(String userId);
    void updateUser(UserInput userInput);
    UserOutput userDetail(String userId);
}
