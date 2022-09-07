package com.ll.exam.app10;

import com.ll.exam.app10.app.home.controller.HomeController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc // MockMVC사용을 위한 어노테이션
@Transactional // 여기서 수행된 것은 실제 db에 반영되지 않는다.
public class GenFileServiceTests {
    @Autowired
    private MockMvc mvc; // Controller 테스트

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
}
