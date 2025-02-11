package br.com.soat.uploadapi.api.config

import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("API")
            .packagesToScan("br.com.soat")
            .build()
    }

    @Bean
    fun httpHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.add("X-Custom-Header", "Custom Value")
        return headers
    }

    @Bean
    fun springDocConfigProperties(): SpringDocConfigProperties {
        return SpringDocConfigProperties()
    }

}