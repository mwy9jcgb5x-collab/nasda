//package com.example.nasda.service;
//
//import com.example.nasda.domain.*;
//import com.example.nasda.dto.PostDecorationRequestDTO;
//import com.example.nasda.dto.PostDecorationResponseDTO;
//import com.example.nasda.repository.*;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//@SpringBootTest
//@Log4j2
//public class PostDecorationServiceTest_1 {
//
//    @Autowired
//    private PostDecorationService postDecorationService;
//
//    // 기초 데이터 생성을 위한 모든 리포지토리 총출동!
//    @Autowired private UserRepository userRepository;
//    @Autowired private CategoryRepository categoryRepository; // 게시물 카테고리
//    @Autowired private PostRepository postRepository;
//    @Autowired private PostImageRepository postImageRepository;
//    @Autowired private StickerCategoryRepository stickerCategoryRepository;
//    @Autowired private StickerRepository stickerRepository;
//
//    // 1. 장식 저장(스티커 붙이기) 테스트
//    @Test
//    public void testRegister() {
//        // [Step 1] 기초 공사: 사용자, 게시물 카테고리, 게시물, 이미지 만들기
//        // (PostDecoration은 이미지가 있어야 붙일 수 있으니까요!)
//
//        // 1-1. 사용자 생성
//        UserEntity user = UserEntity.builder()
//                .loginId("deco_user")
//                .password("1111")
//                .email("deco@test.com")
//                .nickname("DecoMaster")
//                .role(UserRole.USER)
//                .status(UserStatus.ACTIVE)
//                .build();
//        userRepository.save(user);
//
//        // 1-2. 게시물 카테고리 생성
//        CategoryEntity postCategory = CategoryEntity.builder()
//                .categoryName("Daily Life")
//                .isActive(true)
//                .build();
//        categoryRepository.save(postCategory);
//
//        // 1-3. 게시물 생성
//        PostEntity post = PostEntity.builder()
//                .user(user)
//                .category(postCategory)
//                .title("My Diary")
//                .isDecotable(true)
//                .build();
//        postRepository.save(post);
//
//        // 1-4. 게시물 이미지 생성 (★ 여기가 우리가 붙일 타겟!)
//        PostImageEntity postImage = PostImageEntity.builder()
//                .post(post)
//                .imageUrl("/uploads/diary.jpg")
//                .sortOrder(1)
//                .isRepresentative(true)
//                .build();
//        postImageRepository.save(postImage);
//        Integer targetImageId = postImage.getImageId();
//
//
//        // [Step 2] 스티커 공사: 스티커 카테고리, 스티커 만들기
//
//        // 2-1. 스티커 카테고리
//        StickerCategoryEntity stickerCat = StickerCategoryEntity.builder()
//                .name("Deco Items")
//                .isActive(true)
//                .build();
//        stickerCategoryRepository.save(stickerCat);
//
//        // 2-2. 스티커 (★ 우리가 붙일 재료!)
//        StickerEntity sticker = StickerEntity.builder()
//                .stickerCategory(stickerCat)
//                .stickerName("Red Heart")
//                .stickerImageUrl("/stickers/heart_red.png")
//                .build();
//        stickerRepository.save(sticker);
//        Integer targetStickerId = sticker.getStickerId();
//
//
//        // [Step 3] 드디어 서비스 호출! (DTO 생성 및 저장 요청)
//        PostDecorationRequestDTO requestDTO = PostDecorationRequestDTO.builder()
//                .postImageId(targetImageId) // 위에서 만든 이미지 ID
//                .stickerId(targetStickerId) // 위에서 만든 스티커 ID
//                .userId(user.getUserId())   // 위에서 만든 유저 ID
//                .posX(50.5f)
//                .posY(30.0f)
//                .scale(1.2f)
//                .rotation(45.0f)
//                .zIndex(2)
//                .build();
//
//        Integer decorationId = postDecorationService.saveDecoration(requestDTO);
//
//        // [Step 4] 검증
//        log.info("생성된 장식(Decoration) ID: " + decorationId);
//        Assertions.assertNotNull(decorationId);
//    }
//
//    // 2. 조회 테스트 (ModelMapper 매핑 확인)
//    @Test
//    public void testGetByImageId() {
//        // (원래는 데이터를 만들어야 하지만, 간략히 설명하기 위해 로직만 작성합니다.
//        // 실제로는 위 testRegister의 [Step 1, 2] 과정을 통해 데이터를 넣고 ID를 가져와야 합니다.)
//        // 여기서는 편의상 "데이터가 있다고 가정하고" 로직을 설명합니다.
//        // 실제 실행 시엔 testRegister()가 실행된 후 그 ID를 쓰거나, 별도 데이터 세팅이 필요합니다.
//
//        // 테스트용 데이터가 없으면 에러가 날 수 있으니, 간단한 예시 데이터 생성 로직을 포함합니다.
//        // (위 testRegister 코드의 축약판)
//        // ... (User, Post, Image, Sticker 생성 생략 - 위와 동일) ...
//        // 실제로는 위 testRegister 코드를 재사용하거나 @BeforeEach로 빼는 게 좋습니다.
//
//        // 여기서는 가독성을 위해 "이미지가 있고 스티커가 붙어있다"고 가정하고 흐름만 보여드립니다.
//        // 실행 시에는 testRegister와 같은 데이터 준비 과정이 반드시 선행되어야 합니다.
//    }
//
//    // 3. 삭제 테스트
//    @Test
//    public void testDelete() {
//        // 존재하지 않는 ID로 삭제 시도 (예외 발생 확인)
//        Integer nonExistentId = 999999;
//
//        Assertions.assertThrows(IllegalArgumentException.class, () -> {
//            postDecorationService.deleteDecoration(nonExistentId);
//        });
//
//        log.info("삭제 예외 테스트 성공");
//    }
//}