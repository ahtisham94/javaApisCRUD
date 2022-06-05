package com.example.demo;

import com.example.demo.models.User.Gender;
import com.example.demo.models.User.UserAddress;
import com.example.demo.models.User.UserInfo;
import com.example.demo.repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import retrofit2.Retrofit;

import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("first Project");
    }

    @Bean
    CommandLineRunner runner(UserRepository repository, MongoTemplate mongoTemplate) {
        return args -> {
            UserAddress address = new UserAddress("Pakistan", "Islamabad", 46000);
            UserInfo userInfo = new UserInfo("Ahtisham",
                    "Zaheer", "ahtisham.zaheer@gmail.com", Gender.MALE,
                    address, List.of("Computer", "Science"));
            userInfo.setId(1);
            repository.findUserInfoByEmail(userInfo.getEmail())
                    .ifPresentOrElse(userInfo1 -> {
                        System.out.println("Already Exist");
                    }, () -> {
                        System.out.println("Save User");
                        repository.save(userInfo);
                    });
//            usingMongoTemplate(repository, mongoTemplate, userInfo);

        };

    }

    private void usingMongoTemplate(UserRepository repository, MongoTemplate mongoTemplate, UserInfo userInfo) throws Exception {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is("ahhti@gmail.com"));
        List<UserInfo> userInfoList = mongoTemplate.find(query, UserInfo.class);
        if (userInfoList.size() > 0) {
            throw new Exception("Already found");
        } else
            repository.save(userInfo);
    }

}
