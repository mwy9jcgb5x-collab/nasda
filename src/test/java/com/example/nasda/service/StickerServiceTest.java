package com.example.nasda.service;

import com.example.nasda.domain.StickerCategoryEntity;
import com.example.nasda.domain.StickerEntity;
import com.example.nasda.dto.StickerRequestDTO;
import com.example.nasda.dto.StickerResponseDTO;
import com.example.nasda.repository.StickerCategoryRepository;
import com.example.nasda.repository.StickerRepository;
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
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class) // 1. Mockito 환경에서 테스트 실행
class StickerServiceTest {

    @InjectMocks // 2. 가짜(Mock) 부품들을 이 서비스(본체)에 주입합니다.
    private StickerServiceImpl stickerService;

    @Mock // 3. 가짜 저장소 (실제 DB에 안 감)
    private StickerRepository stickerRepository;

    @Mock // 3. 가짜 카테고리 저장소
    private StickerCategoryRepository stickerCategoryRepository;

    @Spy // 4. 진짜 같은 가짜 (실제 객체를 쓰되, 필요하면 조작 가능)
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        // RootConfig와 동일한 ModelMapper 설정을 테스트에도 적용
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.LOOSE);
    }

    @Test
    @DisplayName("스티커 등록 성공: 카테고리가 존재하면 정상 저장되어야 한다")
    void createSticker_Success() {
        // Given (준비)
        Integer categoryId = 10;
        StickerRequestDTO requestDTO = StickerRequestDTO.builder()
                .stickerCategoryId(categoryId)
                .stickerName("Happy Dog")
                .stickerImageUrl("/dog.png")
                .build();

        StickerCategoryEntity mockCategory = StickerCategoryEntity.builder()
                .stickerCategoryId(categoryId)
                .name("Animals")
                .build();

        // "DB에서 아이디 10번 찾으면, 이 가짜 카테고리를 줘!"라고 조수에게 시나리오 전달
        given(stickerCategoryRepository.findById(categoryId))
                .willReturn(Optional.of(mockCategory));

        // "저장하면, ID가 1인 스티커가 나왔다고 쳐!"
        StickerEntity savedSticker = StickerEntity.builder()
                .stickerId(1)
                .stickerCategory(mockCategory)
                .stickerName("Happy Dog")
                .build();

        given(stickerRepository.save(any(StickerEntity.class)))
                .willReturn(savedSticker);

        // When (실행)
        Integer resultId = stickerService.createSticker(requestDTO);

        // Then (검증)
        assertThat(resultId).isEqualTo(1);

        // 실제로 리포지토리의 save가 호출되었는지 확인 (검증)
        verify(stickerRepository, times(1)).save(any(StickerEntity.class));
    }

    @Test
    @DisplayName("스티커 등록 실패: 존재하지 않는 카테고리 ID면 예외가 발생해야 한다")
    void createSticker_Fail_NoCategory() {
        // Given
        Integer wrongCategoryId = 999;
        StickerRequestDTO requestDTO = StickerRequestDTO.builder()
                .stickerCategoryId(wrongCategoryId)
                .build();

        // "999번 찾으면? 텅 빈 거(Empty) 줘."
        given(stickerCategoryRepository.findById(wrongCategoryId))
                .willReturn(Optional.empty());

        // When & Then (실행 및 예외 검증)
        assertThatThrownBy(() -> stickerService.createSticker(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 카테고리");
    }

    @Test
    @DisplayName("카테고리별 스티커 조회: ModelMapper가 DTO로 잘 변환하는지 확인")
    void getStickersByCategoryId_Test() {
        // Given
        Integer categoryId = 10;
        StickerCategoryEntity category = StickerCategoryEntity.builder()
                .stickerCategoryId(categoryId)
                .name("Emotions")
                .build();

        StickerEntity sticker1 = StickerEntity.builder()
                .stickerId(1)
                .stickerCategory(category)
                .stickerName("Smile")
                .build();

        StickerEntity sticker2 = StickerEntity.builder()
                .stickerId(2)
                .stickerCategory(category)
                .stickerName("Sad")
                .build();

        // 가짜 리스트 반환 설정
        given(stickerRepository.findByStickerCategory_StickerCategoryId(categoryId))
                .willReturn(List.of(sticker1, sticker2));

        // When
        List<StickerResponseDTO> result = stickerService.getStickersByCategoryId(categoryId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStickerName()).isEqualTo("Smile");
        // ModelMapper가 연관된 카테고리 이름도 잘 가져왔는지 확인 (DTO에 categoryName 필드가 있다고 가정)
        // assertThat(result.get(0).getCategoryName()).isEqualTo("Emotions");
    }

    @Test
    @DisplayName("스티커 삭제: 존재하면 삭제 메서드를 호출해야 한다")
    void deleteSticker_Success() {
        // Given
        Integer stickerId = 1;
        given(stickerRepository.existsById(stickerId)).willReturn(true);

        // When
        stickerService.deleteSticker(stickerId);

        // Then
        // deleteById가 딱 1번 호출되었는지 감시
        verify(stickerRepository, times(1)).deleteById(stickerId);
    }
}