package kr.co.archan.reflect.auth.controller

import jakarta.validation.Valid
import kr.co.archan.reflect.auth.dto.request.LoginRequest
import kr.co.archan.reflect.auth.dto.request.SignUpRequest
import kr.co.archan.reflect.auth.dto.response.LoginResponse
import kr.co.archan.reflect.auth.dto.response.SignUpResponse
import kr.co.archan.reflect.auth.service.AuthService
import org.springframework.http.HttpStatus
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
        return ResponseEntity.ok().body(LoginResponse(accessToken = result.accessToken.value, refreshToken = result.refreshToken.value))
    }

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: SignUpRequest) : ResponseEntity<SignUpResponse> {
        val result = authService.signUpMember(request.email, request.password, request.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(SignUpResponse(accessToken = result.accessToken.value, refreshToken = result.refreshToken.value))
    }
}