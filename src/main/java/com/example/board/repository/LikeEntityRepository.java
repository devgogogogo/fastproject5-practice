package com.example.board.repository;

import com.example.board.model.entity.LikeEntity;
import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeEntityRepository extends JpaRepository<LikeEntity, Long> {


    List<LikeEntity> findByUser(UserEntity user);

    List<LikeEntity> findByPost(PostEntity post);

   Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

}
