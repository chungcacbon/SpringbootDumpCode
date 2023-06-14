package com.htsc.vn.demo.PrisonManagement.service;

import com.htsc.vn.demo.PrisonManagement.model.GroupUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class GroupUserServiceImpl implements GroupUserService{

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<GroupUser> findAll() {
        List<GroupUser> groupUsers = mongoTemplate.findAll(GroupUser.class);
        return mongoTemplate.findAll(GroupUser.class);
    }
}
