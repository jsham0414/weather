package com.example.weather.service;

import com.example.weather.domain.DateWeather;
import com.example.weather.domain.Diary;
import com.example.weather.exception.WeatherException;
import com.example.weather.repository.DateWeatherRepository;
import com.example.weather.repository.DiaryRepository;
import com.example.weather.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// http://api.openweathermap.org/geo/1.0/direct?q={city}&limit=1&appid={key}
// https://api.openweathermap.org/data/3.0/onecall?lat={lat}&lon={lon}&appid={key}

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {
    @AllArgsConstructor
    static class Coordinate {
        double lat, lon;
    }

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    @Value("${openweathermap.key}")
    private String apiKey;

    final private String geoUrl = "https://api.openweathermap.org/geo/1.0/direct?q={city}&limit=1&appid={key}";
    final private String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={key}";

    final private String city = "seoul";

    private Map<String, Object> getWeatherData() {
        // 위치 정보 가져오기
        String geoResult = getCoordinateString();

        // 위치 정보 파싱하기
        Coordinate parsedCoordinate = parseCoordinate(geoResult);

        // 위치 정보 기반 날씨 데이터 가져오기
        String weatherResult = getWeatherString(parsedCoordinate);

        // 파싱된 날씨 데이터 리턴
        return parseWeather(weatherResult);
    }

    @Transactional
    public DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeatherListFromDB.isEmpty()) {
            // 새로 api에서 날씨 정보를 가져와야 한다.
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Diary createDiary(LocalDate date, String text) {
        if (date.isAfter(LocalDate.ofYearDay(3050, 1))) {
            throw new WeatherException(ErrorCode.INVALIDED_DATE);
        }

        log.info("started to create diary");
        DateWeather dateWeather = getDateWeather(date);

        // 파싱된 데이터와 텍스트 DB에 넣기
        Diary newDiary = Diary.builder()
                .weather(dateWeather.getWeather())
                .icon(dateWeather.getIcon())
                .temperature(dateWeather.getTemperature())
                .text(text)
                .date(date)
                .build();

        return diaryRepository.save(newDiary);
    }

    private Coordinate parseCoordinate(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(jsonString);
            jsonObject = (JSONObject) jsonArray.get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Coordinate((double) jsonObject.get("lat"), (double) jsonObject.get("lon"));
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }

    private String getUrlResult(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();

        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private String getCoordinateString() {
        String apiUrl = geoUrl
                .replace("{city}", city)
                .replace("{key}", apiKey);

        return getUrlResult(apiUrl);
    }

    private String getWeatherString(Coordinate coordinate) {
        String apiUrl = weatherUrl
                .replace("{lat}", String.valueOf(coordinate.lat))
                .replace("{lon}", String.valueOf(coordinate.lon))
                .replace("{key}", apiKey);

        return getUrlResult(apiUrl);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional
    public Diary updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        return diaryRepository.save(nowDiary);
    }

    @Transactional
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() {
        DateWeather dateWeather = getWeatherFromApi();
        log.info(dateWeather.toString());
        dateWeatherRepository.save(dateWeather);
    }

    private DateWeather getWeatherFromApi() {
        Map<String, Object> parsedWeather = getWeatherData();

        return DateWeather.builder()
                .date(LocalDate.now())
                .weather(parsedWeather.get("main").toString())
                .icon(parsedWeather.get("icon").toString())
                .temperature((double) parsedWeather.get("temp"))
                .build();
    }
}
