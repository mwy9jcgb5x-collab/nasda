package com.example.nasda.controller;

import com.example.nasda.domain.UserEntity;
import com.example.nasda.dto.UserJoinDto;
import com.example.nasda.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final UserService userService;

    /**
     * 회원가입 페이지 이동
     */
    @GetMapping("/signup")
    public String signupForm() {
        return "members/signup"; // 회원가입 HTML 파일 경로
    }

    /**
     * 회원가입 로직 처리
     */
//    @PostMapping("/signup")
//    public String signup(UserEntity user, RedirectAttributes redirectAttributes) {
//        try {
//            log.info("회원가입 시도: loginId={}", user.getLoginId());
//
//            // 서비스 호출하여 DB 저장 및 비밀번호 암호화 진행
//            userService.join(user);
//
//            // 성공 메시지 전달 (리다이렉트 후에도 데이터 유지)
//            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
//            return "redirect:/login";
//
//        } catch (IllegalStateException e) {
//            log.error("회원가입 실패: {}", e.getMessage());
//
//            // 에러 메시지를 담아서 다시 회원가입 폼으로 이동
//            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//            return "redirect:/signup";
//        }
//    }
    @PostMapping("/signup")
    public String signup(UserJoinDto user, RedirectAttributes redirectAttributes) {
        // 1. 데이터가 잘 넘어오는지 확인
        System.out.println("넘어온 아이디: " + user.getLoginId());
        System.out.println("넘어온 이메일: " + user.getEmail());

        try {
            userService.join(user);
            return "redirect:/login";
        } catch (Exception e) {
            // 2. 에러가 발생했다면 콘솔에 출력
            e.printStackTrace();
            return "redirect:/signup";
        }
    }

    /**
     * 로그인 페이지 이동
     */
    @GetMapping("/login")
    public String loginForm() {
        return "members/login"; // 로그인 HTML 파일 경로
    }
}