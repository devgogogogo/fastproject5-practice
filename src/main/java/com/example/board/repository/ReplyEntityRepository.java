package com.example.board.repository;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyEntityRepository extends JpaRepository<ReplyEntity, Long> {

    List<ReplyEntity> findByUser(UserEntity user);

    List<ReplyEntity> findByPost(PostEntity post);

}
