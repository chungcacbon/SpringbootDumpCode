package com.htsc.vn.demo.PrisonManagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class LatestUserInCamera {

    private String idRecord;
    private String id;
    private String name;
    private String time;
    private String date;
    private String faceUrl;
    private Integer groupUserId;
    private String groupName;
    private Integer typeUserId;
    private String image;
    private Integer camId;
}
