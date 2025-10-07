package kr.co.archan.reflect.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class PasswordConfig {

    /**
     * Spring Security의 PasswordEncoder 인터페이스와 호환되는 애플리케이션용 비밀번호 인코더.
     *
     * @return Argon2PasswordEncoder 인스턴스
     *
     * 옵션 설명:
     *  - saltLength: 생성되는 솔트의 길이(바이트 단위, 권장 16B)
     *  - hashLength: 결과 해시의 길이(바이트 단위, 권장 32B)
     *  - parallelism: 해시 연산 시 사용할 병렬 처리 스레드 수(일반적으로 서버 앱에서는 1 권장)
     *  - memory: 해시 연산에 사용할 메모리 크기(KiB 단위, 예: 64 * 1024 = 64MiB)
     *  - iterations: 반복 횟수(시간 비용 요소, 예: 3)
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        val saltLength = 16
        val hashLength = 32
        val parallelism = 1
        val memory = 64 * 1024 // KiB (64 MiB)
        val iterations = 3

        return Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations)
    }

}