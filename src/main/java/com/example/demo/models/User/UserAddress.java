package com.example.demo.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class UserAddress {
    @Id
    private int id;
    private String country;
    private String city;
    private int postalCode;

    public UserAddress(String country, String city, int postalCode) {
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
    }
}
