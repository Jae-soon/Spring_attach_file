package com.ll.exam.app10.app.member.controller;

import com.ll.exam.app10.app.member.entity.MemberEntity;
import com.ll.exam.app10.app.member.service.MemberService;
import com.ll.exam.app10.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

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
    @GetMapping("/modify")
    public String showModify() {
        return "member/modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal MemberContext context, String email, MultipartFile profileImg, String profileImg__delete) {
        MemberEntity member = memberService.getMemberById(context.getId());

        if ( profileImg__delete != null && profileImg__delete.equals("Y") ) {
            memberService.removeProfileImg(member);
        }

        memberService.modify(member, email, profileImg);

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

//    @GetMapping("/profile/img/{id}")
//    public String showProfileImg(@PathVariable Long id) {
//        return "redirect:" + memberService.getMemberById(id).getProfileImgUrl();
//    }

    @GetMapping("/profile/img/{id}")
    public ResponseEntity<Object> showProfileImg(@PathVariable Long id) throws URISyntaxException {
        String profileImgUrl = memberService.getMemberById(id).getProfileImgUrl();

        if ( profileImgUrl == null ) {
            profileImgUrl = "https://via.placeholder.com/100x100.png?text=U_U";
        }

        URI redirectUri = new URI(profileImgUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        httpHeaders.setCacheControl(CacheControl.maxAge(60 * 60 * 1, TimeUnit.SECONDS));
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }

}
