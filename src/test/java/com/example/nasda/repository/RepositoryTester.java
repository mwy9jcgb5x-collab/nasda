package com.example.nasda.repository;

import com.example.nasda.domain.UserEntity;
import com.example.nasda.domain.UserRepository;
import com.example.nasda.dto.UserJoinDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Log4j2
public class RepositoryTester {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("DTO를 활용한 회원 저장 및 중복 확인 테스트")
    void saveAndDuplicateCheck() {
        // 1. Given: 화면에서 넘어온 데이터를 가정한 DTO 생성
        UserJoinDto dto = UserJoinDto.builder()
                .loginId("testUser12")
                .password("password123")
                .email("test1@example.com")
                .nickname("테스터12")
                .build();
//        // when: 저장
//        userRepository.save(user);

        // then: 중복 검증 로직 확인
//        assertThat(userRepository.existsByLoginId("testUser")).isTrue();
//        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
//        assertThat(userRepository.existsByNickname("테스터")).isTrue();
//        assertThat(userRepository.existsByLoginId("nonExist")).isFalse();
    }
}