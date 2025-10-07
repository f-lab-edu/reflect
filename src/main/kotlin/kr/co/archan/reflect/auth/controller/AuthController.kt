package kr.co.archan.reflect.auth.controller

import jakarta.validation.Valid
import kr.co.archan.reflect.auth.dto.request.LoginRequest
import kr.co.archan.reflect.auth.dto.response.LoginResponse
import kr.co.archan.reflect.auth.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController (
    private val authService: AuthService
){
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest) : ResponseEntity<LoginResponse> {
        val result = authService.loginMember(request.email, request.password)
        return ResponseEntity.ok().body(LoginResponse(result.accessToken.value, result.refreshToken.value))
    }
}