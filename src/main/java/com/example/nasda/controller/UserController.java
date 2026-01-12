package com.example.nasda.controller;

import com.example.nasda.domain.UserEntity;
import com.example.nasda.dto.UserJoinDto;
import com.example.nasda.service.UserService;
import com.example.nasda.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final LoginService loginService;

    // 회원가입 페이지 이동
    @GetMapping("/signup")
    public String signupPage() {
        return "user/signup"; // 반드시 회원가입 HTML 파일명(signup.html)을 적어야 합니다!
    }

    @PostMapping("/signup")
    public String signup(UserJoinDto user) {
        userService.join(user);
        return "redirect:/user/login";
    }

    // --- 로그인 ---
    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session, // 세션 주입
                        Model model) {
        try {
            // [수정] loginService.login은 UserEntity를 반환합니다. (이미지 3 확인)
            UserEntity loginUser = loginService.login(username, password);

            if (loginUser != null) {
                // 세션에 "loginUser"라는 이름으로 객체 저장
                session.setAttribute("loginUser", loginUser);
                return "redirect:/"; // 메인으로 이동
            }
            return "user/login";

        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "user/login";
        }
    }

    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        // 세션에서 꺼내기
        UserEntity user = (UserEntity) session.getAttribute("loginUser");

        // 로그인이 안 되어 있을 때만 튕기게 설정
        if (user == null) {
            System.out.println("로그인 세션이 없어서 튕김");
            return "redirect:/user/login";
        }

        model.addAttribute("user", user);
        // 게시물 리스트가 없으면 에러가 날 수 있으므로 빈 리스트라도 넣어줍니다.
        model.addAttribute("myPosts", new java.util.ArrayList<>());
        model.addAttribute("postCount", 0);

        return "user/mypage";
    }
    // --- 프로필 정보 수정 ---
    @PostMapping("/mypage/update")
    public String updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam("email") String email,
                                HttpSession session,
                                Model model) {
        // 1. 세션에서 현재 로그인 유저 정보 확인
        UserEntity user = (UserEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/user/login";

        try {
            // 2. UserService 호출 (Integer 타입의 userId 사용)
            UserEntity updatedUser = userService.updateProfile(user.getUserId(), nickname, email);

            // 3. 중요: 수정된 정보를 다시 세션에 저장하여 정보 갱신
            session.setAttribute("loginUser", updatedUser);

            return "redirect:/user/mypage?success=true";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "수정 중 오류가 발생했습니다: " + e.getMessage());
            return "user/mypage";
        }
    }

    // --- 회원 탈퇴 ---
    @PostMapping("/mypage/delete")
    public String deleteAccount(HttpSession session) {
        // 1. 세션에서 유저 확인
        UserEntity user = (UserEntity) session.getAttribute("loginUser");
        if (user == null) return "redirect:/user/login";

        // 2. 서비스에서 DB 삭제 수행
        userService.deleteUser(user.getUserId());

        // 3. 세션 무효화 (로그아웃 처리)
        session.invalidate();

        return "redirect:/?deleted=true";
    }
}