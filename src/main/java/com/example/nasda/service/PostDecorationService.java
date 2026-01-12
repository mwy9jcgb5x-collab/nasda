package com.example.nasda.service;

import com.example.nasda.dto.PostDecorationRequestDTO;
import com.example.nasda.dto.PostDecorationResponseDTO;

import java.util.List;

public interface PostDecorationService {

    // 1. 스티커 붙이기 (저장)
    Integer saveDecoration(PostDecorationRequestDTO requestDTO);

    // 2. 특정 이미지의 장식 목록 조회
    List<PostDecorationResponseDTO> getDecorationsByImageId(Integer imageId);

    // 3. 장식 삭제 (선택 사항이지만 필수 기능)
    void deleteDecoration(Integer decorationId);
}