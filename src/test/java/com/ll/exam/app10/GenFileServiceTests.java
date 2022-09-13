package com.ll.exam.app10;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.app10.app.home.controller.HomeController;
import com.ll.exam.app10.app.member.controller.MemberController;
import com.ll.exam.app10.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;

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
}
