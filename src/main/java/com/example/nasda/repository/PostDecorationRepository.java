package com.example.nasda.repository;

import com.example.nasda.domain.PostDecorationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostDecorationRepository extends JpaRepository<PostDecorationEntity, Integer> {

    // 1. 특정 이미지 위에 붙은 모든 장식(스티커) 조회
    // (가장 많이 쓰일 기능: 화면에 뿌려줄 때)
    @EntityGraph(attributePaths = {"sticker"})
    List<PostDecorationEntity> findByPostImage_ImageId(Integer imageId);

    // 2. 특정 게시물(Post)에 포함된 모든 장식 조회
    // (게시물 전체 삭제나 통계 등을 낼 때 유용)
    List<PostDecorationEntity> findByPost_PostId(Integer postId);

    // 3. 특정 사용자가 붙인 장식 조회 (선택 사항)
    // (예: "내가 꾸민 내역" 보기)
    List<PostDecorationEntity> findByUser_UserId(Integer userId);

    // 4. 특정 이미지의 장식 일괄 삭제
    // (이미지를 수정하거나 지울 때 깔끔하게 청소)
    void deleteByPostImage_ImageId(Integer imageId);
}