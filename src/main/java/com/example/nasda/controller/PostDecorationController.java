//package com.example.nasda.controller;
//
//import com.example.nasda.dto.PostDecorationRequestDTO;
//import com.example.nasda.dto.PostDecorationResponseDTO;
//import com.example.nasda.service.PostDecorationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * 꾸미기 API 컨트롤러
// * 역할: 게시글 상세 페이지(View)에서 자바스크립트(AJAX) 요청을 받아 처리
// * 특징: 화면(HTML)이 아닌 데이터(JSON)를 반환함 (@RestController)
// */
//@Log4j2
//@RestController
//@RequestMapping("/api/decorations")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "*") // 개발 편의를 위해 CORS 허용
//public class PostDecorationController {
//
//    private final PostDecorationService postDecorationService;
//
//    /**
//     * 1. 스티커 붙이기 (저장)
//     * [사용 시점] 상세 페이지에서 스티커 위치를 잡고 '저장' 버튼을 눌렀을 때 호출
//     * [요청 URL] POST /api/decorations
//     */
//    @PostMapping
//    public ResponseEntity<Integer> saveDecoration(@RequestBody PostDecorationRequestDTO requestDTO) {
//        log.info("✨ [꾸미기 저장] 게시글 이미지(ID={}) 위에 스티커(ID={}) 부착 요청",
//                requestDTO.getPostImageId(), requestDTO.getStickerId());
//
//        Integer savedId = postDecorationService.saveDecoration(requestDTO);
//
//        log.info("✅ [저장 완료] 생성된 장식 ID: {}", savedId);
//        return ResponseEntity.ok(savedId);
//    }
//
//    /**
//     * 2. 꾸미기 조회 (이미지별)
//     * [사용 시점] 게시글 상세 페이지가 로딩될 때, 이미지 위에 붙은 스티커들을 불러오기 위해 호출
//     * [요청 URL] GET /api/decorations/image/{imageId}
//     */
//    @GetMapping("/image/{imageId}")
//    public ResponseEntity<List<PostDecorationResponseDTO>> getDecorations(@PathVariable Integer imageId) {
//        // 상세 페이지 내에서는 여러 이미지가 있을 수 있으므로, 각 이미지마다 이 API를 호출하게 됩니다.
//        log.debug("🔍 [꾸미기 조회] 이미지 ID={} 에 부착된 스티커 목록 조회", imageId);
//
//        List<PostDecorationResponseDTO> decorations = postDecorationService.getDecorationsByImageId(imageId);
//
//        return ResponseEntity.ok(decorations);
//    }
//
//    /**
//     * 3. 스티커 떼기 (삭제)
//     * [사용 시점] 상세 페이지 편집 모드에서 붙은 스티커의 'X' 버튼을 눌렀을 때 호출
//     * [요청 URL] DELETE /api/decorations/{decorationId}
//     */
//    @DeleteMapping("/{decorationId}")
//    public ResponseEntity<String> deleteDecoration(@PathVariable Integer decorationId) {
//        log.info("🗑️ [꾸미기 삭제] 장식 ID={} 삭제 요청", decorationId);
//
//        postDecorationService.deleteDecoration(decorationId);
//
//        return ResponseEntity.ok("성공적으로 삭제되었습니다.");
//    }
//}