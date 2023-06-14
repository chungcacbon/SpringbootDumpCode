package com.htsc.vn.demo.PrisonManagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserBuilder {

    private Integer groupId;
    private String userId;
    private String userName;
    private String startDate;
    private String endDate;
    private Integer status;
    private String today;

    public UserBuilder() {
    }

    public UserBuilder withGroupId(Integer groupId) {
        this.groupId = groupId;
        return this;
    }

    public UserBuilder withUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public UserBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserBuilder withStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public UserBuilder withEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public UserBuilder withStatus(Integer status) {
        this.status = status;
        return this;
    }

    public UserBuilder withToday(String today) {
        this.today = today;
        return this;
    }

}
