package com.htsc.vn.demo.PrisonManagement.job;

import com.htsc.vn.demo.PrisonManagement.Uils.Common;
import com.htsc.vn.demo.PrisonManagement.redis.service.InOutRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisCleanupJob {

    @Autowired
    InOutRedisService inOutRedisService;

    // Schedule the job to run every day at 23:59:59

    //@Scheduled(cron = "59 59 23 * * *")
    //@Scheduled(fixedRate = 1000)
    public void cleanupRedis() {
        inOutRedisService.deteleDataRedis(Set.of(Common.REDIS_KEY_CHECKIN, Common.REDIS_KEY_CHECKOUT,Common.REDIS_KEY_GET_ALL_USERS,Common.REDIS_KEY_COMMON));
        System.out.println("Removed key");
    }

}
