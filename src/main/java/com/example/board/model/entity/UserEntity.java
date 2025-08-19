package com.example.board.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;


@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE \"user\" SET deletedDateTime = CURRENT_TIMESTAMP WHERE userId = ?")
@SQLRestriction("deleteddatetime IS NULL")
@Table(name = "\"user\"")  //PostgreSQL 내부에서 유저라는 이름이 이미 사용되고 있는 예약어이다. --> 그래서 \" \"를 붙여주어야 한다.
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column()
    private String username;

    @Column()
    private String password;

    @Column
    private String profile;

    @Column()
    private String description;

    @Column()
    private ZonedDateTime createdDateTime;

    @Column()
    private ZonedDateTime updatedDateTime;

    @Column
    private ZonedDateTime deletedDateTime;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedDateTime(ZonedDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public void setDeletedDateTime(ZonedDateTime deletedDateTime) {
        this.deletedDateTime = deletedDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserEntity that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(profile, that.profile) && Objects.equals(description, that.description) && Objects.equals(createdDateTime, that.createdDateTime) && Objects.equals(updatedDateTime, that.updatedDateTime) && Objects.equals(deletedDateTime, that.deletedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, profile, description, createdDateTime, updatedDateTime, deletedDateTime);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /*
    계정이 만료되지 않았는지 여부를 리턴
    true -> 계정 사용가능
    false -> 계정 만료됨 ->로그인 막음
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정이 잠겨있지 않은지 여부를 리턴합니다.
    //true → 잠금 안 됨, 로그인 가능
    //false → 계정이 잠김 (로그인 불가)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //사용자 비밀번호(자격 증명)가 만료되지 않았는지 리턴합니다.
    //true → 비밀번호 사용 가능
    //false → 비밀번호 만료 (변경해야 로그인 가능)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정이 활성화 되어 있는지 리턴합니다.
    //true → 활성 계정 (정상 로그인 가능)
    //false → 비활성 계정 (관리자가 정지시킨 경우 등 로그인 불가)
    @Override
    public boolean isEnabled() {
        return true;
    }

    public static UserEntity of(String username, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);

        // Set random profile image url
        //램덤한 프로필 사진 설정(1 ~ 100)
        userEntity.setProfile("https://avatar.iran.liara.run/public/" + new Random().nextInt(100));

        //위 API 가 정상적으로 동작하지 않을 경우, 이것을 사용해주세요.
//        userEntity.setProfile("https://dev-jayce.github.io/public/profile" + new Random().nextInt(100));

        return userEntity;
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
