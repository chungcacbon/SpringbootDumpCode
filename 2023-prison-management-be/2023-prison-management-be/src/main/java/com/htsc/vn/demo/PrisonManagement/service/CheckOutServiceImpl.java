package com.htsc.vn.demo.PrisonManagement.service;

import com.htsc.vn.demo.PrisonManagement.model.CheckOut;
import com.htsc.vn.demo.PrisonManagement.repository.CheckOutRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheckOutServiceImpl implements CheckOutService{

    private final CheckOutRepository checkOutRepository;
    private final MongoTemplate mongoTemplate;

    public CheckOutServiceImpl(CheckOutRepository checkOutRepository, MongoTemplate mongoTemplate) {
        this.checkOutRepository = checkOutRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<CheckOut> findById(ObjectId id) {
        return Optional.ofNullable(checkOutRepository.findById(id).orElse(null));
    }

    @Override
    public List<CheckOut> findAll() {
        return mongoTemplate.findAll(CheckOut.class);
    }
}
