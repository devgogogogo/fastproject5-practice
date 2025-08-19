package com.example.board.controller;

import com.example.board.model.user.User;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserLoginRequestBody;
import com.example.board.model.user.UserSignUpRequestBody;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody requestBody) {
        var user = userService.signUp(requestBody.username(), requestBody.password());
        return new ResponseEntity<>(user, HttpStatus.OK);
//        return new ResponseEntity<>(user, HttpStatus.CREATED); 같은 표현임

    }

    @PostMapping("/authenticate")  //로그인을 통해 토큰을 발행하는 컨트롤
    public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody UserLoginRequestBody userLoginRequestBody) {
        UserAuthenticationResponse response = userService.authenticate(userLoginRequestBody.username(), userLoginRequestBody.password());
        return ResponseEntity.ok(response);
    }
}
