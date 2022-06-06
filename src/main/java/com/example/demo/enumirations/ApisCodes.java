package com.example.demo.enumirations;

import com.example.demo.RetrofitRest.APIManager;

public enum ApisCodes {
    SUCCESS(new ApiCode("00", "Success")),
    FILE_TOO_LARGE(new ApiCode("1117", "File too large")),
    RECORD_ALREADY_EXIST(new ApiCode("1112", "Record Already Exists")),
    USER_NOT_FOUND(new ApiCode("1341", "User not found")),
    EXCEPTIONAL_ERROR(new ApiCode("1341", "Something when wrong!! "));
    public ApiCode apiCode;

    ApisCodes(ApiCode code) {
        this.apiCode = code;
    }

    public static class ApiCode {
        public String code;
        public String desc;

        public ApiCode(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }
}
