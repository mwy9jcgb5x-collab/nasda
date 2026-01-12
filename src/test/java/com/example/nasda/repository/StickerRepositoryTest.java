package com.example.nasda.repository;

import com.example.nasda.domain.StickerCategoryEntity;
import com.example.nasda.domain.StickerEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DataJpaTest
// 지난번처럼 실제 DB를 사용하기 위한 설정 (H2 대신 MariaDB/MySQL 사용)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StickerRepositoryTest {

    @Autowired
    private StickerRepository stickerRepository;

    @Autowired
    private TestEntityManager entityManager; // 연관된 데이터(카테고리)를 미리 넣기 위한 도구

    @Test
    @DisplayName("스티커 저장 및 조회 테스트: 카테고리와 함께 정상적으로 저장되어야 한다")
    void saveAndFindSticker_Test() {
        // [1. Given: 기초 공사 (카테고리 만들기)]
        // 스티커는 카테고리가 없으면 저장이 안 되므로(Not Null), 먼저 만들어서 영속화합니다.
        StickerCategoryEntity category = StickerCategoryEntity.builder()
                .name("Cute Animals")
                .isActive(true)
                .build();

        // repository.save() 대신 entityManager를 쓰면 테스트 데이터를 더 순수하게 준비할 수 있습니다.
        entityManager.persist(category);

        // [2. Given: 스티커 생성]
        StickerEntity sticker = StickerEntity.builder()
                .stickerCategory(category) // 위에서 만든 카테고리 연결
                .stickerName("Smiling Cat")
                .stickerImageUrl("/stickers/cat_smile.png")
                .build();

        // [3. When: 저장]
        StickerEntity savedSticker = stickerRepository.save(sticker);

        // [4. Then: 저장 검증]
        // ID가 생성되었는지 확인
        assertThat(savedSticker.getStickerId()).isNotNull();
        log.info("저장된 스티커 ID: {}", savedSticker.getStickerId());

        // [5. When: 조회]
        // 저장된 ID로 다시 DB에서 찾아옵니다.
        Optional<StickerEntity> foundStickerOptional = stickerRepository.findById(savedSticker.getStickerId());

        // [6. Then: 조회 검증]
        assertThat(foundStickerOptional).isPresent(); // 데이터가 있어야 함

        StickerEntity foundSticker = foundStickerOptional.get();

        // 입력한 정보가 그대로 있는지 확인
        assertThat(foundSticker.getStickerName()).isEqualTo("Smiling Cat");
        assertThat(foundSticker.getStickerImageUrl()).isEqualTo("/stickers/cat_smile.png");

        // 연관관계 확인: 스티커를 통해 카테고리 이름까지 알 수 있어야 함
        assertThat(foundSticker.getStickerCategory().getName()).isEqualTo("Cute Animals");

        log.info("테스트 성공! 조회된 스티커 이름: {}, 카테고리: {}",
                foundSticker.getStickerName(), foundSticker.getStickerCategory().getName());
    }
}