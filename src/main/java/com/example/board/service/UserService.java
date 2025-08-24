package com.example.board.service;

import com.example.board.exception.follow.FollowAlreadyExistException;
import com.example.board.exception.follow.FollowNotFoundException;
import com.example.board.exception.follow.InvalidFollowException;
import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.user.UserAlreadyExistsException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.FollowEntity;
import com.example.board.model.entity.LikeEntity;
import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.*;
import com.example.board.repository.FollowEntityRepository;
import com.example.board.repository.LikeEntityRepository;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FollowEntityRepository followEntityRepository;
    private final PostEntityRepository postEntityRepository;
    private final LikeEntityRepository likeEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        return user;
    }

    @Transactional
    public User signUp(String username, String password) {
        userEntityRepository
                .findByUsername(username)
                .ifPresent(
                        user -> {
                            throw new UserAlreadyExistsException();
                        });

        UserEntity userEntity = UserEntity.of(username, passwordEncoder.encode(password));
        UserEntity savedUserEntity = userEntityRepository.save(userEntity);

        return User.from(savedUserEntity);
    }

    public UserAuthenticationResponse authenticate(String username, String password) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username)); //아이디로 유저 엔티티를 찾는다.

        if (passwordEncoder.matches(password, userEntity.getPassword())) {
            String accessToken = jwtService.generateAccessToken(userEntity);
            return new UserAuthenticationResponse(accessToken);
        } else {
            throw new UserNotFoundException();
        }
    }

    public List<User> getUsers(String query, UserEntity currentUser) {
        List<UserEntity> userEntities;

        if (query != null && !query.isBlank()) { //쿼리가 값이 있는경우 --> 검색한다.
            //TODO: query검색어 기반, 해당 검색어가, username에 포함되어 있는 유저 목록 가져오기
            userEntities = userEntityRepository.findByUsernameContaining(query);
        } else {
            userEntities = userEntityRepository.findAll();//없으면 그냥 다 가져오기
        }
        return userEntities.stream().map(userEntity -> getUserWithFollowingStatus(userEntity, currentUser)).toList();
    }

    public User getUser(String username, UserEntity currentUser) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return getUserWithFollowingStatus(userEntity, currentUser);
    }

    //반복되는 작업을 메소드화 시킴
    private User getUserWithFollowingStatus(UserEntity userEntity, UserEntity currentUser) {
        boolean isFollowing = followEntityRepository.findByFollowerAndFollowing(currentUser, userEntity).isPresent();

        return User.from(userEntity, isFollowing);
    }

    public User updateUser(String username, UserPatchRequestBody userPatchRequestBody, UserEntity currentUser) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if (!userEntity.equals(currentUser)) {
            throw new UserNotAllowedException();
        }
        if (userPatchRequestBody.description() != null) {
            userEntity.setDescription(userPatchRequestBody.description());
        }
        UserEntity saved = userEntityRepository.save(userEntity);
        return User.from(saved);
    }

    @Transactional
    public User follow(String username, UserEntity currentUser) {
        UserEntity following = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if (following.equals(currentUser)) {
            throw new InvalidFollowException("A user cannot follow themselves!");
        }
        followEntityRepository.findByFollowerAndFollowing(currentUser, following).ifPresent(follow -> {
            throw new FollowAlreadyExistException(currentUser, following);
        });
        FollowEntity followEntity = FollowEntity.of(currentUser, following);
        followEntityRepository.save(followEntity);

        following.setFollowersCount(following.getFollowersCount() + 1);
        currentUser.setFollowingsCount(following.getFollowingsCount() + 1);
        userEntityRepository.save(following);
        userEntityRepository.save(currentUser);
//        userEntityRepository.saveAll(List.of(following, currentUser)); 이렇게도 할 수 있음.

        User user = User.from(following, true);
        return user;
    }

    @Transactional
    public User unFollow(String username, UserEntity currentUser) {
        UserEntity following = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        if (following.equals(currentUser)) {
            throw new InvalidFollowException("A user cannot unfollow themselves!");
        }

        FollowEntity followEntity = followEntityRepository.findByFollowerAndFollowing(currentUser, following).orElseThrow(
                () -> new FollowNotFoundException(currentUser, following));

        followEntityRepository.delete(followEntity);


        following.setFollowersCount(Math.max(0, following.getFollowersCount() - 1));
        currentUser.setFollowingsCount(Math.max(0, following.getFollowingsCount() - 1));

        userEntityRepository.saveAll(List.of(following, currentUser));

        return User.from(following, false);
    }

    public List<Follower> getFollowersByUsername(String username, UserEntity currentUser) {
        UserEntity following = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        List<FollowEntity> followEntities = followEntityRepository.findByFollower(following);
        return followEntities
                .stream()
                .map(follow -> Follower.from(
                        getUserWithFollowingStatus(follow.getFollower(), currentUser), follow.getCreatedDateTime()))
                .toList();
    }

    public List<User> getFollowingsByUser(String username, UserEntity currentUser) {
        UserEntity follower = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        List<FollowEntity> followEntities = followEntityRepository.findByFollower(follower);
        return followEntities
                .stream()
                .map(follow -> getUserWithFollowingStatus(follow.getFollowing(), currentUser))
                .toList();
    }

    public List<LikedUser> getLikedUsersByPostId(Long postId, UserEntity currentUser) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        List<LikeEntity> likeEntities = likeEntityRepository.findByPost(postEntity);
        return likeEntities.stream()
                .map(likeEntity -> getLikedUserWithFollowingStatus(likeEntity, postEntity, currentUser))
                .toList();
    }


    private LikedUser getLikedUserWithFollowingStatus(LikeEntity likeEntity, PostEntity postEntity, UserEntity currentUser) {
        UserEntity likedUserEntity = likeEntity.getUser();
        User userWithFollowingStatus = getUserWithFollowingStatus(likedUserEntity, currentUser);
        return LikedUser.from(userWithFollowingStatus, postEntity.getPostId(), likeEntity.getCreatedDateTime());
    }

    public List<LikedUser> getLikedUsersByUser(String username, UserEntity currentUser) {
        UserEntity userEntityr = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        List<PostEntity> postEntities = postEntityRepository.findByUser(userEntityr);
        return postEntities
                .stream()
                .flatMap(postEntity -> likeEntityRepository.findByPost(postEntity)
                        .stream()
                        .map(likeEntity -> getLikedUserWithFollowingStatus(likeEntity, postEntity, currentUser)))
                .toList();

    }
}
