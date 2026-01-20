package com.example.nasda.controller;

import com.example.nasda.dto.manager.CategoryDTO;
import com.example.nasda.dto.manager.CommentReportDTO;
import com.example.nasda.dto.manager.ForbiddenWordDTO;
import com.example.nasda.dto.manager.PostReportDTO;
import com.example.nasda.service.manager.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequestMapping("/admin")
@Log4j2
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String adminMain(Model model,
                            @RequestParam(value = "section", defaultValue = "users") String section,
                            @RequestParam(value = "type", defaultValue = "post") String type,
                            @RequestParam(value = "postPage", defaultValue = "0") int postPage,
                            @RequestParam(value = "commentPage", defaultValue = "0") int commentPage,
                            @RequestParam(value = "wordPage", defaultValue = "0") int wordPage,    // 추가
                            @RequestParam(value = "catPage", defaultValue = "0") int catPage) {    // 추가
        log.info("대시보드 실행 - 섹션: {}, 타입: {}", section, type);

        try {
            model.addAttribute("section", section);
            model.addAttribute("type", type);

            // 1. 페이징 설정 (최신순 정렬)
            Pageable postPageable = PageRequest.of(postPage, 10, Sort.by("reportId").descending());
            Pageable commentPageable = PageRequest.of(commentPage, 10, Sort.by("reportId").descending());
            Pageable wordPageable = PageRequest.of(wordPage, 10, Sort.by("wordId").descending());
            Pageable catPageable = PageRequest.of(catPage, 10, Sort.by("categoryId").descending());

            // 2. 계정 목록
            model.addAttribute("userList", adminService.getUserStatusList());

            // 3. 신고 목록 페이징 처리
            Page<PostReportDTO> postReportPage = adminService.getPendingPostReports(postPageable);
            model.addAttribute("postReportList", postReportPage.getContent());
            model.addAttribute("postCurrentPage", postReportPage.getNumber());
            model.addAttribute("postTotalPages", postReportPage.getTotalPages());

            Page<CommentReportDTO> commentReportPage = adminService.getPendingCommentReports(commentPageable);
            model.addAttribute("commentReportList", commentReportPage.getContent());
            model.addAttribute("commentCurrentPage", commentReportPage.getNumber());
            model.addAttribute("commentTotalPages", commentReportPage.getTotalPages());

            // 4. 금지어 목록 페이징 처리
            Page<ForbiddenWordDTO> wordPageResult = adminService.getBannedWords(wordPageable);
            model.addAttribute("wordList", wordPageResult.getContent());
            model.addAttribute("wordCurrentPage", wordPageResult.getNumber());
            model.addAttribute("wordTotalPages", wordPageResult.getTotalPages());

            // 5. 카테고리 목록 페이징 처리
            Page<CategoryDTO> catPageResult = adminService.getCategories(catPageable);
            model.addAttribute("categoryList", catPageResult.getContent());
            model.addAttribute("catCurrentPage", catPageResult.getNumber());
            model.addAttribute("catTotalPages", catPageResult.getTotalPages());

        } catch (Exception e) {
            log.error("데이터 로딩 중 오류 발생: " + e.getMessage());
            model.addAttribute("categoryList", Collections.emptyList());
            model.addAttribute("wordList", Collections.emptyList());
            model.addAttribute("postReportList", Collections.emptyList());
            model.addAttribute("commentReportList", Collections.emptyList());
            model.addAttribute("userList", Collections.emptyList());
        }

        return "admin/dashboard";
    }

    @PostMapping("/report/process")
    public String processReport(@RequestParam("reportId") Integer reportId,
                                @RequestParam("action") String action,
                                @RequestParam("type") String type,
                                RedirectAttributes rttr) {
        adminService.processPostReport(reportId, action, "관리자 승인 처리");
        rttr.addAttribute("section", "reports");
        rttr.addAttribute("type", type);
        rttr.addFlashAttribute("result", "processed");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/register")
    public String registerGET(@RequestParam(value = "type", required = false, defaultValue = "word") String type, Model model) {
        model.addAttribute("type", type);
        return "admin/register";
    }

    @PostMapping("/register")
    public String registerPost(@RequestParam("type") String type, CategoryDTO categoryDTO, ForbiddenWordDTO wordDTO, RedirectAttributes rttr) {
        String section = "category".equals(type) ? "categories" : "banned";
        try {
            if ("category".equals(type)) {
                adminService.registerCategory(categoryDTO);
            } else if ("word".equals(type)) {
                adminService.registerWord(wordDTO);
            }
            rttr.addFlashAttribute("result", "success");
        } catch (RuntimeException e) {
            log.error("등록 중 중복 발생: " + e.getMessage());
            rttr.addFlashAttribute("error", e.getMessage());
        }
        rttr.addAttribute("section", section);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/modify")
    public String modifyGET(@RequestParam(value = "type", required = false) String type, @RequestParam(value = "id", required = false) Integer id, Model model) {
        if (type == null || id == null) return "redirect:/admin/dashboard";
        model.addAttribute("type", type);
        if ("category".equals(type)) model.addAttribute("dto", adminService.readOneCategory(id));
        else if ("word".equals(type)) model.addAttribute("dto", adminService.readOneWord(id));
        return "admin/modify";
    }

    @PostMapping("/modify")
    public String modifyPost(@RequestParam("type") String type, CategoryDTO categoryDTO, ForbiddenWordDTO wordDTO, RedirectAttributes rttr) {
        String section = "";
        if ("category".equals(type)) {
            adminService.modifyCategory(categoryDTO);
            section = "categories";
        } else if ("word".equals(type)) {
            adminService.modifyWord(wordDTO);
            section = "banned";
        }
        rttr.addAttribute("section", section);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("type") String type, @RequestParam("id") Integer id, RedirectAttributes rttr) {
        String section = "";
        if ("word".equals(type)) {
            adminService.removeWord(id);
            section = "banned";
        } else if ("category".equals(type)) {
            adminService.removeCategory(id);
            section = "categories";
        }
        rttr.addAttribute("section", section);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/user-check")
    @ResponseBody
    public java.util.List<java.util.Map<String, Object>> checkUserStatus() {
        return adminService.getUserStatusList();
    }
}