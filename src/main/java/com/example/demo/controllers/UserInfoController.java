package com.example.demo.controllers;

import com.example.demo.RetrofitRest.APIManager;
import com.example.demo.models.GeneralResponseModel;
import com.example.demo.models.OTPModel;
import com.example.demo.models.User.GenerateOTPRequestModel;
import com.example.demo.models.User.UserInfo;
import com.example.demo.services.StudentService;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;
import retrofit2.Call;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RestController
@AllArgsConstructor
public class UserInfoController<T> {

    private final StudentService studentService;
    @Autowired
    MongoTemplate template;

    @RequestMapping(value = "api/v1/getAllUsers", method = RequestMethod.GET)
    public GeneralResponseModel<T> getAllUsers() {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        if (studentService.getAllUsers().size() > 0) {

            responseModel.setCode(00);
            responseModel.setMessage("Users Founds");
            responseModel.setSuccess(true);
            responseModel.setData(studentService.getAllUsers());
        } else {
            responseModel.setCode(1514);
            responseModel.setMessage("Users not found");
            responseModel.setSuccess(false);
            responseModel.setData(null);
        }
        return responseModel;
    }

    @RequestMapping(value = "api/v1/saveUser", method = RequestMethod.POST)
    public GeneralResponseModel<T> saveUser(@RequestBody UserInfo userInfo) {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        studentService.getRepository().findUserInfoByEmail(userInfo.getEmail()).
                ifPresentOrElse(userInfo1 -> {
                    responseModel.setCode(1211);
                    responseModel.setMessage("User Already Exists");
                    responseModel.setSuccess(false);
                    responseModel.setData(null);

                }, () -> {
                    studentService.saveUser(userInfo);
                    responseModel.setCode(00);
                    responseModel.setMessage("User information Successfully");
                    responseModel.setSuccess(true);
                    responseModel.setData(userInfo);

                });
        return responseModel;

    }

    @RequestMapping(value = "api/v1/deleteUser", method = RequestMethod.DELETE)
    public GeneralResponseModel<T> deleteUser(@RequestBody UserInfo userInfo) {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        studentService.getRepository().findUserInfoByEmail(userInfo.getEmail()).
                ifPresentOrElse(userInfo1 -> {
                    studentService.getRepository().delete(userInfo1);
                    responseModel.setCode(00);
                    responseModel.setMessage("User Deleted Successfully");
                    responseModel.setSuccess(true);
                    responseModel.setData(null);

                }, () -> {
                    responseModel.setCode(8524);
                    responseModel.setMessage("User Not found");
                    responseModel.setSuccess(false);
                    responseModel.setData(null);

                });
        return responseModel;

    }

    @RequestMapping(value = "api/v1/updateUser", method = RequestMethod.POST)
    public GeneralResponseModel<T> updateUser(@RequestBody UserInfo userInfo) {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        studentService.getRepository().findUserInfoByEmail(userInfo.getEmail()).
                ifPresentOrElse(userInfo1 -> {
                    Query query = new Query().addCriteria(where("email").is(userInfo.getEmail()));
                    Update update = new Update();
                    update.set("email", userInfo.getUpdatedEmail());
                    template.updateFirst(query, update, UserInfo.class);
                    UserInfo updateUserInfo = userInfo1;
                    updateUserInfo.setEmail(userInfo.getUpdatedEmail());
                    responseModel.setCode(00);
                    responseModel.setMessage("User update Successfully");
                    responseModel.setSuccess(true);
                    responseModel.setData(updateUserInfo);

                }, () -> {
                    responseModel.setCode(8524);
                    responseModel.setMessage("User Not found");
                    responseModel.setSuccess(false);
                    responseModel.setData(null);

                });
        return responseModel;

    }

    @RequestMapping(value = "api/v1/GenerateOTP", method = RequestMethod.POST)
    public GeneralResponseModel<T> generateOTP(@RequestBody GenerateOTPRequestModel requestModel) {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        APIManager.getInstance().generateOTP((response, requestCode) -> {
            responseModel.setData(response.getData());
            responseModel.setMessage(response.getMessage());
            responseModel.setSuccess(response.isSuccess());
            responseModel.setCode(response.getCode());

            studentService.getOtpRepository().findOtpModelByNumber(requestModel.getMsisdn())
                    .ifPresentOrElse(otpModel -> {
                        Query query = new Query().addCriteria(where("number").is(requestModel.getMsisdn()));
                        Update update = new Update();
                        otpModel.setOtp((int)((((Double)response.getData())).doubleValue()));
                        update.set("otp", otpModel.getOtp());
                        template.updateFirst(query, update, OTPModel.class);
                    }, () -> {
                        OTPModel otpModel = new OTPModel();
                        otpModel.setOtp((int)((((Double)response.getData())).doubleValue()));
                        otpModel.setNumber(requestModel.getMsisdn());
                        studentService.saveOTP(otpModel);
                    });


        }, requestModel, 1);

        return responseModel;
    }
}
