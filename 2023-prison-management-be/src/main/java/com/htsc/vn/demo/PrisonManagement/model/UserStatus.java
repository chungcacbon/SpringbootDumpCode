package com.htsc.vn.demo.PrisonManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "users_status")
@AllArgsConstructor
@NoArgsConstructor
public class UserStatus {
    @Id
    @Field("_id")
    private ObjectId id;
    @Field("statusId")
    private Integer statusId;

}
