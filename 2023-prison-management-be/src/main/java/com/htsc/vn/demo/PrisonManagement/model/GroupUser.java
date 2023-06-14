package com.htsc.vn.demo.PrisonManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "group_user")
@AllArgsConstructor
@NoArgsConstructor
public class GroupUser {

    @Id
    @Field("_id")
    private ObjectId id;
    @Field("group_id")
    private Integer groupId;
    @Field("group_name")
    private String groupName;
}
