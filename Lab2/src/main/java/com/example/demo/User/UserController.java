package com.example.demo.User;

import com.example.demo.User.dto.CreateUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping()
    public List<User> getUsers(){
        return this.userService.getUsers();
    }

    @PostMapping()
    public User createUser(@RequestBody CreateUserDto createUserDto){
        return this.userService.createUser(createUserDto);
    }
}
