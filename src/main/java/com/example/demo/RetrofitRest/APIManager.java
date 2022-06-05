package com.example.demo.RetrofitRest;

import com.example.demo.models.GeneralResponseModel;
import com.example.demo.models.User.GenerateOTPRequestModel;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

public class APIManager<T> {

    public static APIManager instance = new APIManager();

    public static Retrofit retrofit;

    public static APIManager getInstance() {
        if (instance == null)
            instance = new APIManager();
        return instance;
    }

    static GetDataService service;

    public static GetDataService getService() {
        if (service == null)
            service = retrofit.create(GetDataService.class);
        return service;
    }

    private APIManager() {
        retrofit = ClientInstance.getIntance();
    }

    public void generateOTP(CallbackGeneric callback, GenerateOTPRequestModel requestModel, int rc) {
        GetDataService service = getService();
        Call<GeneralResponseModel> result = service.generateOTP("application/json", requestModel);
        sendRequestSync(result, callback, rc);
    }


    private <T> void sendRequestSync(Call<T> call, final CallbackGeneric result, int rc) {
        try {
            Response<GeneralResponseModel> response = (Response<GeneralResponseModel>) call.execute();


            if (response.code() == 200 || response.code() == 201) {
                GeneralResponseModel<T> genericResponseModel = (GeneralResponseModel<T>) response.body();
                result.onResult(genericResponseModel, rc);
            } else {
                GeneralResponseModel<T> genericResponseModel = new GeneralResponseModel<>();
                genericResponseModel.setData(null);
                genericResponseModel.setSuccess(false);
                genericResponseModel.setCode(response.code());
                genericResponseModel.setMessage(response.message());
            }

        } catch (IOException e) {
            GeneralResponseModel<T> genericResponseModel = new GeneralResponseModel<>();
            genericResponseModel.setData(null);
            genericResponseModel.setSuccess(false);
            genericResponseModel.setCode(12470);
            genericResponseModel.setMessage(e.getLocalizedMessage());
        }
    }

    private <T> void sendResultGeneric(Call<T> call, final CallbackGeneric result, int rc) {

        call.enqueue(new retrofit2.Callback<T>() {


            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                if (response.code() == 200 || response.code() == 201) {
                    GeneralResponseModel<T> genericResponseModel = (GeneralResponseModel<T>) response.body();
                    result.onResult(genericResponseModel, rc);
                } else {
                    GeneralResponseModel<T> genericResponseModel = new GeneralResponseModel<>();
                    genericResponseModel.setData(null);
                    genericResponseModel.setSuccess(false);
                    genericResponseModel.setCode(response.code());
                    genericResponseModel.setMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                GeneralResponseModel<T> genericResponseModel = new GeneralResponseModel<>();
                genericResponseModel.setData(null);
                genericResponseModel.setSuccess(false);
                genericResponseModel.setCode(12470);
                genericResponseModel.setMessage(throwable.getLocalizedMessage());
            }
        });

    }


    public interface CallbackGeneric<T> {
        void onResult(GeneralResponseModel<T> response, int requestCode);
    }
}
