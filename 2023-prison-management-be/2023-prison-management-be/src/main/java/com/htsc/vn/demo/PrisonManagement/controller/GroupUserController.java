package com.htsc.vn.demo.PrisonManagement.controller;

import com.htsc.vn.demo.PrisonManagement.model.GroupUser;
import com.htsc.vn.demo.PrisonManagement.service.GroupUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
@CrossOrigin
public class GroupUserController {

    @Autowired
    GroupUserService groupUserService;

    @GetMapping("/list")
    public ResponseEntity<List<GroupUser>> getAllGroupUser() {
        return ResponseEntity.ok(groupUserService.findAll());
    }

}
