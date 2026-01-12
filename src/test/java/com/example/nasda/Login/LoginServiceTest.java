package com.example.nasda.Login;

import com.example.nasda.domain.UserEntity;
import com.example.nasda.domain.UserRepository;
import com.example.nasda.service.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LoginServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("성공: 올바른 아이디와 비밀번호로 로그인")
    void login_Success() {
        // 1. Given: @Builder를 사용하여 객체 생성 (setLoginId 대신 빌더 사용)
        UserEntity user = UserEntity.builder()
                .loginId("testUser")
                .password("password123!")
                .email("test@test.com")
                .nickname("테스터")
                .build(); // 빌더를 사용하면 protected 생성자 문제를 피할 수 있습니다.

        userRepository.save(user);

        // 2. When
// LoginServiceTest.java 수정
        UserEntity response = loginService.login("testUser", "password123!"); // String -> UserEntity
        // 3. Then
        assertThat(response).isEqualTo("Login_Success_Token");
    }
}