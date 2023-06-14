package com.htsc.vn.demo.PrisonManagement.config;

import com.htsc.vn.demo.PrisonManagement.error.ErrorResponse;
import com.htsc.vn.demo.PrisonManagement.exception.HtscException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleValidationException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        List<String> errors = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation error", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }

    //    @ExceptionHandler(FeignClientException.class)
//    public ResponseEntity<?> handleFeignClientException(FeignClientException ex) {
//        List<String> errors = new ArrayList<>();
//        String errorMessage  = ex.getMessage();
//        errors.add(errorMessage);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),errorMessage,errors));
//    }
    @ExceptionHandler(HtscException.class)
    public ResponseEntity<?> handleFeignClientException(HtscException ex) {
        List<String> errors = new ArrayList<>();
        String errorMessage = ex.getMessage();
        int status = ex.getStatus();

        errors.add(errorMessage);
        return ResponseEntity.status(status).body(new ErrorResponse(status, errorMessage, errors));
    }


}
