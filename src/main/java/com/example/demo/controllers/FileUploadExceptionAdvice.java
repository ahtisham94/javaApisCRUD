package com.example.demo.controllers;

import com.example.demo.models.GeneralResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<GeneralResponseModel> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        responseModel.setSuccess(false);
        responseModel.setMessage("File too large!");
        responseModel.setData(null);
        responseModel.setCode(134356);
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
    }
}