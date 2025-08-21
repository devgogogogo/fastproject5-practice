package com.example.board.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Table(
        name = "\"follow\"",
        indexes = {
                @Index(
                        name = "follow_follower_following_idx",
                        columnList = "follower,following",
                        unique = true)
        }
)
//DB 성능을 개선하기 위해 인덱스를 사용함
public class FollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId;

    @Column
    private ZonedDateTime createdDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower")
    private UserEntity follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following")
    private UserEntity following;

    public static FollowEntity of(UserEntity follower, UserEntity following) {
        FollowEntity follow = new FollowEntity();
        follow.setFollower(follower);
        follow.setFollowing(following);
        return follow;
    }

    @PrePersist
    public void prePersist() {
        this.createdDateTime = ZonedDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FollowEntity that)) return false;
        return Objects.equals(followId, that.followId) && Objects.equals(createdDateTime, that.createdDateTime) && Objects.equals(follower, that.follower) && Objects.equals(following, that.following);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followId, createdDateTime, follower, following);
    }
}
