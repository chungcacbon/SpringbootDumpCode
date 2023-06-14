package com.htsc.vn.demo.PrisonManagement.service;

import com.htsc.vn.demo.PrisonManagement.model.CheckIn;
import com.htsc.vn.demo.PrisonManagement.model.CheckOut;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface CheckOutService {
    Optional<CheckOut> findById(ObjectId id);

    List<CheckOut> findAll();
}
