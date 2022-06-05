package com.example.demo.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class
GenerateOTPRequestModel {
    String msisdn = "";


}
