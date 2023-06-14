package com.htsc.vn.demo.PrisonManagement.controller;

import com.htsc.vn.demo.PrisonManagement.model.CheckIn;
import com.htsc.vn.demo.PrisonManagement.service.CheckInService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkin")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @GetMapping("")
    public List<CheckIn> findAllCheckIn() {
        return checkInService.findAll();
    }
}
