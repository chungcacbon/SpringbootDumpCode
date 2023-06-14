package com.htsc.vn.demo.PrisonManagement.exception;

import lombok.Data;

@Data
public class HtscException extends RuntimeException {
    private int status;
    public HtscException(int status, String message) {
        super(message);
        this.status = status;
    }
}
