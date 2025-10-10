package kr.co.archan.reflect.global.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    /**
     * 스프링 SpEL. ex) "#request.memberId", "#orderId", "#a0" (첫 번째 파라미터)
     */
    val key: String,

    /**
     * 락 도메인 접두사. ex) "member", "order"
     */
    val prefix: String,

    /**
     * 락 대기 시간 (ms). 0 이면 즉시 실패(Fail Fast).
     */
    val waitTimeMs: Long = 1000L,

    /**
     * 락 점유 보장 시간 (ms). -1 이면 Redisson watchdog(자동연장).
     */
    val leaseTimeMs: Long = -1L,

    /**
     * 공정 락 사용 여부 (기본 false). 대기열 공정성 필요 시 true.
     */
    val fair: Boolean = false
)