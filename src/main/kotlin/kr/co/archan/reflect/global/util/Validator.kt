package kr.co.archan.reflect.global.util

import java.net.IDN

object Validator {

    fun isEmailValid(email: String): Boolean {
        if (email.isBlank() || email.length > 254 || email.contains(' ')) return false

        // 도메인 분리
        val parts = email.split("@")
        if (parts.size != 2) return false
        val localPart = parts[0]
        val domainPart = parts[1]

        // 로컬파트 공백 체크
        if (localPart.isEmpty()) return false

        // 도메인은 아스키 코드로 변환
        val asciiDomain = try {
            IDN.toASCII(domainPart, IDN.ALLOW_UNASSIGNED)
        } catch (e: IllegalArgumentException) {
            return false
        }

        // 도메인 공백 체크
        if (asciiDomain.isEmpty()) return false

        // 도메인 비정상적인 . 검증
        if (asciiDomain.startsWith(".") || asciiDomain.endsWith(".")) return false
        if (".." in asciiDomain) return false

        // 도메인 최소 두 개의 레이블 필요 (ex: example.com)
        val domainLabels = asciiDomain.split(".")
        if (domainLabels.size < 2) return false

        // TLD 최소 2글자 (예: com, net)
        val tld = domainLabels.last()
        if (tld.length < 2 || !tld.all { it.isLetter() }) return false

        // 로컬파트 길이 제한 (RFC 규정)
        if (localPart.length > 64) return false

        // 로컬파트 정규식: RFC 기본 허용 문자 (실용적 제한)
        val localRegex = Regex("^[A-Za-z0-9._%+\\-]+\$")
        if (!localRegex.matches(localPart)) return false

        // 도메인 정규식: 알파벳, 숫자, 하이픈, 점
        val domainRegex = Regex("^[A-Za-z0-9.-]+\$")
        if (!domainRegex.matches(asciiDomain)) return false

        return true
    }

    fun isNameValid(name: String): Boolean =
        name.isNotBlank() && name.trim().length in 1..20

    fun isPasswordValid(password: String): Boolean {
        if (password.length < 8 || password.length > 64) return false
        
        if (!password.any { it.isDigit() }) return false
        if (!password.any { !it.isLetterOrDigit() && !it.isWhitespace() }) return false
        
        return true
    }
}