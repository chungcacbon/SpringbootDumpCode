package com.htsc.vn.demo.PrisonManagement.Entity.output;

import lombok.Data;
@Data
public class UserOutput {

    private String id;
    private String name;
    private String image;
    private String birthday;
    private String address;
    private Integer groupUserId;
    private Integer typeUserId;
}
