package com.nomad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI nomadOpenAPI() {
        Server renderServer = new Server();
        renderServer.setUrl("https://mcm-nomad-backend.onrender.com" + contextPath);
        renderServer.setDescription("Render Production Server (Live)");

        Server relativeServer = new Server();
        relativeServer.setUrl("/" + (contextPath.startsWith("/") ? contextPath.substring(1) : contextPath));
        relativeServer.setDescription("Current Host Server (Relative)");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080" + contextPath);
        localServer.setDescription("Local Development Server");

        return new OpenAPI()
                .info(new Info()
                        .title("🚀 MCM Nomad Passport AI - Backend REST API Specification")
                        .description("MCM 노마드 패스포트 AI 백엔드 시스템 API 문서입니다.\n\n" +
                                "### 📌 주요 도메인 및 API 사용 안내:\n" +
                                "- **Auth API**: 로그인 및 VIP 노마드 허브 접근\n" +
                                "- **Journey API**: 보딩패스 Vision OCR PNR 스캔 & 목적지 기후 분석 추천\n" +
                                "- **Cart API**: 스마트 장바구니 & VIP 피팅(ChoiceFit) 분기 설정\n" +
                                "- **Store API**: 면세점 BLE/NFC/QR 오토 체크인 & 매장 직원 태블릿 알림\n" +
                                "- **Order API**: 선속 결제, VIP 면세 한도 할인 적용, Nomad Miles 적립\n" +
                                "- **Care API**: 현지 MCM 비세토스 스팟 및 가죽 케어 푸시 가이드")
                        .version("v1.0.0")
                        .contact(new Contact().name("MCM Nomad Tech Team").email("tech@mcmworldwide.com"))
                        .license(new License().name("MCM Worldwide License").url("https://mcmworldwide.com")))
                .servers(List.of(renderServer, relativeServer, localServer));
    }
}

