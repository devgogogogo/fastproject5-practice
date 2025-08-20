package com.example.board.model.reply;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.post.Post;
import com.example.board.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;

//DTO 역할
@JsonInclude(JsonInclude.Include.NON_NULL)
// null 아닐 경우에만 json에 포함시켜서 응답한다.
public record Reply(
        Long replyId,
        String body,
        User user,
        Post post,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime,
        ZonedDateTime deletedDateTime
        ) {
    public static Reply from(ReplyEntity replyEntity) {
        return new Reply(
                replyEntity.getReplyId(),
                replyEntity.getBody(),
                User.from(replyEntity.getUser()),
                Post.from(replyEntity.getPost()),
                replyEntity.getCreatedDateTime(),
                replyEntity.getUpdatedDateTime(),
                replyEntity.getDeletedDateTime()
        );
    }
}
