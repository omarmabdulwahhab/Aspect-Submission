package com.bostman.service;

import com.bostman.dto.UserRegisterDTO;
import com.bostman.dto.UserLoginDTO;
import com.bostman.dto.UserResponseDTO;
import com.bostman.entity.Role;

import java.util.List;

public interface UserService {
    String register(UserRegisterDTO dto);
    String login(UserLoginDTO dto);
    List<UserResponseDTO> findUsersByRole(Role role);
}
