package com.example.nasda.controller;

import com.example.nasda.dto.post.HomePostDto;
import com.example.nasda.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostApiController {

    private final PostService postService;

    @GetMapping
    public Page<HomePostDto> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        int safePage = Math.max(page - 1, 0);

        PageRequest pageable = PageRequest.of(
                safePage,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return postService.getHomePostsByCategory(category, pageable);
    }
}
