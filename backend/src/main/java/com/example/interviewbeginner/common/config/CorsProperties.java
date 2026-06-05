package com.example.interviewbeginner.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CORS 配置属性，通过 application.yml 外部化。
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * 允许的前端来源，多个用逗号分隔。
     */
    private String allowedOrigins = "http://localhost:5173";
}
