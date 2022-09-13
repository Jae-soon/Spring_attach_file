package com.ll.exam.app10;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.app10.app.home.controller.HomeController;
import com.ll.exam.app10.app.member.controller.MemberController;
import com.ll.exam.app10.app.member.entity.MemberEntity;
import com.ll.exam.app10.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc // MockMVC사용을 위한 어노테이션
@Transactional // 여기서 수행된 것은 실제 db에 반영되지 않는다.
@ActiveProfiles({"base-addi", "test"})
public class GenFileServiceTests {
    @Autowired
    private MockMvc mvc; // Controller 테스트

    @Autowired
    private MemberService memberService;

    @Value("${custom.genFileDirPath}")
    private String genFileDirPath;


    @Test
    @DisplayName("메인화면에서는 안녕이 나와야 한다.")
    void t1() throws Exception {
        // when
        ResultActions resultActions = mvc
                // 무언가를 실행하고 싶을 때
                .perform(get("/")) // get요청(GET : localhost:8080/)일 때
                .andDo(print()); // 화면에 나오는 것을 콘솔에 출력해라

        // then
        resultActions
                .andExpect(status().is2xxSuccessful()) // http 응답 코드
                .andExpect(handler().handlerType(HomeController.class)) // HomeController의 내용
                .andExpect(handler().methodName("main")) // HomeController의 메서드 중 main
                .andExpect(content().string(containsString("안녕"))); // main 메서드로 매핑된 html파일 내에서 "안녕"이 포함되어 있는지 확인
    }

    @Test
    @DisplayName("회원의 수")
    void t2() throws Exception {
        long count = memberService.count();
        assertThat(count).isGreaterThan(0);
    }

    @Test
    @DisplayName("user1로 로그인 후 프로필페이지에 접속하면 user1의 이메일이 보여야 한다.")
    void t3() throws Exception {
        // WHEN
        // GET /
        ResultActions resultActions = mvc
                .perform(
                        get("/member/profile") // 접속하겠다.
                                .with(user("user1").password("1234").roles("user")) // 아이디 : user1 / 비밀번호 : 1234 인 상태로
                )
                .andDo(print());

        // THEN
        // 안녕
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showProfile"))
                .andExpect(content().string(containsString("user1@test.com")));
    }

    @Test
    @DisplayName("user4로 로그인 후 프로필 페이지에 접속하면 user4의 이메일이 보여야 한다.")
    void t4() throws Exception {
        // WHEN
        // GET /
        ResultActions resultActions = mvc
                .perform(
                        get("/member/profile") // 접속하겠다.
                                .with(user("user4").password("1234").roles("user")) // 아이디 : user1 / 비밀번호 : 1234 인 상태로
                )
                .andDo(print());

        // THEN
        // 안녕
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("showProfile"))
                .andExpect(content().string(containsString("user4@test.com")));
    }

    @Test
    @DisplayName("회원가입")
    void t5() throws Exception {
        String testUploadFileUrl = "https://picsum.photos/200/300";
        String originalFileName = "test.png";

        // wget
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Resource> response = restTemplate.getForEntity(testUploadFileUrl, Resource.class);
        InputStream inputStream = response.getBody().getInputStream();

        MockMultipartFile profileImg = new MockMultipartFile(
                "profileImg",
                originalFileName,
                "image/png",
                inputStream
        );

        // 회원가입(MVC MOCK)
        // when
        ResultActions resultActions = mvc.perform(
                        multipart("/member/join")
                                .file(profileImg)
                                .param("username", "user5")
                                .param("password", "1234")
                                .param("email", "user5@test.com")
                                .characterEncoding("UTF-8"))
                .andDo(print());

        // 5번회원이 생성되어야 함, 테스트
        // 여기 마저 구현
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/profile"))
                .andExpect(handler().handlerType(MemberController.class))
                .andExpect(handler().methodName("join"));

        MemberEntity member = memberService.getMemberById(5L);

        assertThat(member).isNotNull();

        memberService.removeProfileImg(member);
        // 5번회원의 프로필 이미지 제거
    }
}
