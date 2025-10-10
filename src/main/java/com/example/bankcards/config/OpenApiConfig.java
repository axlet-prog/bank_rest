package com.example.bankcards.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Bank REST API", version = "v1"),
    // Эта настройка применяет схему "bearerAuth" ко всем эндпоинтам глобально
    security = @SecurityRequirement(name = "bearerAuth")
)
// Эта аннотация описывает саму схему безопасности, которую мы будем использовать
@SecurityScheme(
    name = "bearerAuth", // Имя схемы, которое мы будем использовать для ссылки на нее
    type = SecuritySchemeType.HTTP, // Тип схемы - HTTP
    bearerFormat = "JWT", // Формат токена (информационное поле)
    scheme = "bearer" // Схема аутентификации - Bearer
    )
public class OpenApiConfig {

}
