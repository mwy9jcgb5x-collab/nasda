package com.example.nasda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDecorationRequestDTO {

    // 1. 어떤 이미지 위에 붙일지 (필수)
    private Integer postImageId;

    // 2. 어떤 스티커를 사용할지 (필수)
    private Integer stickerId;

    // 3. 누가 붙이는지 (로그인 구현 전 테스트용)
    private Integer userId;

    // 4. 꾸미기 속성 (위치, 크기, 회전, 순서)
    private Float posX;
    private Float posY;

    @Builder.Default
    private Float scale = 1.0f;

    @Builder.Default
    private Float rotation = 0.0f;

    @Builder.Default
    private Integer zIndex = 1;
}