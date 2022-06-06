package com.example.demo.controllers;

import com.example.demo.enumirations.ApisCodes;
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
        responseModel.setMessage(ApisCodes.FILE_TOO_LARGE.apiCode.desc);
        responseModel.setData(null);
        responseModel.setCode(ApisCodes.FILE_TOO_LARGE.apiCode.code + "");
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
    }
}