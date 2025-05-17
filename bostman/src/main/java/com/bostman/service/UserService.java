package com.bostman.service;

import com.bostman.dto.UserRegisterDTO;
import com.bostman.dto.UserLoginDTO;
import com.bostman.entity.User;

public interface UserService {
    String register(UserRegisterDTO dto);
    String login(UserLoginDTO dto);

}
