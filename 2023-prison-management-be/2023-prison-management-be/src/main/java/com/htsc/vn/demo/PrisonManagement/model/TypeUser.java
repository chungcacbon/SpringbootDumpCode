package com.htsc.vn.demo.PrisonManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "type_user")
@AllArgsConstructor
@NoArgsConstructor
public class TypeUser {

    @Id
    @Field("_id")
    private ObjectId id;
    @Field("typeId")
    private Integer typeId;
}
