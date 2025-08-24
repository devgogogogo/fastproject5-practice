package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.reply.Reply;
import com.example.board.model.user.*;
import com.example.board.service.PostService;
import com.example.board.service.ReplyService;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final PostService postService;
    private final ReplyService replyService;

    //유저 전체조회
    @GetMapping()
    public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String query, Authentication authentication) {
        List<User> userList = userService.getUsers(query, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(userList);
    }

    //유저 단건조회
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username, Authentication authentication) {
        User user = userService.getUser(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

    //특정유저의 게시물
    @GetMapping("/{username}/posts")
    public ResponseEntity<List<Post>> getPostByUsername(@PathVariable String username, Authentication authentication) {
        List<Post> posts = postService.getPostByUsername(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(posts);
    }

    //팔로우
    @PostMapping("/{username}/follows")
    public ResponseEntity<User> follow(@PathVariable String username, Authentication authentication) {
        User user = userService.follow(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

    //팔로우 취소
    @DeleteMapping("/{username}/follows")
    public ResponseEntity<User> unFollow(@PathVariable String username, Authentication authentication) {
        User user = userService.unFollow(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

    //
    @GetMapping("/{username}/followers") //누군가의 팔로워 조회 목록
    public ResponseEntity<List<Follower>> getFollowersByUser(@PathVariable String username, Authentication authentication) {
        List<Follower> followers = userService.getFollowersByUsername(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{username}/followings") //누군가의 팔로잉 조회 목록
    public ResponseEntity<List<User>> getFollowingsByUser(@PathVariable String username, Authentication authentication) {
        List<User> followings = userService.getFollowingsByUser(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(followings);
    }

    @GetMapping("/{username}/replies")
    public ResponseEntity<List<Reply>> getRepliesByUser(@PathVariable String username, Authentication authentication) {

        List<Reply> replies = replyService.getRepliesByUser(username);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/{username}/liked-users") //누군가의 팔로잉 조회 목록
    public ResponseEntity<List<LikedUser>> getLikedUsersByUser(@PathVariable String username, Authentication authentication) {
        List<LikedUser> likedUsers  =userService.getLikedUsersByUser(username, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(likedUsers);
    }

    @PatchMapping("/{username}") //회원 수정
    public ResponseEntity<User> getUser(
            @PathVariable String username,
            @RequestBody UserPatchRequestBody requestBody,
            Authentication authentication) {
        User user = userService.updateUser(username, requestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(user);
    }

    //회원가입
    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody requestBody) {
        User user = userService.signUp(requestBody.username(), requestBody.password());
        return new ResponseEntity<>(user, HttpStatus.OK);
//        return new ResponseEntity<>(user, HttpStatus.CREATED); 같은 표현임

    }

    //로그인
    @PostMapping("/authenticate")  //로그인을 통해 토큰을 발행하는 컨트롤
    public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody UserLoginRequestBody userLoginRequestBody) {
        UserAuthenticationResponse response = userService.authenticate(userLoginRequestBody.username(), userLoginRequestBody.password());
        return ResponseEntity.ok(response);
    }


}
