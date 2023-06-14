package com.htsc.vn.demo.PrisonManagement.job;

import com.htsc.vn.demo.PrisonManagement.Uils.Common;
import com.htsc.vn.demo.PrisonManagement.Uils.StatusUser;
import com.htsc.vn.demo.PrisonManagement.model.CheckingLog;
import com.htsc.vn.demo.PrisonManagement.model.User;
import com.htsc.vn.demo.PrisonManagement.model.UserStatus;
import com.htsc.vn.demo.PrisonManagement.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CloneDataUserJob {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepository userRepository;


    //@Scheduled(cron = "59 59 23 * * *")
    //@Scheduled(fixedRate = 1000)
    //@Scheduled(cron = "0 19 11 * * *")
    public void removeDataCheckingLog() throws Exception {

        try {
            mongoTemplate.remove(CheckingLog.class);
            List<User> users = mongoTemplate.findAll(User.class);
            List<CheckingLog> checkingLogs = new ArrayList<>();
            for (User user : users) {
                CheckingLog checkingLog = new CheckingLog();
                checkingLog.setId(user.getId());
                checkingLog.setName(user.getName());
                checkingLog.setImage(user.getImage());
                checkingLog.setAddress(user.getAddress());
                checkingLog.setBirthday(user.getBirthday());
                checkingLog.setFeature(user.getFeature());
                checkingLog.setGroupUserId(user.getGroupUserId());
                checkingLog.setTypeUserId(user.getTypeUserId());
                checkingLog.setUserStatusId(user.getUserStatusId());
                checkingLogs.add(checkingLog);
                if (checkingLogs.size() == 50) {
                    mongoTemplate.insertAll(checkingLogs);
                    checkingLogs.clear();
                }
            }
            if (!checkingLogs.isEmpty()) {
                mongoTemplate.insertAll(checkingLogs);
            }
        }catch (Exception e) {
            throw new Exception("Error deleting documents in CheckingLog collection:",e);
        }

    }
}
