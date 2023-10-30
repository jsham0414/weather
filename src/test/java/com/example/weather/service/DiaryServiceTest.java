package com.example.weather.service;

import com.example.weather.domain.DateWeather;
import com.example.weather.domain.Diary;
import com.example.weather.exception.WeatherException;
import com.example.weather.repository.DateWeatherRepository;
import com.example.weather.repository.DiaryRepository;
import com.example.weather.type.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// https://doyoung.tistory.com/12

@Transactional
@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {
    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private DateWeatherRepository dateWeatherRepository;

    @InjectMocks
    private DiaryService diaryService;

    @Test
    void diaryCreationSuccess() {
        // given
        List<DateWeather> weatherList = Arrays.asList(
                DateWeather.builder()
                        .date(LocalDate.of(2023, 10, 25))
                        .build(),
                DateWeather.builder()
                        .date(LocalDate.of(2023, 10, 26))
                        .build()
        );

        given(dateWeatherRepository.findAllByDate(any()))
                .willReturn(weatherList);

        given(diaryRepository.save(any()))
                .willReturn(Diary.builder()
                        .text("오늘의 일기")
                        .date(LocalDate.of(2023, 10, 25))
                        .build());

        ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);

        // when
        Diary diary = diaryService.createDiary(LocalDate.of(2023, 10, 25), "오늘의 일기");


        // then
        verify(diaryRepository, times(1)).save(captor.capture());
        assertEquals(captor.getValue().getDate().toString(), "2023-10-25");
    }

    @Test
    void invalidedDateErrorOccurred() {
        // given
        // when
        WeatherException exception = assertThrows(
                WeatherException.class,
                () -> diaryService.createDiary(LocalDate.of(5000, 5, 13), "오늘의 일기")
        );

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.INVALIDED_DATE);
    }
}