package com.example.board.model.post;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;

//DTO 역할
@JsonInclude(JsonInclude.Include.NON_NULL)
// null 아닐 경우에만 json에 포함시켜서 응답한다.
public record Post(
        Long postId,
        String body,
        Long repliesCount,
        Long likesCount,
        User user,
        ZonedDateTime createdDateTime,
        ZonedDateTime updatedDateTime,
        ZonedDateTime deletedDateTime,
        Boolean isLiking
        ) {
    public static Post from(PostEntity postEntity) {
        return new Post(
                postEntity.getPostId(),
                postEntity.getBody(),
                postEntity.getRepliesCount(),
                postEntity.getLikeCount(),
                com.example.board.model.user.User.from(postEntity.getUser()),
                postEntity.getCreatedDateTime(),
                postEntity.getUpdatedDateTime(),
                postEntity.getDeletedDateTime(),
                null
        );
    }
    public static Post from(PostEntity postEntity,boolean isLiking) {
        return new Post(
                postEntity.getPostId(),
                postEntity.getBody(),
                postEntity.getRepliesCount(),
                postEntity.getLikeCount(),
                com.example.board.model.user.User.from(postEntity.getUser()),
                postEntity.getCreatedDateTime(),
                postEntity.getUpdatedDateTime(),
                postEntity.getDeletedDateTime(),
                isLiking
        );
    }
}
