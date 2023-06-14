package com.htsc.vn.demo.PrisonManagement.controller;


import com.htsc.vn.demo.PrisonManagement.Entity.input.UserInput;
import com.htsc.vn.demo.PrisonManagement.dto.UserBuilder;
import com.htsc.vn.demo.PrisonManagement.error.ErrorMessage;
import com.htsc.vn.demo.PrisonManagement.exception.HtscException;
import com.htsc.vn.demo.PrisonManagement.model.PaginatedUserResponse;
import com.htsc.vn.demo.PrisonManagement.model.StepExportRequest;
import com.htsc.vn.demo.PrisonManagement.model.UserClearInfo;
import com.htsc.vn.demo.PrisonManagement.service.UserService;

import org.apache.poi.hpsf.Array;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseEntity<?> listUser(@RequestParam(required = false) Integer groupId,
                                      @RequestParam(required = false) String userId,
                                      @RequestParam(required = false) String userName,
                                      @RequestParam(required = false) String startDate,
                                      @RequestParam(required = false) String endDate,
                                      @RequestParam(required = false) Integer status,
                                      @RequestParam(required = false) String today) throws ParseException {
        if (startDate == null ^ endDate == null) {
            ErrorMessage errorMessage = new ErrorMessage(400, "startDate and endDate must be both null or both not null.");
            return ResponseEntity.badRequest().body(errorMessage);
        }
        UserBuilder userBuilder = new UserBuilder()
                .withUserId(userId)
                .withUserName(userName)
                .withStartDate(startDate)
                .withGroupId(groupId)
                .withEndDate(endDate)
                .withStatus(status)
                .withToday(today);
        return ResponseEntity.ok(userService.getUsersWithCheckinAndCheckoutInfo(userBuilder));
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) throws IOException, ParseException {
        userService.exportExcel(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<?> checkUser(@RequestParam(required = false) String userId,
                                       @RequestParam(required = false) String userName,
                                       @RequestParam(required = false) Integer groupId) {
        return ResponseEntity.ok(userService.findLatestUserInCamera(userId, userName, groupId));
    }

    @GetMapping("/attendances/detail")
    public ResponseEntity<?> detailAttendancesUser(@RequestParam(required = false) String idRecord) {
        return ResponseEntity.ok(userService.detailAttendancesUser(idRecord));
    }
    
    @GetMapping("/getAllUser")
    public ResponseEntity<?> getAllUser(@RequestParam(required = false)Integer pageSize, @RequestParam(required = false)Integer pageNumber) {
        try {
            if (pageSize != null && pageNumber != null) {
                Page<UserClearInfo> resultPage = userService.paginationUserClearInfo((pageNumber - 1), pageSize);
                PaginatedUserResponse response = new PaginatedUserResponse();
                response.setContent(resultPage.getContent());
                response.setTotalElements(resultPage.getTotalElements());
                response.setTotalPages(resultPage.getTotalPages()-1);
                response.setPageNumber(resultPage.getNumber());
                response.setPageSize(resultPage.getSize());
                
                return ResponseEntity.ok(response);
            } else           	
                return ResponseEntity.ok(userService.getAllUser());
        } catch (Exception e) {
        	throw new HtscException(HttpStatus.BAD_REQUEST.value(),"Error to get users");
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @ModelAttribute UserInput userInput) throws Exception {
        userService.createUser(userInput);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorMessage(HttpStatus.OK.value(),"save user successfully!"));
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@Valid @RequestParam(required = true) String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorMessage(HttpStatus.OK.value(),"delete user successfully!"));
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(UserInput userInput) {
        userService.updateUser(userInput);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorMessage(HttpStatus.OK.value(),"save user successfully!"));
    }
    @GetMapping("/detail")
    public ResponseEntity<?> detailUser(@RequestParam(required = false) String userId) {
        return ResponseEntity.ok(userService.userDetail(userId));
    }
    @GetMapping("/ccccccccccc")
    public ResponseEntity<?> cccccccccccccc(@RequestParam(required = false) String userId) {
        return ResponseEntity.ok(userService.userDetail(userId));
    }
    int abc = 5;
    

}
