package com.store.popup.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 필요에 따라 ObjectMapper 설정 추가
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);  // 날짜를 타임스탬프로 쓰지 않기
        objectMapper.findAndRegisterModules();  // 추가 모듈 자동 등록 (JavaTimeModule 등)

        return objectMapper;
    }
}
