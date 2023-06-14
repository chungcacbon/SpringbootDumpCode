package com.htsc.vn.demo.PrisonManagement.model;

import java.util.List;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserClearInfo {

	    private String id;

	    private String name;

	    private String image;

	    private String birthday;

	    private String address;

	    private String groupUser;

	    private String typeUser;

}
