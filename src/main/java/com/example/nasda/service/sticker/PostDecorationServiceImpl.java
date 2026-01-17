package com.example.nasda.service.sticker;

import com.example.nasda.domain.*;
import com.example.nasda.dto.sticker.PostDecorationRequestDTO;
import com.example.nasda.dto.sticker.PostDecorationResponseDTO;
import com.example.nasda.repository.PostImageRepository;
import com.example.nasda.repository.sticker.PostDecorationRepository;
import com.example.nasda.repository.sticker.StickerRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostDecorationServiceImpl implements PostDecorationService {

    private final PostDecorationRepository postDecorationRepository;
    private final PostImageRepository postImageRepository;
    private final StickerRepository stickerRepository;
    private final EntityManager entityManager; // ✅ UserRepository 대신 사용

    @Override
    @Transactional
    public List<PostDecorationResponseDTO> saveDecorations(PostDecorationRequestDTO requestDTO) {

        // 1. 기존 장식 벌크 삭제 (데드락 방지)
        postDecorationRepository.deleteByUserAndImageBulk(
                requestDTO.getUserId(), requestDTO.getPostImageId());

        // 2. ✨ 데드락 방지 핵심: 삭제 즉시 DB 반영 및 캐시 비우기
        entityManager.flush();
        entityManager.clear();

        // 3. 비우기 대응: 리스트가 없으면 삭제만 수행 후 종료
        if (requestDTO.getDecorations() == null || requestDTO.getDecorations().isEmpty()) {
            log.info("✨ [CRUD: Delete] 이미지 ID={} 스티커 초기화 완료", requestDTO.getPostImageId());
            return List.of();
        }

        // 4. 공통 정보 조회 (이미지 존재 확인)
        PostImageEntity postImage = postImageRepository.findById(requestDTO.getPostImageId())
                .orElseThrow(() -> new IllegalArgumentException("이미지 없음"));

        // 5. UserRepository 없이 사용자 정보 연결 (ID만 가진 가짜 객체 생성)
        UserEntity decorator = entityManager.getReference(UserEntity.class, requestDTO.getUserId());

        // 6. 스티커 정보 조회 최적화
        List<Integer> stickerIds = requestDTO.getDecorations().stream()
                .map(PostDecorationRequestDTO.DecorationItem::getStickerId)
                .distinct()
                .collect(Collectors.toList());

        List<StickerEntity> stickerEntities = stickerRepository.findAllById(stickerIds);
        Map<Integer, StickerEntity> stickerMap = stickerEntities.stream()
                .collect(Collectors.toMap(StickerEntity::getStickerId, s -> s));

        // 7. 엔티티 변환 및 저장
        List<PostDecorationEntity> entities = requestDTO.getDecorations().stream()
                .map(item -> {
                    StickerEntity sticker = stickerMap.get(item.getStickerId());
                    if (sticker == null) throw new IllegalArgumentException("존재하지 않는 스티커");

                    return PostDecorationEntity.builder()
                            .post(postImage.getPost())
                            .postImage(postImage)
                            .user(decorator)
                            .sticker(sticker)
                            .posX(item.getPosX())
                            .posY(item.getPosY())
                            .scale(0.43f) // ✅ 0.43 사이즈 고정
                            .rotation(item.getRotation())
                            .zIndex(10)
                            .build();
                })
                .collect(Collectors.toList());

        List<PostDecorationEntity> savedEntities = postDecorationRepository.saveAll(entities);

        return savedEntities.stream()
                .map(PostDecorationResponseDTO::from)
                .collect(Collectors.toList());
    }

    // ✅ 인터페이스 미구현 에러를 해결하기 위한 updateDecoration 메서드 추가
    @Override
    @Transactional
    public void updateDecoration(Integer decorationId, PostDecorationRequestDTO.DecorationItem updateDTO, Integer currentUserId) {
        PostDecorationEntity decoration = postDecorationRepository.findById(decorationId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 장식이 존재하지 않습니다."));

        // 권한 체크: 스티커를 붙인 본인인지 확인
        if (!decoration.getUser().getUserId().equals(currentUserId)) {
            throw new SecurityException("자신이 붙인 스티커만 수정할 수 있습니다.");
        }

        // 데이터 갱신 (Dirty Checking 활용)
        decoration.changePosition(
                updateDTO.getPosX(),
                updateDTO.getPosY(),
                0.43f, // 수정 시에도 0.43 고정
                updateDTO.getRotation()
        );
        log.info("장식 수정 완료: ID={}", decorationId);
    }

    @Override
    @Transactional
    public void deleteDecoration(Integer decorationId, Integer currentUserId) {
        PostDecorationEntity decoration = postDecorationRepository.findById(decorationId)
                .orElseThrow(() -> new IllegalArgumentException("장식 없음"));

        // 본인 혹은 게시글 주인 확인 후 삭제
        if (currentUserId.equals(decoration.getUser().getUserId()) ||
                currentUserId.equals(decoration.getPost().getUser().getUserId())) {
            postDecorationRepository.delete(decoration);
            log.info("장식 개별 삭제 성공: ID {}", decorationId);
        } else {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
    }

    @Override
    public List<PostDecorationResponseDTO> getDecorationsByImageId(Integer imageId) {
        return postDecorationRepository.findByPostImage_ImageId(imageId).stream()
                .map(PostDecorationResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDecorationResponseDTO> getDecorationsByPostId(Integer postId) {
        return postDecorationRepository.findByPostPostId(postId).stream()
                .map(PostDecorationResponseDTO::from)
                .collect(Collectors.toList());
    }
}