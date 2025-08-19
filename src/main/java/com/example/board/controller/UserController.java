package com.example.board.controller;

import com.example.board.model.user.User;
import com.example.board.model.user.UserSignUpRequestBody;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping()
    public ResponseEntity<User> signUp(@RequestBody UserSignUpRequestBody userSignUpRequestBody) {
        User user =userService.signUp(userSignUpRequestBody.username(), userSignUpRequestBody.password());
        return ResponseEntity.ok(user);
//        return new ResponseEntity<>(user, HttpStatus.CREATED); 같은 표현임
    }
}
