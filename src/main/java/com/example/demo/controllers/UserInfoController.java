package com.example.demo.controllers;

import com.example.demo.models.GeneralResponseModel;
import com.example.demo.models.User.UserInfo;
import com.example.demo.services.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserInfoController<T> {

    private final StudentService studentService;

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

                    UserInfo updateUserInfo = userInfo1;
                    studentService.getRepository().delete(userInfo1);
                    updateUserInfo.setEmail(userInfo.getUpdatedEmail());
                    studentService.saveUser(updateUserInfo);
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
}
