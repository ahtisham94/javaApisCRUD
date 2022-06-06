package com.example.demo.repos;

import com.example.demo.models.User.UserInfo;
import org.apache.catalina.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserInfo, String> {

    Optional<UserInfo> findUserInfoByEmail(String email);

    @Override
    long count();
}
