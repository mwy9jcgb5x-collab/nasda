package com.example.nasda.dto;

import com.example.nasda.domain.UserEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinDto {

    private String loginId;
    private String password;
    private String nickname;
    private String email;

    // DTO를 엔티티로 변환하는 메서드
    public UserEntity toEntity() {
        return UserEntity.builder()
                .loginId(this.loginId)
                .password(this.password)
                .nickname(this.nickname)
                .email(this.email)
                .build();
    }
}