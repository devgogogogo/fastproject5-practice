package com.example.board.exception.follow;


import com.example.board.exception.ClientErrorException;
import com.example.board.model.entity.UserEntity;
import org.springframework.http.HttpStatus;

public class FollowAlreadyExistException extends ClientErrorException {

    public FollowAlreadyExistException() {
        super(HttpStatus.CONFLICT, "Follow already exists.");
    }

    //예외가 발생했을때 구체적인 포스트 아이디를 알고 있다면 이런식으로 사용할 수 있다.
    public FollowAlreadyExistException(UserEntity follower, UserEntity following) {
        super(HttpStatus.CONFLICT, "Follow with follower" + follower.getUsername() + " and following " + following.getUsername() + " already exists.");
    }
}
