package com.example.demo.controllers;

import com.example.demo.RetrofitRest.APIManager;
import com.example.demo.enumirations.ApisCodes;
import com.example.demo.fileUtils.FileInfo;
import com.example.demo.fileUtils.FilesStorageService;
import com.example.demo.models.GeneralResponseModel;
import com.example.demo.models.OTPModel;
import com.example.demo.models.User.GenerateOTPRequestModel;
import com.example.demo.models.User.UserInfo;
import com.example.demo.services.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@AllArgsConstructor
public class UserInfoController<T> {

    private final StudentService studentService;
    @Autowired
    MongoTemplate template;
    @Autowired
    FilesStorageService storageService;

    @RequestMapping(value = "api/v1/getAllUsers", method = RequestMethod.GET)
    public GeneralResponseModel<T> getAllUsers() {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        if (studentService.getAllUsers().size() > 0) {

            responseModel.setCode(ApisCodes.SUCCESS.apiCode.code + "");
            responseModel.setMessage(ApisCodes.SUCCESS.apiCode.desc);
            responseModel.setSuccess(true);
            responseModel.setData(studentService.getAllUsers());
        } else {
            responseModel.setCode(ApisCodes.USER_NOT_FOUND.apiCode.code + "");
            responseModel.setMessage(ApisCodes.USER_NOT_FOUND.apiCode.desc);
            responseModel.setSuccess(false);
            responseModel.setData(null);
        }
        return responseModel;
    }

    @RequestMapping(value = "api/v1/saveUser", method = RequestMethod.POST)
    public GeneralResponseModel<T> saveUser(@RequestBody UserInfo userInfo) {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        studentService.getRepository().findUserInfoByEmail(userInfo.getEmail()).ifPresentOrElse(userInfo1 -> {
            responseModel.setCode(ApisCodes.RECORD_ALREADY_EXIST.apiCode.code + "");
            responseModel.setMessage(ApisCodes.RECORD_ALREADY_EXIST.apiCode.desc);
            responseModel.setSuccess(false);
            responseModel.setData(null);

        }, () -> {
            userInfo.setId(studentService.getRepository().count() + 1);
            studentService.saveUser(userInfo);
            responseModel.setCode(ApisCodes.SUCCESS.apiCode.code + "");
            responseModel.setMessage(ApisCodes.SUCCESS.apiCode.desc);
            responseModel.setSuccess(true);
            responseModel.setData(userInfo);

        });
        return responseModel;

    }

    @RequestMapping(value = "api/v1/deleteUser", method = RequestMethod.DELETE)
    public GeneralResponseModel<T> deleteUser(@RequestBody UserInfo userInfo) {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        studentService.getRepository().findUserInfoByEmail(userInfo.getEmail()).ifPresentOrElse(userInfo1 -> {
            studentService.getRepository().delete(userInfo1);
            responseModel.setCode(ApisCodes.SUCCESS.apiCode.code + "");
            responseModel.setMessage("User Deleted Successfully");
            responseModel.setSuccess(true);
            responseModel.setData(null);

        }, () -> {
            responseModel.setCode(ApisCodes.USER_NOT_FOUND.apiCode.code + "");
            responseModel.setMessage(ApisCodes.USER_NOT_FOUND.apiCode.desc);
            responseModel.setSuccess(false);
            responseModel.setData(null);

        });
        return responseModel;

    }

    @RequestMapping(value = "api/v1/updateUser", method = RequestMethod.POST)
    public GeneralResponseModel<T> updateUser(@RequestBody UserInfo userInfo) {
        GeneralResponseModel responseModel = new GeneralResponseModel();
        studentService.getRepository().findUserInfoByEmail(userInfo.getEmail()).ifPresentOrElse(userInfo1 -> {
            Query query = new Query().addCriteria(where("email").is(userInfo.getEmail()));
            Update update = new Update();
            update.set("email", userInfo.getUpdatedEmail());
            template.updateFirst(query, update, UserInfo.class);
            UserInfo updateUserInfo = userInfo1;
            updateUserInfo.setEmail(userInfo.getUpdatedEmail());
            responseModel.setCode(ApisCodes.SUCCESS.apiCode.code + "");
            responseModel.setMessage("User update Successfully");
            responseModel.setSuccess(true);
            responseModel.setData(updateUserInfo);

        }, () -> {
            responseModel.setCode(ApisCodes.USER_NOT_FOUND.apiCode.code + "");
            responseModel.setMessage(ApisCodes.USER_NOT_FOUND.apiCode.desc);
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
            studentService.getOtpRepository().findOtpModelByNumber(requestModel.getMsisdn()).ifPresentOrElse(otpModel -> {
                Query query = new Query().addCriteria(where("number").is(requestModel.getMsisdn()));
                Update update = new Update();
                otpModel.setOtp((int) ((((Double) response.getData())).doubleValue()));
                update.set("otp", otpModel.getOtp());
                template.updateFirst(query, update, OTPModel.class);
            }, () -> {
                OTPModel otpModel = new OTPModel();
                otpModel.setId(studentService.getOtpRepository().count() + 1);
                otpModel.setOtp((int) ((((Double) response.getData())).doubleValue()));
                otpModel.setNumber(requestModel.getMsisdn());
                studentService.saveOTP(otpModel);
            });


        }, requestModel, 1);

        return responseModel;
    }

    @RequestMapping(value = "api/v1/upload", method = RequestMethod.POST)
    public ResponseEntity<GeneralResponseModel> uploadFile(@RequestParam("file") MultipartFile file,
                                                           String name, int id) {
        String message = "";
        GeneralResponseModel responseModel = new GeneralResponseModel();
        try {
            studentService.getRepository().findUserInfoById(id).ifPresentOrElse(userInfo -> {
                storageService.save(file);
                attachedFileWithUser(file.getOriginalFilename(), userInfo);
                responseModel.setMessage("Uploaded the file successfully: " + file.getOriginalFilename());
                responseModel.setCode(ApisCodes.SUCCESS.apiCode.code + "");
                responseModel.setSuccess(true);
            }, () -> {
                responseModel.setCode(ApisCodes.USER_NOT_FOUND.apiCode.code + "");
                responseModel.setMessage(ApisCodes.USER_NOT_FOUND.apiCode.desc + "");
                responseModel.setSuccess(false);
            });

            responseModel.setData(null);
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            responseModel.setCode(ApisCodes.EXCEPTIONAL_ERROR.apiCode.code + "");
            responseModel.setData(null);
            responseModel.setMessage(ApisCodes.EXCEPTIONAL_ERROR.apiCode.desc + message);
            responseModel.setSuccess(true);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseModel);
        }
    }

    private void attachedFileWithUser(String originalFilename, UserInfo userInfo) {
        Query query = new Query().addCriteria(where("id").is(userInfo.getId()));
        Update update = new Update();
        String url = MvcUriComponentsBuilder.fromMethodName(UserInfoController.class, "getFile", originalFilename).build().toString();
        userInfo.setProfilePath(url);
        update.set("profilePath", userInfo.getProfilePath());
        template.updateFirst(query, update, UserInfo.class);
    }

    @RequestMapping(value = "api/v1/files", method = RequestMethod.GET)
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder.fromMethodName(UserInfoController.class, "getFile", path.getFileName().toString()).build().toString();
            return new FileInfo(filename, url);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("api/v1/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
