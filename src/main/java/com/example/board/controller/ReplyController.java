package com.example.board.controller;

import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.reply.Reply;
import com.example.board.model.reply.ReplyPatchRequestBody;
import com.example.board.model.reply.ReplyRequestBody;
import com.example.board.service.PostService;
import com.example.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/replies")
@RequiredArgsConstructor
@Slf4j
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping()
    public ResponseEntity<List<Reply>> getReplies(@PathVariable Long postId) {

        List<Reply> replies = replyService.getRepliesByPostId(postId);

        return ResponseEntity.ok(replies);

    }

    @PostMapping
    public ResponseEntity<Reply> createReply(
            @PathVariable Long postId,
            @RequestBody ReplyRequestBody replyPostRequestBody,
            Authentication authentication) {

        Reply reply = replyService.createReply(postId, replyPostRequestBody, (UserEntity) authentication.getPrincipal());

        return ResponseEntity.ok(reply);
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<Reply> updateReply(
            @PathVariable Long postId,
            @PathVariable Long replyId,
            @RequestBody ReplyPatchRequestBody replyPatchRequestBody,
            Authentication authentication
    ) {
        Reply reply= replyService.updateReply(postId, replyId, replyPatchRequestBody, (UserEntity)authentication.getPrincipal());

        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long postId,
            @PathVariable Long replyId,
            Authentication authentication
    ) {
        replyService.deleteReply(postId, replyId,(UserEntity) authentication.getPrincipal());
        return ResponseEntity.noContent().build();
    }
}
