package kr.co.archan.reflect.global.util

import kr.co.archan.reflect.global.exception.common.LockAcquisitionException
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * 분산 락을 프로그래매틱하게 사용할 수 있도록 지원하는 매니저 클래스
 * 
 * 사용 예시:
 * ```
 * distributedLockManager.executeWithLock(
 *     key = email,
 *     prefix = "auth:signup",
 *     waitTime = 3000L,
 *     leaseTime = 5000L
 * ) {
 *     // 락이 필요한 핵심 로직만 여기에 작성
 *     memberRepository.save(member)
 * }
 * ```
 */
@Component
class DistributedLockManager(
    private val redissonClient: RedissonClient
) {

    /**
     * 분산 락을 획득하고 작업을 실행한 후 락을 해제합니다.
     * 
     * @param key 락 키 (prefix 제외)
     * @param prefix 락 키 prefix (기본값: "generic")
     * @param waitTime 락 획득 대기 시간 (ms)
     * @param leaseTime 락 보유 시간 (ms), -1이면 watchdog 사용
     * @param fair 공정 락 사용 여부
     * @param action 락을 획득한 후 실행할 작업
     * @return action의 실행 결과
     * @throws LockAcquisitionException 락 획득 실패 시
     */
    fun <T> executeWithLock(
        key: String,
        prefix: String = "generic",
        waitTime: Long = 1000,
        leaseTime: Long = -1L,
        fair: Boolean = false,
        action: () -> T
    ): T {
        require(key.isNotBlank()) { "락 키가 비어있음" }
        
        val lockKey = "reflect:redisson_lock:$prefix:$key"
        
        val lock: RLock = if (fair) {
            redissonClient.getFairLock(lockKey)
        } else {
            redissonClient.getLock(lockKey)
        }

        var acquired = false
        try {
            // 락 획득
            acquired = if (leaseTime <= 0L) {
                // watchdog 사용
                lock.tryLock(waitTime, -1, TimeUnit.MILLISECONDS)
            } else {
                lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)
            }

            if (!acquired) {
                throw LockAcquisitionException("분산 락 획득 실패: key=$lockKey, wait=${waitTime}ms")
            }

            // 실제 작업 실행
            return action()
        } catch (e: LockAcquisitionException) {
            throw e
        } catch (e: Throwable) {
            throw e
        } finally {
            if (acquired && lock.isHeldByCurrentThread) {
                try {
                    lock.unlock()
                } catch (ignored: IllegalMonitorStateException) {
                    // 이미 해제된 경우 무시
                }
            }
        }
    }
}

