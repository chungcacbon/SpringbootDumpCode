package com.htsc.vn.demo.PrisonManagement.controller;

import com.htsc.vn.demo.PrisonManagement.model.CheckOut;
import com.htsc.vn.demo.PrisonManagement.service.CheckOutService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkout")
public class CheckOutController {


    private final CheckOutService checkOutService;

    public CheckOutController(CheckOutService checkOutService) {
        this.checkOutService = checkOutService;
    }

    @GetMapping("")
    public List<CheckOut> findAllCheckIn() {
        return checkOutService.findAll();
    }

}
