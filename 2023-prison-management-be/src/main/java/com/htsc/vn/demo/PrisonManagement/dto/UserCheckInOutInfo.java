package com.htsc.vn.demo.PrisonManagement.dto;

import com.htsc.vn.demo.PrisonManagement.model.User;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
public class UserCheckInOutInfo {


    private String id;
    private String name;
    private String image;
    private Integer groupUserId;
    private String groupName;
    private Integer typeUserId;
    private Integer userStatusId;
    private String checkinTime;
    private String dateCheckin;
    private String checkoutTime;
    private String dateCheckOut;
}
