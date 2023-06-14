package com.htsc.vn.demo.PrisonManagement.Uils;

import lombok.Data;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public enum StatusUser {
    NOT_CHECKIN_OUT(new ObjectId("6458c95cbb4e2d63e299f312"), -1, "Chưa điểm danh"),
    CHECK_OUT(new ObjectId("6459e844bb4e2d63e299f3ad"), 0, "Chưa về"),
    CHECK_IN(new ObjectId("6459e856bb4e2d63e299f3ae"), 1, "Đã về");

    private final ObjectId objectId;
    private final int id;
    private final String description;

    StatusUser(ObjectId objectId, int id, String description) {
        this.objectId = objectId;
        this.id = id;
        this.description = description;
    }
    public static StatusUser getById(int id) {
        for (StatusUser userId : StatusUser.values()) {
            if (userId.id == id) {
                return userId;
            }
        }
        throw new IllegalArgumentException("Invalid UserId id: " + id);
    }

    public static StatusUser getByObjectId(ObjectId objectId) {
        for (StatusUser userId : StatusUser.values()) {
            if (userId.objectId.equals(objectId)) {
                return userId;
            }
        }
        throw new IllegalArgumentException("Invalid UserId objectId: " + objectId);
    }
}
