package com.example.demo.repos;

import com.example.demo.models.OTPModel;
import com.example.demo.models.User.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OTPRepository extends MongoRepository<OTPModel, String> {


    Optional<OTPModel> findOtpModelByNumber(String number);

    @Override
    long count();
}
