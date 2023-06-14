package com.htsc.vn.demo.PrisonManagement.exception;

import lombok.Data;

public class FeignClientException extends RuntimeException{
    public FeignClientException(String message) {
        super(message);
    }
}
