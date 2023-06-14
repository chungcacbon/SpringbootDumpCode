package com.htsc.vn.demo.PrisonManagement.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "type_user_description")
@AllArgsConstructor
@NoArgsConstructor
public class TypeUserDescription {
	@Id
    @Field("_id")
	private ObjectId id;
	
	@Field("type_user_id")
	private ObjectId typeId;
	
	@Field("description")
	private String description;
}
