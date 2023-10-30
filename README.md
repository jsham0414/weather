# weather
제로베이스 2차 개인 실습 프로젝트 Weather api

Swagger의 경우 강의에서 진행중인 버전과 달라 http://localhost:8080/swagger-ui/index.html 로 접속 가능합니다.

diary 테이블 생성 쿼리입니다.
```sql
CREATE TABLE `diary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `weather` varchar(50) NOT NULL,
  `icon` varchar(50) NOT NULL,
  `temperature` double NOT NULL,
  `text` varchar(500) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
```

date_weather 테이블 생성 쿼리입니다.
```sql
CREATE TABLE `date_weather` (
  `date` date NOT NULL,
  `weather` varchar(50) NOT NULL,
  `icon` varchar(50) NOT NULL,
  `temperature` double NOT NULL,
  PRIMARY KEY (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
```
