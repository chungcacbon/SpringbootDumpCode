package com.htsc.vn.demo.PrisonManagement.redis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("InOutRedis")
@Data
public class InOutRedis implements Serializable {

    @Id
    private ObjectId id;
    private String userId;
    private String time;
    private Integer camId;
    private String faceUrl;
    private String name;

}
