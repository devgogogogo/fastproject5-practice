package com.example.board.config;


import com.example.board.exception.jwt.JwtTokenNotFoundException;
import com.example.board.service.JwtService;
import com.example.board.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserService userService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        //todo : JWT 검증
        //auThorication 값 추충해서 BEARER_PREFIX 떼서
        String BEARER_PREFIX = "Bearer ";
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        SecurityContext securityContext = SecurityContextHolder.getContext();


        //헤더 형식·중복 인증 체크
        if (!ObjectUtils.isEmpty(authorization) //헤더가 비어 있지 않고
                && authorization.startsWith(BEARER_PREFIX) //"Bearer "로 시작해야 합니다.
                && securityContext.getAuthentication() == null) { //현재 SecurityContext(보안 세션)에 사용자 정보가 아직 안 채워져 있다면

            //→ JWT 검증을 해라.
            String accessToken = authorization.substring(BEARER_PREFIX.length()); //"Bearer " 접두사를 떼고 순수 JWT 문자열만 가져와요.
            String username = jwtService.getUsername(accessToken); //보통 sub(subject) 클레임에서 username을 꺼냅니다.
            UserDetails userDetails = userService.loadUserByUsername(username); //DB나 사용자 저장소에서 해당 사용자를 불러와 권한(roles/authorities)까지 확보.

            UsernamePasswordAuthenticationToken authenticationToken //스프링 시큐리티가 이해하는 Authentication 구현체를 생성.
                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //이제부터 컨트롤러/인터셉터/표현식(@PreAuthorize, @AuthenticationPrincipal)에서 인증된 사용자로 인식.
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
        }

        filterChain.doFilter(request, response); //바통을 다음 필터/서블릿으로 넘깁니다. 이 줄이 없으면 요청이 여기서 멈춰요.
    }
}
