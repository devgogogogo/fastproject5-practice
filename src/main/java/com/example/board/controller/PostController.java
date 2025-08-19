package com.example.board.controller;

import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping()
    public ResponseEntity<List<Post>> getPosts() {
        var posts = postService.getPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
        Post post = postService.getPostByPostId(postId);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody postPostRequestBody) {
        var post = postService.createPost(postPostRequestBody);
        return ResponseEntity.ok(post);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId , @RequestBody PostPatchRequestBody  postPatchRequestBody) {
        Post post = postService.updatePost(postId, postPatchRequestBody);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId ) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
