package com.htsc.vn.demo.PrisonManagement.kafka;

import com.htsc.vn.demo.PrisonManagement.Uils.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class TestListener {

//    @KafkaListener(topics = Common.KAFKA.TOPIC_SEND_MESSAGE, groupId = Common.KAFKA.GROUP_ID)
//    public void consume(String jsonData) {
//        System.out.println(String.format("#### -> Consumed message-> %s", jsonData));
//    }

}
