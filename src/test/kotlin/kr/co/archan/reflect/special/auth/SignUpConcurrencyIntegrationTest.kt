package kr.co.archan.reflect.special.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.redis.testcontainers.RedisContainer
import kr.co.archan.reflect.auth.dto.request.SignUpRequest
import kr.co.archan.reflect.member.repository.MemberRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class SignUpConcurrencyIntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val redisContainer: RedisContainer = RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun registerRedisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @AfterEach
    fun tearDown() {
        memberRepository.deleteAll()
    }

    @Test
    @DisplayName("동시성 테스트 - 같은 이메일로 10개의 스레드가 동시에 회원가입 시도 시 1개만 성공")
    fun `동시성 테스트 - 같은 이메일로 여러 스레드가 동시에 회원가입 시도 시 1개만 성공`() {
        // given
        val email = "concurrent@test.com"
        val password = "password1!"
        val name = "동시성테스터"
        val threadCount = 10

        val request = SignUpRequest(email, password, name)
        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)
        val exceptionMessages = mutableListOf<String>()

        // when
        repeat(threadCount) {
            executorService.submit {
                try {
                    latch.countDown()
                    latch.await() // 모든 스레드가 동시에 시작하도록 대기

                    val result = mockMvc.perform(
                        post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    ).andReturn()

                    if (result.response.status == 201) {
                        successCount.incrementAndGet()
                    } else {
                        failCount.incrementAndGet()
                        synchronized(exceptionMessages) {
                            exceptionMessages.add("Status: ${result.response.status}, Body: ${result.response.contentAsString}")
                        }
                    }
                } catch (e: Exception) {
                    failCount.incrementAndGet()
                    synchronized(exceptionMessages) {
                        exceptionMessages.add(e.message ?: "Unknown error")
                    }
                }
            }
        }

        executorService.shutdown()
        while (!executorService.isTerminated) {
            Thread.sleep(100)
        }

        // 분산 락이 제대로 작동한다면, 정확히 1개만 성공해야 함
        assertEquals(1, successCount.get(), "정확히 하나의 회원가입만 성공해야 합니다")
        assertEquals(threadCount - 1, failCount.get(), "나머지 ${threadCount - 1}개의 회원가입은 실패해야 합니다")

        // DB에도 1개만 저장되어 있어야 함
        val savedMembers = memberRepository.findAll().filter { it.email == email }
        assertEquals(1, savedMembers.size, "DB에는 1개의 회원만 저장되어야 합니다")
        assertEquals(email, savedMembers[0].email)
        assertEquals(name, savedMembers[0].name)
    }

    @Test
    @DisplayName("동시성 테스트 - 다른 이메일로 동시 회원가입 시 모두 성공")
    fun `동시성 테스트 - 다른 이메일로 동시 회원가입 시 모두 성공`() {
        // given
        val threadCount = 5
        val password = "password1!"
        val name = "테스터"

        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        // when
        repeat(threadCount) { index ->
            executorService.submit {
                try {
                    val email = "user$index@test.com"
                    val request = SignUpRequest(email, password, name)

                    latch.countDown()
                    latch.await() // 모든 스레드가 동시에 시작하도록 대기

                    val result = mockMvc.perform(
                        post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    ).andReturn()

                    if (result.response.status == 201) {
                        successCount.incrementAndGet()
                    } else {
                        failCount.incrementAndGet()
                    }
                } catch (e: Exception) {
                    failCount.incrementAndGet()
                }
            }
        }

        executorService.shutdown()
        while (!executorService.isTerminated) {
            Thread.sleep(100)
        }

        // then
        println("Different email - Success count: ${successCount.get()}")
        println("Different email - Fail count: ${failCount.get()}")

        // 다른 이메일이므로 모두 성공해야 함
        assertEquals(threadCount, successCount.get(), "모든 회원가입이 성공해야 합니다")
        assertEquals(0, failCount.get(), "실패한 회원가입이 없어야 합니다")

        // DB에 모든 회원이 저장되어 있어야 함
        val savedMembers = memberRepository.findAll()
        assertTrue(savedMembers.size >= threadCount, "DB에 $threadCount 명 이상의 회원이 저장되어야 합니다")
    }
}
