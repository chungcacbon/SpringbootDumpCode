package com.htsc.vn.demo.PrisonManagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class CheckInOutDto {

    @Id
    @Field("_id")
    private ObjectId id;
    @Field("user_id")
    private String userId;
    private String time;
    private String date;
    @Field("cam_id")
    private Integer camId;
    @Field("face_img")
    private String faceImg;
    private String name;
    private int type;
}
