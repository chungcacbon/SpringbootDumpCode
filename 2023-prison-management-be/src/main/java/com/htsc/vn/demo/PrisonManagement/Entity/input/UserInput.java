package com.htsc.vn.demo.PrisonManagement.Entity.input;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserInput {

    private String userId;
    @NotEmpty(message = "userName not null")
    private String userName;
    @NotNull(message = "file not null")
    private MultipartFile image;
    @NotEmpty(message = "birthday not null")
    private String birthday;
    @NotNull(message = "groupId not null")
    private Integer groupId;
    @NotNull(message = "typeUser not null")
    private Integer typeUser;
    @NotEmpty(message = "address not null")
    private String address;
}
