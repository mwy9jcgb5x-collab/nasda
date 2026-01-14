package com.example.nasda.controller;

import com.example.nasda.service.AuthUserService;
import com.example.nasda.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;
    private final AuthUserService authUserService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postService.getHomePosts());

        String nickname = authUserService.getCurrentNicknameOrNull();
        model.addAttribute("username", nickname == null ? "게스트" : nickname);

        model.addAttribute("category", "전체");
        return "index";
    }
}
