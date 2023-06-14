package com.htsc.vn.demo.PrisonManagement.Uils;

import org.bson.types.ObjectId;

public class Common {

    public static final String REDIS_KEY_CHECKIN = "checkins";
    public static final String REDIS_KEY_CHECKOUT = "checkouts";
    public static final String REDIS_KEY_GET_ALL_USERS = "getAllUser";

    public static final String REDIS_KEY_RENDER_DATA_CLIENT = "initData";

    public final static String BASE_URL_IMG = "/images";

    public static final String REDIS_KEY_COMMON = "inout";

    public static final Integer CHECK_OUT = 0;
    public static final Integer CHECK_IN = 1;

    public static final Integer CAM_ID_CHECKIN = 1;

    public static final Integer CAM_ID_CHECKOUT = 2;

    public interface KAFKA {
        public static final String TOPIC_SEND_MESSAGE = "topic_send_sms";
        public static final String GROUP_ID = "group_send_sms_id";
    }

}
