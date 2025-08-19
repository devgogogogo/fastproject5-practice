package com.example.board.exception.user;


import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ClientErrorException {

    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "User not found.");
    }

    //예외가 발생했을때 구체적인 포스트 아이디를 알고 있다면 이런식으로 사용할 수 있다.
    public UserNotFoundException(String username) {
        super(HttpStatus.NOT_FOUND, "User with userId" + username + " not found.");
    }
}
