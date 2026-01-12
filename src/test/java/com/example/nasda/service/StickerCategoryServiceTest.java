package com.example.nasda.service;

import com.example.nasda.domain.StickerCategoryEntity;
import com.example.nasda.dto.StickerCategoryRequestDTO;
import com.example.nasda.dto.StickerCategoryResponseDTO;
import com.example.nasda.repository.StickerCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class StickerCategoryServiceTest {

    @InjectMocks // 테스트 대상 (주인공)
    private StickerCategoryServiceImpl stickerCategoryService;

    @Mock // 가짜 저장소 (조수)
    private StickerCategoryRepository stickerCategoryRepository;

    @Spy // 진짜 객체지만 스파이처럼 심어놓음 (ModelMapper는 로직이 복잡해서 Mock보다 Spy가 유리)
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        // 실제 코드(RootConfig)와 똑같이 설정을 맞춰줍니다.
        // 이게 없으면 private 필드에 접근 못해서 매핑이 안 될 수도 있어요!
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
    }

    @Test
    @DisplayName("카테고리 생성 성공: 중복된 이름이 없으면 저장된다")
    void createCategory_Success() {
        // Given
        String categoryName = "Retro";
        StickerCategoryRequestDTO requestDTO = StickerCategoryRequestDTO.builder()
                .name(categoryName)
                .isActive(true)
                .build();

        // 1. 중복 검사 통과 시나리오: "찾아봤는데 없더라(Empty)"
        given(stickerCategoryRepository.findByName(categoryName))
                .willReturn(Optional.empty());

        // 2. 저장 시나리오: "저장하면 ID 1번 줄게"
        StickerCategoryEntity savedEntity = StickerCategoryEntity.builder()
                .stickerCategoryId(1)
                .name(categoryName)
                .isActive(true)
                .build();

        given(stickerCategoryRepository.save(any(StickerCategoryEntity.class)))
                .willReturn(savedEntity);

        // When
        Integer resultId = stickerCategoryService.createCategory(requestDTO);

        // Then
        assertThat(resultId).isEqualTo(1);
        verify(stickerCategoryRepository, times(1)).save(any(StickerCategoryEntity.class));
    }

    @Test
    @DisplayName("카테고리 생성 실패: 이미 존재하는 이름이면 예외 발생")
    void createCategory_Fail_Duplicate() {
        // Given
        String duplicateName = "Retro";
        StickerCategoryRequestDTO requestDTO = StickerCategoryRequestDTO.builder()
                .name(duplicateName)
                .build();

        StickerCategoryEntity existingEntity = StickerCategoryEntity.builder()
                .stickerCategoryId(5)
                .name(duplicateName)
                .build();

        // 시나리오: "어? 그 이름 이미 DB에 있던데?"
        given(stickerCategoryRepository.findByName(duplicateName))
                .willReturn(Optional.of(existingEntity));

        // When & Then
        assertThatThrownBy(() -> stickerCategoryService.createCategory(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 카테고리 이름");

        // 중요: 저장은 절대 호출되면 안 됨!
        verify(stickerCategoryRepository, never()).save(any(StickerCategoryEntity.class));
    }

    @Test
    @DisplayName("전체 조회: Entity 목록이 DTO 목록으로 잘 변환되어야 한다")
    void getAllCategories_Test() {
        // Given
        StickerCategoryEntity cat1 = StickerCategoryEntity.builder().stickerCategoryId(1).name("A").isActive(true).build();
        StickerCategoryEntity cat2 = StickerCategoryEntity.builder().stickerCategoryId(2).name("B").isActive(false).build();

        given(stickerCategoryRepository.findAll()).willReturn(List.of(cat1, cat2));

        // When
        List<StickerCategoryResponseDTO> result = stickerCategoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(2);

        // ModelMapper가 이름(name)과 활성상태(isActive)를 잘 옮겼는지 확인
        assertThat(result.get(0).getName()).isEqualTo("A");
        assertThat(result.get(0).getIsActive()).isTrue();

        assertThat(result.get(1).getName()).isEqualTo("B");
        assertThat(result.get(1).getIsActive()).isFalse();
    }

    @Test
    @DisplayName("삭제 실패: ID가 없으면 예외 발생")
    void deleteCategory_Fail_NotFound() {
        // Given
        Integer wrongId = 999;
        given(stickerCategoryRepository.findById(wrongId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> stickerCategoryService.deleteCategory(wrongId))
                .isInstanceOf(IllegalArgumentException.class);

        // 삭제 명령이 실행되지 않았는지 확인
        verify(stickerCategoryRepository, never()).delete(any());
    }
}