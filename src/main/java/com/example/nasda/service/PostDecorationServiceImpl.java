//package com.example.nasda.service;
//
//import com.example.nasda.domain.*;
//import com.example.nasda.dto.PostDecorationRequestDTO;
//import com.example.nasda.dto.PostDecorationResponseDTO;
//import com.example.nasda.repository.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Log4j2
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class PostDecorationServiceImpl implements PostDecorationService {
//
//    private final PostDecorationRepository postDecorationRepository;
//    private final PostImageRepository postImageRepository;
//    private final StickerRepository stickerRepository;
//    private final UserRepository userRepository;
//
//    private final ModelMapper modelMapper;
//
//    /**
//     * 스티커 장식 저장
//     * (이 부분은 변경 없음: ID로 엔티티를 찾아야 하므로 수동 조립이 가장 안전함)
//     */
//    @Override
//    @Transactional
//    public Integer saveDecoration(PostDecorationRequestDTO requestDTO) {
//        PostImageEntity postImage = postImageRepository.findById(requestDTO.getPostImageId())
//                .orElseThrow(() -> new IllegalArgumentException("이미지 없음"));
//
//        StickerEntity sticker = stickerRepository.findById(requestDTO.getStickerId())
//                .orElseThrow(() -> new IllegalArgumentException("스티커 없음"));
//
//        UserEntity user = userRepository.findById(requestDTO.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
//
//        PostDecorationEntity decoration = PostDecorationEntity.builder()
//                .post(postImage.getPost())
//                .postImage(postImage)
//                .user(user)
//                .sticker(sticker)
//                .posX(requestDTO.getPosX())
//                .posY(requestDTO.getPosY())
//                .scale(requestDTO.getScale())
//                .rotation(requestDTO.getRotation())
//                .zIndex(requestDTO.getZIndex())
//                .build();
//
//        return postDecorationRepository.save(decoration).getDecorationId();
//    }
//
//    /**
//     * 목록 조회
//     */
//    @Override
//    public List<PostDecorationResponseDTO> getDecorationsByImageId(Integer imageId) {
//        List<PostDecorationEntity> entities = postDecorationRepository.findByPostImage_ImageId(imageId);
//
//        return entities.stream()
//                .map(entity -> modelMapper.map(entity, PostDecorationResponseDTO.class))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 삭제
//     */
//    @Override
//    @Transactional
//    public void deleteDecoration(Integer decorationId) {
//        if (!postDecorationRepository.existsById(decorationId)) {
//            throw new IllegalArgumentException("삭제할 장식이 없습니다.");
//        }
//        postDecorationRepository.deleteById(decorationId);
//    }
//
//    // convertToResponseDTO 헬퍼 메서드는 이제 필요 없어서 삭제했습니다.
//    // modelMapper.map() 한 줄이면 충분하니까요!
//}