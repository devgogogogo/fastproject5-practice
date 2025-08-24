package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.user.LikedUser;
import com.example.board.service.PostService;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<Post>> getPosts(Authentication authentication) {
        var posts = postService.getPosts((UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId,
                                                Authentication authentication) {
        Post post = postService.getPostByPostId(postId, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{postId}/liked-users")  //게시물에 좋아요 누른 유저들
    public ResponseEntity<List<LikedUser>> getLikedUsersByPostId(@PathVariable Long postId,
                                                                 Authentication authentication) {
        List<LikedUser> likedUsers = userService.getLikedUsersByPostId(postId, (UserEntity) authentication.getPrincipal());

        return ResponseEntity.ok(likedUsers);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody postPostRequestBody, Authentication authentication) {
        var post = postService.createPost(postPostRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody PostPatchRequestBody postPatchRequestBody, Authentication authentication) {
        Post post = postService.updatePost(postId, postPatchRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Authentication authentication) {
        postService.deletePost(postId, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.noContent().build();
    }


    //좋아요
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Post> toggleLike(@PathVariable Long postId, Authentication authentication) {
        Post post = postService.toggleLike(postId, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }
}
