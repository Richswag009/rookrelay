package com.richcodes.hookrelay.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponse> handleException(ResponseStatusException exec){
        return buildResponseEntity(exec,HttpStatus.valueOf(exec.getStatusCode().value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exec){
        return buildResponseEntity(exec,HttpStatus.BAD_REQUEST);
    }





    public ResponseEntity<ExceptionResponse> buildResponseEntity(Exception exec, HttpStatus httpStatus){
        ExceptionResponse error = new ExceptionResponse();
        error.setStatus(httpStatus.value());
        error.setMessage(exec.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error,httpStatus);
    }
}
