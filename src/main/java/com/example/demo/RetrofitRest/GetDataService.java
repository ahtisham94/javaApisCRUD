package com.example.demo.RetrofitRest;

import com.example.demo.models.GeneralResponseModel;
import com.example.demo.models.User.GenerateOTPRequestModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GetDataService {
    @POST("/Account/GenerateOTP")
    Call<GeneralResponseModel> generateOTP(
            @Header("Content-Type") String contentType,
            @Body GenerateOTPRequestModel model);
}
