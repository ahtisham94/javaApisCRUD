package com.example.demo.services;

import com.example.demo.models.User.UserInfo;
import com.example.demo.repos.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {

    private final UserRepository repository;

    public List<UserInfo> getAllUsers() {
        return repository.findAll();
    }

    public UserInfo saveUser(UserInfo userInfo) {
        return repository.insert(userInfo);
    }

    public UserRepository getRepository() {
        return repository;
    }
}
