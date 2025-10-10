package kr.co.archan.reflect.global.aop

import kr.co.archan.reflect.global.annotation.DistributedLock
import kr.co.archan.reflect.global.exception.base.ProductException
import kr.co.archan.reflect.global.exception.common.LockAcquisitionException
import kr.co.archan.reflect.global.util.LockKeyEvaluator
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Order(Ordered.HIGHEST_PRECEDENCE) // 락을 최우선으로 설정한다
@Aspect
@Component
class DistributedLockAspect(
    private val redissonClient: RedissonClient,
    private val keyEvaluator: LockKeyEvaluator
) {

    @Around("@annotation(distributedLock)")
    fun around(pjp: ProceedingJoinPoint, distributedLock: DistributedLock): Any? {
        val method = (pjp.signature as MethodSignature).method
        val args = pjp.args

        // 1) 키 생성
        val evaluated = keyEvaluator.evaluateKey(distributedLock.key, method, args)
        require(evaluated.isNotBlank()) { "락 키가 비어있음" }
        val prefix = distributedLock.prefix.takeIf { it.isNotBlank() } ?: "generic"
        val lockKey = "reflect:redisson_lock:$prefix:$evaluated"

        // 2) 락 객체
        val lock: RLock = if (distributedLock.fair) {
            redissonClient.getFairLock(lockKey)
        } else {
            redissonClient.getLock(lockKey)
        }

        val wait = distributedLock.waitTimeMs
        val lease = distributedLock.leaseTimeMs

        var acquired = false
        try {
            // 3) 락 획득
            acquired = if (lease <= 0L) {
                // watchdog 사용: tryLock(wait, unit) 오버로드가 없음 → tryLock(wait, lease=-1, unit)
                lock.tryLock(wait, -1, TimeUnit.MILLISECONDS)
            } else {
                lock.tryLock(wait, lease, TimeUnit.MILLISECONDS)
            }

            if (!acquired) {
                throw LockAcquisitionException("분산 락 획득 실패: key=$lockKey, wait=${wait}ms")
            }

            // 4) 실제 로직 실행
            return pjp.proceed()
        } catch (e: LockAcquisitionException) {
            throw e
        } catch (e: Throwable) {
            throw e
        } finally {
            // 5) 락 해제 (내가 가진 락만 안전하게)
            if (acquired && lock.isHeldByCurrentThread) {
                try {
                    lock.unlock()
                } catch (ignored: IllegalMonitorStateException) {

                }
            }
        }
    }
}