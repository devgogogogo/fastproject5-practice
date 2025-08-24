package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.reply.ReplyNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.reply.Reply;
import com.example.board.model.reply.ReplyPatchRequestBody;
import com.example.board.model.reply.ReplyRequestBody;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.ReplyEntityRepository;
import com.example.board.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyEntityRepository replyEntityRepository;
    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public List<Reply> getRepliesByPostId(Long postId) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        List<ReplyEntity> replyEntityList = replyEntityRepository.findByPost(postEntity);
        List<Reply> list = replyEntityList.stream().map(Reply::from).toList();
//        List<Reply> list = replyEntityList.stream().map(entity -> Reply.from(entity)).toList();//.map(Reply::from) 를 쉽게 더 풀면
        return list;

//       stream을 쉽게 풀어서 코딩한것
//        List<Reply> list = new ArrayList<>();
//        for (ReplyEntity entity : replyEntityList) {
//            Reply reply = Reply.from(entity);
//            list.add(reply);
//        }
//        return list;
    }


    @Transactional
    public Reply createReply(Long postId, ReplyRequestBody replyPostRequestBody, UserEntity currentUser) {

        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        ReplyEntity replyEntity = ReplyEntity.of(replyPostRequestBody.body(), currentUser, postEntity);

        ReplyEntity savedReply = replyEntityRepository.save(replyEntity);

        Reply reply = Reply.from(savedReply);
        postEntity.setRepliesCount(postEntity.getRepliesCount() + 1);


        return reply;
    }

    public Reply updateReply(Long postId, Long replyId, ReplyPatchRequestBody replyPatchRequestBody, UserEntity currentUser) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        //필요 없지만 그럼에도 postId를 받은 이유는 보드서비스에서 모든 댓글은 게시물과 함께 보여지기 때문에
        //게시물이 존재하지 않는데 댓글을 수정하거나 할 필요가 없어서 게시물 검증 용도로만 사용했다.

        ReplyEntity replyEntity = replyEntityRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException(replyId));

        if (!replyEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }
        replyEntity.setBody(replyPatchRequestBody.body());
        ReplyEntity saved = replyEntityRepository.save(replyEntity);
        Reply reply = Reply.from(saved);
        return reply;
    }

    @Transactional
    public void deleteReply(Long postId, Long replyId, UserEntity currentUser) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        ReplyEntity replyEntity = replyEntityRepository.findById(replyId).orElseThrow(() -> new ReplyNotFoundException(replyId));

        if (!replyEntity.getUser().equals(currentUser)) {
            throw new UserNotAllowedException();
        }
        replyEntityRepository.delete(replyEntity);

        postEntity.setRepliesCount(Math.max(0, postEntity.getRepliesCount() - 1));  //댓글 개수 감소 시키고
        postEntityRepository.save(postEntity); //포스트DB에 저장해야함.

        //여기서는 postEntityRepository.save(postEntity) 를 안 해주고 끝났죠.
        //그런데도 실제로 DB에 반영됩니다.
        // 왜냐면: postEntity는 postEntityRepository.findById(...) 로 가져온 엔티티 → 이미 영속 상태(persistent state).
        //메서드 전체가 @Transactional 안에서 실행되고 있으니까,
        //트랜잭션이 끝날 때 JPA가 변경 감지(dirty checking) 를 해서
        //replies_count 값이 바뀌었으면 자동으로 UPDATE 쿼리를 날려줍니다.
        //즉, save() 안 해도 JPA가 알아서 반영해줘요.
    }

    public List<Reply> getRepliesByUser(String username) {

        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        List<ReplyEntity> replyEntities = replyEntityRepository.findByUser(userEntity);

        return replyEntities.stream().map(Reply::from).toList();
    }
}
