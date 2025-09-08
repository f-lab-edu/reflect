package kr.co.archan.reflect.member.controller

import kr.co.archan.reflect.member.dto.request.MemberSignupRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: MemberSignupRequest) {

    }
}