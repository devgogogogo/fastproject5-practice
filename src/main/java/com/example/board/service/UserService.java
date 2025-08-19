package com.example.board.service;

import com.example.board.exception.user.UserAlreadyExistsException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserSignUpRequestBody;
import com.example.board.repository.UserEntityRepository;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        return user;
    }

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

    public UserAuthenticationResponse authenticate( String username, String password) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username)); //아이디로 유저 엔티티를 찾는다.

        if (passwordEncoder.matches(password, userEntity.getPassword())) {
            String accessToken = jwtService.generateAccessToken(userEntity);
            return new UserAuthenticationResponse(accessToken);
        } else {
            throw new UserNotFoundException();
        }
    }
}
