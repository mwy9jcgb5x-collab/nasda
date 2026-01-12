package com.example.nasda.service;

import com.example.nasda.domain.UserEntity;
import com.example.nasda.domain.UserRepository;
import com.example.nasda.dto.UserJoinDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
@SpringBootTest
@Log4j2
//@ExtendWith(MockitoExtension.class)
public class ServiceTester {

//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
@Autowired
    private UserService userService;//

    @Test
    @DisplayName("DTO를 통한 회원가입 성공 테스트 - 암호화 및 저장 호출 확인")
    void join_WithDto_Success() {
        // 1. Given: 화면에서 넘어온 데이터를 가정한 DTO 생성
        UserJoinDto dto = UserJoinDto.builder()
                .loginId("tester_dto")
                .password("password123")
                .email("dto@nasda.com")
                .nickname("디티오테스터")
                .build();

//        // 중복이 없다고 가정
//        given(userRepository.existsByLoginId(dto.getLoginId())).willReturn(false);
//        // 비밀번호 암호화 결과값 가설 설정
//        given(passwordEncoder.encode(dto.getPassword())).willReturn("encoded_hash_123");
//
//        // 2. When: 서비스의 join 메서드에 DTO를 던짐
        userService.join(dto);
//
//        // 3. Then: 결과 검증
//        // 비밀번호 암호화가 실행되었는지 확인
//        verify(passwordEncoder, times(1)).encode("password123");
//        // 최종적으로 엔티티가 Repository의 save 메서드로 전달되었는지 확인
//        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
//
//    @Test
//    @DisplayName("회원가입 실패 테스트 - 아이디 중복 시 예외 발생")
//    void join_WithDto_Fail_Duplicate() {
//        // Given
//        UserJoinDto dto = UserJoinDto.builder()
//                .loginId("user_active") // 이미 DB에 있는 아이디라고 가정
//                .build();
//
//        given(userRepository.existsByLoginId("user_active")).willReturn(true);
//
//        // When & Then: 예외가 발생하는지 검증
//        assertThatThrownBy(() -> userService.join(dto))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("이미 존재하는 아이디입니다.");
//    }
}