package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.LikeEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.LikeEntityRepository;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;

    //반복되는 작업을 메소드로 따로 빼줬음
    private Post getPostWithLikingStatus(PostEntity postEntity, UserEntity currentUser) {
        boolean isLiking = likeEntityRepository.findByUserAndPost(currentUser, postEntity).isPresent();

        return Post.from(postEntity, isLiking);
    }


    public List<Post> getPosts(UserEntity currentUser) {
        List<PostEntity> postEntityList = postEntityRepository.findAll();

        List<Post> list = postEntityList
                .stream()
                .map(postEntity -> getPostWithLikingStatus(postEntity, currentUser))
                .toList();
        return list;
    }

    public Post getPostByPostId(Long postId, UserEntity currentUser) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        return getPostWithLikingStatus(postEntity, currentUser);
    }

    public Post createPost(PostPostRequestBody postPostRequestBody, UserEntity currentUser) {
        PostEntity postEntity = PostEntity.of(postPostRequestBody.body(), currentUser);
        PostEntity savedPostEntity = postEntityRepository.save(postEntity);
        return Post.from(savedPostEntity);
    }

    public Post updatePost(Long postId, PostPatchRequestBody postPatchRequestBody, UserEntity currentUser) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        if (!postEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }


        postEntity.setBody(postPatchRequestBody.body());
        PostEntity updatedPostEntity = postEntityRepository.save(postEntity);
        return Post.from(updatedPostEntity);
    }

    public void deletePost(Long postId, UserEntity currentUser) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        if (!postEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }
        postEntityRepository.delete(postEntity);
    }

    public List<Post> getPostByUsername(String username,UserEntity currentUser) {

        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        List<PostEntity> postEntities = postEntityRepository.findByUser(userEntity);
        List<Post> list = postEntities.stream()
                .map(postEntity-> getPostWithLikingStatus(postEntity,currentUser))
                .toList();
        return list;
    }

    @Transactional
    public Post toggleLike(Long postId, UserEntity currentUser) {

        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        Optional<LikeEntity> likeEntity = likeEntityRepository.findByUserAndPost(currentUser, postEntity);
        if (likeEntity.isPresent()) {
            likeEntityRepository.delete(likeEntity.get());
            postEntity.setLikeCount(Math.max(0, postEntity.getLikeCount() - 1));
            PostEntity saved = postEntityRepository.save(postEntity);
            return Post.from(saved, false);
        } else {
            LikeEntity newLikeEntity = LikeEntity.of(currentUser, postEntity);
            likeEntityRepository.save(newLikeEntity);
            postEntity.setLikeCount(postEntity.getLikeCount() + 1);
            PostEntity saved = postEntityRepository.save(postEntity);
            return Post.from(saved, true);
        }
    }
}
