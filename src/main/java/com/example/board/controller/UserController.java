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

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
/*
    @PathVariable
     - URL 경로의 일부를 변수로 받아옵니다.
     - 보통 "특정 리소스 하나"를 지정할 때 사용해요.

    @RequestParam
     - ?key=value 형식의 쿼리 파라미터를 가져옵니다.
     - 보통 검색, 필터링, 페이징 같은 옵션 값에 많이 쓰여요.
 */
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String query) {
        List<User> userList = userService.getUsers(query);
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        User user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }


    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody requestBody) {
        User user = userService.signUp(requestBody.username(), requestBody.password());
        return new ResponseEntity<>(user, HttpStatus.OK);
//        return new ResponseEntity<>(user, HttpStatus.CREATED); 같은 표현임

    }

    @PostMapping("/authenticate")  //로그인을 통해 토큰을 발행하는 컨트롤
    public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody UserLoginRequestBody userLoginRequestBody) {
        UserAuthenticationResponse response = userService.authenticate(userLoginRequestBody.username(), userLoginRequestBody.password());
        return ResponseEntity.ok(response);
    }
}
