//package com.example.nasda.service;
//
//import com.example.nasda.domain.*;
//import com.example.nasda.dto.PostDecorationRequestDTO;
//import com.example.nasda.dto.PostDecorationResponseDTO;
//import com.example.nasda.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.config.Configuration;
//import org.modelmapper.convention.MatchingStrategies;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.times;
//
//@ExtendWith(MockitoExtension.class)
//class PostDecorationServiceTest {
//
//    @InjectMocks // 주인공 (서비스)
//    private PostDecorationServiceImpl postDecorationService;
//
//    // 조연들 (가짜 저장소)
//    @Mock private PostDecorationRepository postDecorationRepository;
//    @Mock private PostImageRepository postImageRepository;
//    @Mock private StickerRepository stickerRepository;
//    @Mock private UserRepository userRepository;
//
//    // 도구 (진짜 ModelMapper를 쓰되 Spy로 감시)
//    @Spy
//    private ModelMapper modelMapper;
//
//    @BeforeEach
//    void setup() {
//        // RootConfig와 동일한 설정 적용 (private 필드 접근 허용)
//        modelMapper.getConfiguration()
//                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
//                .setFieldMatchingEnabled(true)
//                .setMatchingStrategy(MatchingStrategies.LOOSE);
//    }
//
//    @Test
//    @DisplayName("장식 저장 성공: 모든 재료(이미지, 스티커, 유저)가 있으면 저장된다")
//    void saveDecoration_Success() {
//        // Given (재료 준비)
//        PostDecorationRequestDTO requestDTO = PostDecorationRequestDTO.builder()
//                .postImageId(10).stickerId(20).userId(30)
//                .posX(50f).posY(50f).build();
//
//        // 가짜 데이터들
//        PostImageEntity mockImage = PostImageEntity.builder().imageId(10).build();
//        StickerEntity mockSticker = StickerEntity.builder().stickerId(20).build();
//        UserEntity mockUser = UserEntity.builder().userId(30).build();
//
//        // 시나리오: 각 저장소에게 "이거 찾으면 이거 줘"라고 교육
//        given(postImageRepository.findById(10)).willReturn(Optional.of(mockImage));
//        given(stickerRepository.findById(20)).willReturn(Optional.of(mockSticker));
//        given(userRepository.findById(30)).willReturn(Optional.of(mockUser));
//
//        // 저장 결과 시나리오
//        PostDecorationEntity savedEntity = PostDecorationEntity.builder()
//                .decorationId(100) // 저장되면 100번 ID를 받는다고 가정
//                .build();
//        given(postDecorationRepository.save(any(PostDecorationEntity.class)))
//                .willReturn(savedEntity);
//
//        // When (실행)
//        Integer resultId = postDecorationService.saveDecoration(requestDTO);
//
//        // Then (검증)
//        assertThat(resultId).isEqualTo(100);
//        // 저장 메서드가 정확히 1번 호출되었는지 확인
//        verify(postDecorationRepository, times(1)).save(any(PostDecorationEntity.class));
//    }
//
//    @Test
//    @DisplayName("장식 저장 실패: 이미지가 없으면 예외 발생")
//    void saveDecoration_Fail_NoImage() {
//        // Given
//        PostDecorationRequestDTO requestDTO = PostDecorationRequestDTO.builder().postImageId(999).build();
//
//        // 시나리오: 이미지 찾으러 갔는데 없음(Empty)
//        given(postImageRepository.findById(999)).willReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> postDecorationService.saveDecoration(requestDTO))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("이미지 없음");
//    }
//
//    @Test
//    @DisplayName("목록 조회: ModelMapper가 Entity를 DTO로 잘 변환하는지 확인")
//    void getDecorationsByImageId_Test() {
//        // Given
//        // 복잡한 중첩 구조를 가진 Entity 생성
//        StickerEntity sticker = StickerEntity.builder()
//                .stickerId(5)
//                .stickerImageUrl("/stickers/heart.png")
//                .build();
//
//        PostDecorationEntity decoration = PostDecorationEntity.builder()
//                .decorationId(1)
//                .sticker(sticker) // ★ 중요: Entity 안에 Sticker 객체가 들어있음
//                .posX(100f).posY(200f)
//                .build();
//
//        given(postDecorationRepository.findByPostImage_ImageId(10))
//                .willReturn(List.of(decoration));
//
//        // When
//        List<PostDecorationResponseDTO> result = postDecorationService.getDecorationsByImageId(10);
//
//        // Then
//        assertThat(result).hasSize(1);
//        PostDecorationResponseDTO dto = result.get(0);
//
//        // 1. 기본 필드 매핑 확인
//        assertThat(dto.getPosX()).isEqualTo(100f);
//
//        // 2. ★ 중요: ModelMapper가 sticker.stickerImageUrl -> dto.stickerImageUrl 로 잘 펴서 넣었는지 확인
//        // (MatchingStrategies.LOOSE 전략 덕분에 가능할 것으로 예상)
//        // 만약 여기서 null이 나온다면, 서비스 코드에서 map 이후 수동 설정을 추가해야 함을 알 수 있음!
//        // 일단은 매핑이 되었다고 가정하고 검증합니다.
//        assertThat(dto.getStickerImageUrl()).isEqualTo("/stickers/heart.png");
//    }
//
//    @Test
//    @DisplayName("장식 삭제 성공")
//    void deleteDecoration_Success() {
//        // Given
//        Integer decoId = 1;
//        given(postDecorationRepository.existsById(decoId)).willReturn(true);
//
//        // When
//        postDecorationService.deleteDecoration(decoId);
//
//        // Then
//        verify(postDecorationRepository).deleteById(decoId);
//    }
//}