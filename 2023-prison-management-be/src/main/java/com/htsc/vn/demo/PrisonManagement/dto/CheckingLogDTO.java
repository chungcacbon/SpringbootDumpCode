package com.htsc.vn.demo.PrisonManagement.dto;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class CheckingLogDTO {

    private String id;
    private String name;
    private String image;
    private Integer groupUserId;
    private String groupUserName;
    private Integer typeUserId;
    private Integer userStatus;
    private String time;
    private String faceUrl;

}
