package com.example.weather.exception;

import com.example.weather.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public WeatherException(ErrorCode errorCode) {
        super(errorCode.getDescription());

        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
