package com.example.nasda.controller;//package com.example.nasda.controller;
//
//import com.example.nasda.domain.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController // @Controller 대신 반드시 @RestController 사용
//@RequestMapping("/api/member")
//@RequiredArgsConstructor
//public class MemberApiController {
//
//    private final UserRepository userRepository;
//
//    @GetMapping("/check-loginId")
//    public boolean checkLoginId(@RequestParam("loginId") String loginId) {
//        return userRepository.existsByLoginId(loginId); // JSON 형식의 true/false가 반환됨
//    }
//}