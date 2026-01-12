package com.example.nasda.service;

import com.example.nasda.domain.UserEntity;
import com.example.nasda.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 생성자 주입을 위해 추가
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // SecurityConfig에서 등록한 빈 주입

    // LoginService.java 예시
    public UserEntity login(String username, String password) {
        // 유저 조회 및 비밀번호 검증 로직...
        UserEntity user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user; // 성공 시 유저 객체 반환
    }
}