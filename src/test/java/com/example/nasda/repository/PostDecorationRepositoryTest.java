//package com.example.nasda.repository;
//
//import com.example.nasda.domain.*;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class PostDecorationRepositoryTest {
//
//    @Autowired
//    private PostDecorationRepository postDecorationRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    @DisplayName("이미지 ID로 장식 목록 조회 시, 연관된 스티커와 카테고리 정보까지 무결하게 가져와야 한다")
//    void findByPostImage_ImageId_Test() {
//        // [1. 게시물 관련 기초 공사 (Given)]
//
//        // 1-1. 사용자(User) 생성
//        UserEntity user = UserEntity.builder()
//                .loginId("moana_dev")
//                .password("securePass123!")
//                .email("moana@ocean.com")
//                .nickname("Moana")
//                .role(UserRole.USER)
//                .status(UserStatus.ACTIVE)
//                .build();
//        entityManager.persist(user);
//
//        // 1-2. 게시물 카테고리(Category) 생성
//        CategoryEntity postCategory = CategoryEntity.builder()
//                .categoryName("Inspiration")
//                .isActive(true)
//                .build();
//        entityManager.persist(postCategory);
//
//        // 1-3. 게시물(Post) 생성
//        PostEntity post = PostEntity.builder()
//                .user(user)
//                .category(postCategory)
//                .title("나의 스티커 다이어리")
//                .isDecotable(true)
//                .build();
//        entityManager.persist(post);
//
//        // 1-4. 게시물 이미지(PostImage) 생성
//        PostImageEntity postImage = PostImageEntity.builder()
//                .post(post)
//                .imageUrl("/uploads/sample_image.jpg")
//                .sortOrder(1)
//                .isRepresentative(true)
//                .build();
//        entityManager.persist(postImage);
//
//
//        // [2. 스티커 관련 기초 공사 (New!)]
//
//        // 2-1. 스티커 카테고리(StickerCategory) 생성 (★중요: 이게 없으면 스티커 생성 불가)
//        StickerCategoryEntity stickerCategory = StickerCategoryEntity.builder()
//                .name("Emotions") // 감정 표현 스티커
//                .isActive(true)
//                .build();
//        entityManager.persist(stickerCategory);
//
//        // 2-2. 스티커(Sticker) 생성
//        // (보내주신 필드명 stickerImageUrl 반영 완료)
//        StickerEntity sticker = StickerEntity.builder()
//                .stickerCategory(stickerCategory) // 카테고리 연결 필수!
//                .stickerName("Heart Sticker")
//                .stickerImageUrl("/stickers/heart.png")
//                .build();
//        entityManager.persist(sticker);
//
//
//        // [3. 테스트 대상: 장식(Decoration) 데이터 저장]
//        PostDecorationEntity decoration = PostDecorationEntity.builder()
//                .post(post)
//                .postImage(postImage)
//                .user(user)
//                .sticker(sticker)
//                .posX(50.5f)
//                .posY(30.0f)
//                .scale(1.5f)
//                .rotation(90.0f)
//                .zIndex(2)
//                .build();
//
//        postDecorationRepository.save(decoration);
//
//        // 영속성 컨텍스트 초기화 (DB 조회 쿼리 발생 확인용)
//        entityManager.flush();
//        entityManager.clear();
//
//
//        // [4. 실행 (When)]
//        List<PostDecorationEntity> result = postDecorationRepository.findByPostImage_ImageId(postImage.getImageId());
//
//
//        // [5. 검증 (Then)]
//        assertThat(result).hasSize(1);
//
//        PostDecorationEntity foundDecoration = result.get(0);
//
//        // 좌표 검증
//        assertThat(foundDecoration.getPosX()).isEqualTo(50.5f);
//
//        // 스티커 정보 검증 (필드명 수정 반영: getStickerImageUrl)
//        assertThat(foundDecoration.getSticker().getStickerImageUrl()).isEqualTo("/stickers/heart.png");
//
//        // 스티커의 카테고리까지 잘 따라가는지 확인 (Entity Graph 테스트)
//        assertThat(foundDecoration.getSticker().getStickerCategory().getName()).isEqualTo("Emotions");
//    }
//}