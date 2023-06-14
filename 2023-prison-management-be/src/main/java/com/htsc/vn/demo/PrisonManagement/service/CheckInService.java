package com.htsc.vn.demo.PrisonManagement.service;

import com.htsc.vn.demo.PrisonManagement.model.CheckIn;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface CheckInService {
    Optional<CheckIn> findById(ObjectId id);
    List<CheckIn> findAll();
}
