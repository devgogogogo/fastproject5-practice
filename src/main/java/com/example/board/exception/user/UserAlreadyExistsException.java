package com.example.board.exception.user;


import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ClientErrorException {

    public UserAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "User already exists.");
    }

    //예외가 발생했을때 구체적인 포스트 아이디를 알고 있다면 이런식으로 사용할 수 있다.
    public UserAlreadyExistsException(String username) {
        super(HttpStatus.CONFLICT, "User with userId" + username + " already exists.");
    }
}
