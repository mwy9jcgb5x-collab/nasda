package com.example.nasda.controller;

import com.example.nasda.dto.post.HomePostDto;
import com.example.nasda.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final PostService postService;

    @GetMapping("/search")
    public String search(@RequestParam String keyword,
                         @RequestParam(defaultValue = "content") String type,
                         Model model) {

        List<HomePostDto> posts = postService.searchHomePosts(keyword, type);

        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("pageTitle", "검색 결과");

        // ✅ index.html 재사용 (홈과 같은 카드 UI)
        return "index";
    }
}
