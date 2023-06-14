package com.htsc.vn.demo.PrisonManagement.feign.service;

import com.htsc.vn.demo.PrisonManagement.model.feign.output.UsersFeignClientOutput;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "postFeignClient", url = "${client.post.baseUrl}")
public interface UsersFeignClient {

    @PostMapping(value = "/get-feature-person-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Headers("Content-Type: multipart/form-data")
    ResponseEntity<UsersFeignClientOutput> getFeatureUser(@RequestPart(value = "file_up", required = true) MultipartFile image);
}
