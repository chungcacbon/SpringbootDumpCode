package com.htsc.vn.demo.PrisonManagement.service;

import com.htsc.vn.demo.PrisonManagement.model.CheckIn;
import com.htsc.vn.demo.PrisonManagement.repository.CheckInRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheckInServiceImpl implements CheckInService {

    private final CheckInRepository checkInRepository;

    private final MongoTemplate mongoTemplate;

    public CheckInServiceImpl(CheckInRepository checkInRepository, MongoTemplate mongoTemplate) {
        this.checkInRepository = checkInRepository;
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public Optional<CheckIn> findById(ObjectId id) {
        return Optional.ofNullable(checkInRepository.findById(id).orElse(null));
    }

    @Override
    public List<CheckIn> findAll() {
        return mongoTemplate.findAll(CheckIn.class);
    }
}
