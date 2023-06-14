package com.htsc.vn.demo.PrisonManagement.redis.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

//@RedisHash("CheckingLogDTORedis")
@Data
public class CheckingLogDTORedis implements Serializable {

    @Id
    private String id;
    private String name;
    private String image;
//    private String birthday;
//    private String address;
//    private List<Double> feature;
    private Integer groupUserId;
    private String groupUserName;
    private Integer typeUserId;
    private Integer userStatus;
    private String time;
    private String faceUrl;
}
