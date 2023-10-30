package com.example.weather.controller;

import com.example.weather.domain.Diary;
import com.example.weather.service.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {
    @MockBean
    private DiaryService diaryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("다이어리 생성 요청")
    void createDiarySuccess() throws Exception {
        // given
        given(diaryService.createDiary(any(), anyString()))
                .willReturn(Diary.builder()
                        .text("테스트입니다.")
                        .date(LocalDate.of(2023, 10, 30))
                        .build());

        // when

        // then
        mockMvc.perform(post("/create/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("date", "2023-10-30")
                        .content("테스트입니다."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2023-10-30"))
                .andExpect(jsonPath("$.text").value("테스트입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("다이어리 생성 후 수정 요청")
    void updateDiarySuccess() throws Exception {
        // given
        given(diaryService.createDiary(any(), anyString()))
                .willReturn(Diary.builder()
                        .text("수정 전 텍스트입니다.")
                        .date(LocalDate.of(2023, 10, 30))
                        .build());

        given(diaryService.updateDiary(any(), anyString()))
                .willReturn(Diary.builder()
                        .text("수정 후 텍스트입니다.")
                        .date(LocalDate.of(2023, 10, 30))
                        .build());

        // when

        // then
        mockMvc.perform(post("/create/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("date", "2023-10-30")
                        .content("수정 전 텍스트입니다."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2023-10-30"))
                .andExpect(jsonPath("$.text").value("수정 전 텍스트입니다."))
                .andDo(print());

        mockMvc.perform(put("/update/diary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("date", "2023-10-30")
                        .content("수정 후 텍스트입니다."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2023-10-30"))
                .andExpect(jsonPath("$.text").value("수정 후 텍스트입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("다이어리 삭제 요청")
    void deleteDiarySuccess() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/read/diary?date=2023-10-02"))
                .andDo(print());
    }

    @Test
    @DisplayName("같은 날짜 여러 개의 다이어리 조회")
    void readDiarySuccess() throws Exception {
        // given
        List<Diary> diaryList = Arrays.asList(
                Diary.builder()
                        .text("1번 테스트")
                        .date(LocalDate.of(2023, 10, 2))
                        .build(),
                Diary.builder()
                        .text("2번 테스트")
                        .date(LocalDate.of(2023, 10, 2))
                        .build(),
                Diary.builder()
                        .text("3번 테스트")
                        .date(LocalDate.of(2023, 10, 2))
                        .build()
        );

        given(diaryService.readDiary(any()))
                .willReturn(diaryList);

        // when
        // then
        mockMvc.perform(get("/read/diary?date=2023-10-02"))
                .andDo(print())
                .andExpect(jsonPath("$[0].date").value("2023-10-02"))
                .andExpect(jsonPath("$[0].text").value("1번 테스트"))
                .andExpect(jsonPath("$[1].date").value("2023-10-02"))
                .andExpect(jsonPath("$[1].text").value("2번 테스트"))
                .andExpect(jsonPath("$[2].date").value("2023-10-02"))
                .andExpect(jsonPath("$[2].text").value("3번 테스트"))
                .andDo(print());
    }

    @Test
    @DisplayName("다이어리 기간 조회")
    void readDiariesSuccess() throws Exception {
        // given
        List<Diary> diaryList = Arrays.asList(
                Diary.builder()
                        .text("1번 테스트")
                        .date(LocalDate.of(2023, 10, 2))
                        .build(),
                Diary.builder()
                        .text("2번 테스트")
                        .date(LocalDate.of(2023, 10, 3))
                        .build(),
                Diary.builder()
                        .text("3번 테스트")
                        .date(LocalDate.of(2023, 10, 4))
                        .build()
        );

        given(diaryService.readDiaries(any(), any()))
                .willReturn(diaryList);

        // when
        // then
        mockMvc.perform(get("/read/diaries?start-date=2023-10-01&end-date=2023-10-05"))
                .andExpect(jsonPath("$[0].date").value("2023-10-02"))
                .andExpect(jsonPath("$[0].text").value("1번 테스트"))
                .andExpect(jsonPath("$[1].date").value("2023-10-03"))
                .andExpect(jsonPath("$[1].text").value("2번 테스트"))
                .andExpect(jsonPath("$[2].date").value("2023-10-04"))
                .andExpect(jsonPath("$[2].text").value("3번 테스트"))
                .andDo(print());
    }

}