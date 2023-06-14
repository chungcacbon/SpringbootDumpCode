package com.htsc.vn.demo.PrisonManagement.feign.service;

import com.htsc.vn.demo.PrisonManagement.model.feign.output.UsersFeignClientOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UsersFeignClientService {
    ResponseEntity<UsersFeignClientOutput> getFeatureUser(MultipartFile image);
}
