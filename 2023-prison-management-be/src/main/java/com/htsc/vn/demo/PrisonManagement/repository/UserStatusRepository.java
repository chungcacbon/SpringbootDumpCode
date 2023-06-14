package com.htsc.vn.demo.PrisonManagement.repository;

import com.htsc.vn.demo.PrisonManagement.model.UserStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatusRepository extends MongoRepository<UserStatus, ObjectId> {

}
