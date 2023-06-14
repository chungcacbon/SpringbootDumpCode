package com.htsc.vn.demo.PrisonManagement.feign.serviceImpl;

import com.htsc.vn.demo.PrisonManagement.feign.service.UsersFeignClient;
import com.htsc.vn.demo.PrisonManagement.feign.service.UsersFeignClientService;
import com.htsc.vn.demo.PrisonManagement.model.feign.output.UsersFeignClientOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsersFeignClientServiceImpl implements UsersFeignClientService {

    private final UsersFeignClient usersFeignClient;

    public UsersFeignClientServiceImpl(UsersFeignClient usersFeignClient) {
        this.usersFeignClient = usersFeignClient;
    }

    @Override
    public ResponseEntity<UsersFeignClientOutput> getFeatureUser(MultipartFile image) {
        return usersFeignClient.getFeatureUser(image);
    }
}
