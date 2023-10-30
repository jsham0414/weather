package com.example.weather.controller;

import com.example.weather.domain.Diary;
import com.example.weather.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "DiaryController", description = "Diary에 관련된 다양한 작업을 수행하는 엔드포인트들을 제공합니다.")
public class DiaryController {
    private final DiaryService diaryService;

    @Operation(summary =  "다이어리 생성", description = "날짜와 텍스트를 받아 다이어리를 생성합니다.")
    @PostMapping("/create/diary")
    Diary createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "날짜 형식 : yyyy-MM-dd") LocalDate date,
            @RequestBody String text) {
        return diaryService.createDiary(date, text);
    }

    @Operation(summary =  "다이어리 조회", description = "하루치의 다이어리를 조회한 값을 전송합니다.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "날짜 형식 : yyyy-MM-dd") LocalDate date) {
        return diaryService.readDiary(date);
    }

    @Operation(summary =  "다이어리 기간 조회", description = "시작일과 종료일 사이의 다이어리를 조회한 값을 전송합니다.")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(
            @RequestParam("start-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "조회할 기간의 첫번째 날", example = "2023-10-22") LocalDate startDate,
            @RequestParam("end-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "조회할 기간의 마지막 날", example = "2023-10-31") LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @Operation(summary =  "다이어리 수정", description = "해당 날짜의 다이어리를 수정합니다.")
    @PutMapping("/update/diary")
    Diary updateDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "날짜 형식 : yyyy-MM-dd") LocalDate date,
            @RequestBody String text) {
        return diaryService.updateDiary(date, text);
    }

    @Operation(summary =  "다이어리 삭제", description = "해당 날짜의 다이어리를 삭제합니다.")
    @DeleteMapping("/delete/diary")
    void deleteDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "날짜 형식 : yyyy-MM-dd") LocalDate date) {
        diaryService.deleteDiary(date);
    }
}
