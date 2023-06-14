package com.htsc.vn.demo.PrisonManagement.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "users")
public class User {

    @Id
    @Field("_id")
    private ObjectId id;
    @Field("name")
    private String name;
    @Field("image")
    private String image;
    @Field("birthday")
    private String birthday;
    @Field("address")
    private String address;
    @Field("feature")
    private List<Double> feature;
    @Field("group_user_id")
    private ObjectId groupUserId;
    @Field("type_user_id")
    private ObjectId typeUserId;
    @Field("users_status_id")
    private ObjectId userStatusId;
}
