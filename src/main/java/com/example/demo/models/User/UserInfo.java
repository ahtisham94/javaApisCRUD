package com.example.demo.models.User;

import lombok.Data;
import org.apache.catalina.LifecycleState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class UserInfo {
    @Id
    private long id;
    private String name;
    private String lastName;
    @Indexed(unique = true)
    private String email;
    private Gender gender;
    private UserAddress userAddress;
    private List<String> userSubjects;

    private String updatedEmail;

    private String profilePath;

    public UserInfo(String name, String lastName, String email, Gender gender, UserAddress userAddress, List<String> userSubjects) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.userAddress = userAddress;
        this.userSubjects = userSubjects;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getProfilePath() {
        return profilePath;
    }
}
