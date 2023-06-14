package com.htsc.vn.demo.PrisonManagement.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "attendances")
public class Attendances {

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
    @Field("name")
    private String name;

}
