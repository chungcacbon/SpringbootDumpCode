package com.htsc.vn.demo.PrisonManagement.config;

import com.htsc.vn.demo.PrisonManagement.redis.service.InOutRedisService;
import com.htsc.vn.demo.PrisonManagement.service.CheckInService;
import com.htsc.vn.demo.PrisonManagement.service.CheckOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    private final ApplicationContext applicationContext;
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    public WebSocketConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        CheckInService checkInService = applicationContext.getBean(CheckInService.class);
        CheckOutService checkOutService = applicationContext.getBean(CheckOutService.class);
        InOutRedisService inOutRedisService = applicationContext.getBean(InOutRedisService.class);
        //registry.addHandler(new SocketHandler(checkInService,checkOutService,inOutRedisService,mongoTemplate), "/checkins").setAllowedOrigins("*");
        registry.addHandler(new SocketHandler(checkInService,checkOutService,inOutRedisService,mongoTemplate), "/attendance").setAllowedOrigins("*");

    }
}
