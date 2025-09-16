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
            val raw = (ex.cause?.message ?: ex.message).orEmpty()

            val (title, detail) = when {
                raw.contains("expired", true) ->
                    "Token_Expired" to "토큰이 만료되었습니다."

                raw.contains("Invalid issuer", true) || raw.contains("issuer", true) || raw.contains(" iss", true) ->
                    "Invalid_Issuer" to "토큰 발급자가 올바르지 않습니다."

                raw.contains("Invalid audience", true) || raw.contains("audience", true) ->
                    "Invalid_Audience" to "토큰 대상(audience)이 올바르지 않습니다."

                raw.contains("signature", true) || raw.contains("JWS", true) || raw.contains("MAC", true) ->
                    "Invalid_Signature" to "토큰 서명이 유효하지 않습니다."

                raw.contains("algorithm", true) ->
                    "Invalid_Algorithm" to "허용되지 않은 서명 알고리즘입니다."

                else ->
                    "Invalid_Token" to "유효하지 않은 토큰입니다."
            }

            val basicErrorResponse = BasicErrorResponse(
                title = title,
                status = HttpStatus.UNAUTHORIZED.value(),
                detail = detail,
                instance = req.requestURI
            )

            // RFC 6750 권장 헤더
            res.setHeader("WWW-Authenticate", """Bearer error="invalid_token", error_description="$title"""")
            res.status = HttpStatus.UNAUTHORIZED.value()
            res.characterEncoding = Charsets.UTF_8.name()
            res.writer.write(objectMapper.writeValueAsString(basicErrorResponse))
        }

    @Bean
    fun accessDeniedHandler(): AccessDeniedHandler =
        AccessDeniedHandler { req, res, _ ->
            val basicErrorResponse = BasicErrorResponse(
                title = "Permission_Denied",
                status = HttpStatus.FORBIDDEN.value(),
                detail = "접근 권한이 존재하지 않습니다.",
                instance = req.requestURI
            )

            res.status = HttpStatus.FORBIDDEN.value()
            res.characterEncoding = Charsets.UTF_8.name()
            res.writer.write(objectMapper.writeValueAsString(basicErrorResponse))
        }
}