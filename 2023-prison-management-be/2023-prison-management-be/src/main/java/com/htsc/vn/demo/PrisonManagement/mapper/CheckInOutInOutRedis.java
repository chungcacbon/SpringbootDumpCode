package com.htsc.vn.demo.PrisonManagement.mapper;

import com.htsc.vn.demo.PrisonManagement.dto.CheckingLogDTO;
import com.htsc.vn.demo.PrisonManagement.model.CheckIn;
import com.htsc.vn.demo.PrisonManagement.model.CheckOut;
import com.htsc.vn.demo.PrisonManagement.redis.model.CheckingLogDTORedis;
import com.htsc.vn.demo.PrisonManagement.redis.model.InOutRedis;

public class CheckInOutInOutRedis {

    public static CheckingLogDTORedis fromAI(CheckingLogDTO c) {
        CheckingLogDTORedis checkingLogDTORedis = new CheckingLogDTORedis();
        checkingLogDTORedis.setId(c.getId());
        checkingLogDTORedis.setName(c.getName());
        checkingLogDTORedis.setImage(c.getImage());
//        checkingLogDTORedis.setBirthday(c.getBirthday());
//        checkingLogDTORedis.setAddress(c.getAddress());
//        checkingLogDTORedis.setFeature(c.getFeature());
        checkingLogDTORedis.setGroupUserId(c.getGroupUserId());
        checkingLogDTORedis.setGroupUserName(c.getGroupUserName());
        checkingLogDTORedis.setTypeUserId(c.getTypeUserId());
        checkingLogDTORedis.setUserStatus(c.getUserStatus());
        checkingLogDTORedis.setTime(c.getTime());
        checkingLogDTORedis.setFaceUrl(c.getFaceUrl());
        return checkingLogDTORedis;
    }
}

