package com.example.weather.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    INVALIDED_DATE("유효하지 않은 날짜입니다.");

    private final String description;
}
