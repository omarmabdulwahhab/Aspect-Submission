package com.bostman.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String fullName;
    private String email;
    private String password;
}
