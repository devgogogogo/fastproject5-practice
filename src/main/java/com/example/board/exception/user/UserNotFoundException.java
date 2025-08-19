package com.example.board.exception.post;


import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ClientErrorException {

    public PostNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Post not found.");
    }

    //예외가 발생했을때 구체적인 포스트 아이디를 알고 있다면 이런식으로 사용할 수 있다.
    public PostNotFoundException(Long postId) {
        super(HttpStatus.NOT_FOUND, "Post with postId" + postId + " not found.");
    }


    //구체적인 메세지를 남기고 싶다면 이렇게 할 수 있다.
    public PostNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
