package kr.co.archan.reflect.global.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import kr.co.archan.reflect.global.exception.dto.BasicErrorResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler

@Configuration
class SecurityHandlersConfig(
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint =
        AuthenticationEntryPoint { req, res, ex ->
            val basicErrorResponse = BasicErrorResponse(ex, req)

            // RFC 6750 권장 헤더
            res.setHeader("WWW-Authenticate", """Bearer error="invalid_token", error_description="${basicErrorResponse.title}"""")
            res.status = HttpStatus.UNAUTHORIZED.value()
            res.characterEncoding = Charsets.UTF_8.name()
            res.writer.write(objectMapper.writeValueAsString(basicErrorResponse))
        }

    @Bean
    fun accessDeniedHandler(): AccessDeniedHandler =
        AccessDeniedHandler { req, res, ex ->
            val basicErrorResponse = BasicErrorResponse(ex, req)

            res.status = HttpStatus.FORBIDDEN.value()
            res.characterEncoding = Charsets.UTF_8.name()
            res.writer.write(objectMapper.writeValueAsString(basicErrorResponse))
        }
}