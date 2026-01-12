//package com.example.nasda.repository;
//
//import com.example.nasda.domain.*;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.IntStream;
//
//@SpringBootTest
//@Log4j2
//public class PostDecorationRepositoryTest_1 {
//
//    @Autowired
//    private PostDecorationRepository postDecorationRepository;
//
//    // 기초 공사를 위한 모든 리포지토리 주입
//    @Autowired private UserRepository userRepository;
//    @Autowired private CategoryRepository categoryRepository;
//    @Autowired private PostRepository postRepository;
//    @Autowired private PostImageRepository postImageRepository;
//    @Autowired private StickerCategoryRepository stickerCategoryRepository;
//    @Autowired private StickerRepository stickerRepository;
//
//    @Test
//    public void testInsert() {
//        // [Step 1] 기초 재료 준비
//
//        // 1. 사용자 (Enum 타입 수정: UserRole, UserStatus)
//        UserEntity user = UserEntity.builder()
//                .loginId("test_user_deco")
//                .password("1111")
//                .email("deco@nasda.com")
//                .nickname("DecoUser")
//                .role(UserRole.USER)     // 수정됨
//                .status(UserStatus.ACTIVE) // 수정됨
//                .build();
//        userRepository.save(user);
//
//        // 2. 게시글 카테고리
//        CategoryEntity category = CategoryEntity.builder()
//                .categoryName("Free Board")
//                .isActive(true)
//                .build();
//        categoryRepository.save(category);
//
//        // 3. 게시글
//        PostEntity post = PostEntity.builder()
//                .title("Decorate Test Post")
//                .user(user)
//                .category(category)
//                .isDecotable(true)
//                .build();
//        postRepository.save(post);
//
//        // 4. 게시글 이미지
//        PostImageEntity postImage = PostImageEntity.builder()
//                .post(post)
//                .imageUrl("test_image_01.jpg")
//                .sortOrder(0)
//                .isRepresentative(true)
//                .build();
//        postImageRepository.save(postImage);
//
//        // 5. 스티커 카테고리 & 스티커
//        StickerCategoryEntity stickerCat = StickerCategoryEntity.builder()
//                .name("Emotions")
//                .isActive(true)
//                .build();
//        stickerCategoryRepository.save(stickerCat);
//
//        StickerEntity sticker = StickerEntity.builder()
//                .stickerCategory(stickerCat)
//                .stickerName("Smile Icon")
//                .stickerImageUrl("smile.png")
//                .build();
//        stickerRepository.save(sticker);
//
//
//        // [Step 2] 장식 데이터 대량 등록 (IntStream)
//        IntStream.rangeClosed(1, 10).forEach(i -> {
//            PostDecorationEntity decoration = PostDecorationEntity.builder()
//                    .post(post)
//                    .postImage(postImage)
//                    .user(user)
//                    .sticker(sticker)
//                    .posX((float) (i * 10))
//                    .posY((float) (i * 10))
//                    .scale(1.0f)
//                    .rotation(0.0f)
//                    .zIndex(i)
//                    .build();
//
//            PostDecorationEntity result = postDecorationRepository.save(decoration);
//            log.info("Saved Decoration ID: " + result.getDecorationId());
//        });
//    }
//
//    @Test
//    public void testSelect() {
//        // 테스트 실행 시 DB에 존재하는 실제 ID를 넣어야 합니다.
//        // testInsert를 먼저 실행했다면 ID 1번부터 10번이 있을 것입니다.
//        Integer decorationId = 1;
//
//        Optional<PostDecorationEntity> result = postDecorationRepository.findById(decorationId);
//
//        PostDecorationEntity decoration = result.orElseThrow();
//
//        log.info("------------------------------------------------");
//        log.info("Found Decoration: " + decoration);
//        log.info("Sticker Name: " + decoration.getSticker().getStickerName());
//        log.info("Image URL: " + decoration.getPostImage().getImageUrl());
//        log.info("------------------------------------------------");
//    }
//
//    @Test
//    public void testUpdate() {
//        Integer decorationId = 1;
//
//        Optional<PostDecorationEntity> result = postDecorationRepository.findById(decorationId);
//        PostDecorationEntity decoration = result.orElseThrow();
//
//        // [수정 포인트] Entity에 @Setter가 없으므로 Builder로 덮어쓰기 (Update 쿼리 발생)
//        // 만약 Entity에 change() 같은 편의 메서드가 있다면 그걸 쓰면 됩니다.
//        PostDecorationEntity updatedDecoration = PostDecorationEntity.builder()
//                .decorationId(decoration.getDecorationId()) // ID 유지 (핵심)
//                .post(decoration.getPost())
//                .postImage(decoration.getPostImage())
//                .user(decoration.getUser())
//                .sticker(decoration.getSticker())
//                // 변경할 값들
//                .posX(500.5f)
//                .posY(300.5f)
//                .scale(2.0f)
//                .rotation(45.0f)
//                .zIndex(decoration.getZIndex())
//                .build();
//
//        postDecorationRepository.save(updatedDecoration);
//
//        log.info("Updated Decoration: " + updatedDecoration);
//    }
//
//    @Test
//    public void testDelete() {
//        Integer decorationId = 1;
//
//        postDecorationRepository.deleteById(decorationId);
//
//        log.info("Deleted Decoration ID: " + decorationId);
//    }
//
//    @Test
//    public void testSelectByImage() {
//        // testInsert에서 생성된 이미지의 ID를 사용 (보통 1번일 확률 높음)
//        Integer targetImageId = 1;
//
//        List<PostDecorationEntity> decorationList = postDecorationRepository.findByPostImage_ImageId(targetImageId);
//
//        log.info("Total decorations on Image " + targetImageId + ": " + decorationList.size());
//
//        decorationList.forEach(deco -> {
//            log.info("ID: " + deco.getDecorationId() + " | Pos: (" + deco.getPosX() + "," + deco.getPosY() + ")");
//        });
//    }
//}