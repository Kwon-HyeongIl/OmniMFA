package com.khi.securityservice.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .version("v1.0.0")
                .title("OmniMFA Security API")
                .description("OmniMFA 보안 서비스 관련 RESTful API 명세서입니다.")
                .contact(new Contact()
                        .name("Kwon HyeongIl")
                        .email("llioopv@naver.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 3. Swagger UI에 'Authorize' 자물쇠 버튼 추가
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 4. API 요청 헤더에 JWT 인증 토큰을 포함하도록 설정
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                // 5. 위에서 정의한 Info 객체 설정
                .info(info);
    }
}
