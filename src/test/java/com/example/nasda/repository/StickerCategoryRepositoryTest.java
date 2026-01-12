package com.example.nasda.repository;

import com.example.nasda.domain.StickerCategoryEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 리얼 DB 사용
class StickerCategoryRepositoryTest {

    @Autowired
    private StickerCategoryRepository stickerCategoryRepository;

    @Test
    @DisplayName("스티커 카테고리 저장 및 조회 테스트: 기본값(Active) 확인")
    void saveCategory_Test() {
        // [1. Given: 카테고리 객체 생성]
        // 빌더 패턴 사용 시, @Builder.Default로 설정한 isActive=true가
        // 값을 명시하지 않았을 때 잘 적용되는지 확인해봅니다.
        StickerCategoryEntity category = StickerCategoryEntity.builder()
                .name("Vintage Mood")
                .build();

        // [2. When: 저장]
        StickerCategoryEntity savedCategory = stickerCategoryRepository.save(category);

        // [3. Then: 저장 검증]
        // ID가 자동 생성되었는지 확인
        assertThat(savedCategory.getStickerCategoryId()).isNotNull();
        log.info("생성된 카테고리 ID: {}", savedCategory.getStickerCategoryId());

        // [4. When: 조회]
        Optional<StickerCategoryEntity> foundCategory = stickerCategoryRepository.findById(savedCategory.getStickerCategoryId());

        // [5. Then: 조회 검증]
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Vintage Mood");

        // 중요: 빌더 디폴트 값(isActive = true)이 잘 들어갔는지 확인
        assertThat(foundCategory.get().getIsActive()).isTrue();

        log.info("카테고리 조회 성공: {}, 활성상태: {}",
                foundCategory.get().getName(), foundCategory.get().getIsActive());
    }
}