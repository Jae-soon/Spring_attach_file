package com.ll.exam.app10.app.member.controller;

import com.ll.exam.app10.app.member.entity.MemberEntity;
import com.ll.exam.app10.app.member.service.MemberService;
import com.ll.exam.app10.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin() {
        return "member/join_form";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(HttpServletRequest req, String username, String password, String email, MultipartFile img) throws ServletException {
        MemberEntity oldMember = memberService.getMemberByUsername(username);
        MemberEntity member;

        if (oldMember != null) {
            return "redirect:/?errorMsg=Already done.";
        }

        String passwordClearText = password;

        password = passwordEncoder.encode(password); // 암호화

        member = memberService.join(username, password, email, img);

        req.login(member.getUsername(), passwordClearText);

        return "redirect:/member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        model.addAttribute("memberContext", memberContext);
        return "member/profile";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login() {
        return "member/login_form";
    }

    @GetMapping("/profile/img/{id}")
    public String showProfileImg(@PathVariable Long id) {
        return "redirect:" + memberService.getMemberById(id).getProfileImgUrl();
    }

}
