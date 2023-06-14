package com.htsc.vn.demo.PrisonManagement.model;

import com.htsc.vn.demo.PrisonManagement.dto.CheckingLogDTO;
import com.htsc.vn.demo.PrisonManagement.redis.model.CheckingLogDTORedis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class InOutResponse {

    private List<TeamDTO> data;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamDTO {
        private Integer groupId;
        private String groupName;
        private List<CheckingLogDTORedis> users;
    }
}
