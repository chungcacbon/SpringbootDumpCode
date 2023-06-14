package com.htsc.vn.demo.PrisonManagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AIModel {

    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("type_event")
    private String typeEvent;
    @JsonProperty("count_record")
    private String countRecord;
    @JsonProperty("user_id")
    private String userId;
}
