package com.example.board.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.Objects;


/*
@SQLDelete

원래 JPA의 delete() 메서드는 DELETE FROM post ... 쿼리를 날립니다.
그런데 여기서는 DELETE 대신 UPDATE로 deletedDateTime 필드를 현재 시간으로 변경하도록 지정되어 있습니다.
즉, DB에서 행(row)을 삭제하는 대신 "삭제 시간"을 기록합니다.

@SQLRestriction

모든 조회 쿼리에 "deletedDateTime IS NULL" 조건을 자동으로 붙여줍니다.
덕분에 findAll(), findById() 같은 기본 조회 메서드도 삭제된 데이터는 안 나오게 됩니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE \"reply\" SET deleteddatetime = CURRENT_TIMESTAMP WHERE replyid = ?")
@SQLRestriction("deleteddatetime IS NULL")
@Table(
        name = "reply",
        indexes = {@Index(name = "post_userid_idx",columnList = "userid")})
//DB 성능을 개선하기 위해 인덱스를 사용함
public class ReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column
    private ZonedDateTime createdDateTime;

    @Column
    private ZonedDateTime updatedDateTime;

    @Column
    private ZonedDateTime deletedDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postid")
    private PostEntity post;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReplyEntity that)) return false;
        return Objects.equals(replyId, that.replyId) && Objects.equals(body, that.body) && Objects.equals(createdDateTime, that.createdDateTime) && Objects.equals(updatedDateTime, that.updatedDateTime) && Objects.equals(deletedDateTime, that.deletedDateTime) && Objects.equals(user, that.user) && Objects.equals(post, that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replyId, body, createdDateTime, updatedDateTime, deletedDateTime, user, post);
    }

    public static ReplyEntity of(String body, UserEntity user,PostEntity post) {
        ReplyEntity reply = new ReplyEntity();
        reply.setBody(body);
        reply.setUser(user);
        reply.setPost(post);
        return reply;
    }

    @PrePersist
    public void prePersist() {
        this.createdDateTime = ZonedDateTime.now();
        this.updatedDateTime = this.createdDateTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDateTime = ZonedDateTime.now();
    }
}
