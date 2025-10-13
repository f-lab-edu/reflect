package kr.co.archan.reflect.unit.auth.dto.response

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import kr.co.archan.reflect.auth.dto.response.SignUpResponse

class SignUpResponseTest : BehaviorSpec({

    context("SignUpResponse 생성 및 프로퍼티 접근") {
        Given("유효한 accessToken과 refreshToken이 주어지고") {
            val accessToken = "sample-access-token"
            val refreshToken = "sample-refresh-token"

            When("SignUpResponse를 생성하면") {
                val response = SignUpResponse(accessToken, refreshToken)

                Then("토큰이 올바르게 설정된다") {
                    response.accessToken shouldBe accessToken
                    response.refreshToken shouldBe refreshToken
                }
            }
        }
    }

    context("equals - 같은 값을 가진 SignUpResponse는 동등함") {
        Given("같은 값을 가진 두 개의 SignUpResponse가 있고") {
            val response1 = SignUpResponse("access-token", "refresh-token")
            val response2 = SignUpResponse("access-token", "refresh-token")

            When("두 객체를 비교하면") {
                val result = response1 == response2

                Then("동등하다") {
                    result shouldBe true
                    response1 shouldBe response2
                }
            }
        }
    }

    context("equals - 다른 accessToken을 가진 SignUpResponse는 동등하지 않음") {
        Given("다른 accessToken을 가진 두 개의 SignUpResponse가 있고") {
            val response1 = SignUpResponse("access-token-1", "refresh-token")
            val response2 = SignUpResponse("access-token-2", "refresh-token")

            When("두 객체를 비교하면") {
                val result = response1 == response2

                Then("동등하지 않다") {
                    result shouldBe false
                    response1 shouldNotBe response2
                }
            }
        }
    }

    context("equals - 다른 refreshToken을 가진 SignUpResponse는 동등하지 않음") {
        Given("다른 refreshToken을 가진 두 개의 SignUpResponse가 있고") {
            val response1 = SignUpResponse("access-token", "refresh-token-1")
            val response2 = SignUpResponse("access-token", "refresh-token-2")

            When("두 객체를 비교하면") {
                val result = response1 == response2

                Then("동등하지 않다") {
                    result shouldBe false
                    response1 shouldNotBe response2
                }
            }
        }
    }

    context("hashCode - 같은 값을 가진 SignUpResponse는 같은 hashCode") {
        Given("같은 값을 가진 두 개의 SignUpResponse가 있고") {
            val response1 = SignUpResponse("access-token", "refresh-token")
            val response2 = SignUpResponse("access-token", "refresh-token")

            When("hashCode를 비교하면") {
                val hashCode1 = response1.hashCode()
                val hashCode2 = response2.hashCode()

                Then("같은 hashCode를 가진다") {
                    hashCode1 shouldBe hashCode2
                }
            }
        }
    }

    context("hashCode - 다른 값을 가진 SignUpResponse는 다른 hashCode") {
        Given("다른 값을 가진 두 개의 SignUpResponse가 있고") {
            val response1 = SignUpResponse("access-token-1", "refresh-token-1")
            val response2 = SignUpResponse("access-token-2", "refresh-token-2")

            When("hashCode를 비교하면") {
                val hashCode1 = response1.hashCode()
                val hashCode2 = response2.hashCode()

                Then("다른 hashCode를 가진다") {
                    hashCode1 shouldNotBe hashCode2
                }
            }
        }
    }

    context("copy - accessToken 변경") {
        Given("원본 SignUpResponse가 있고") {
            val original = SignUpResponse("original-access", "original-refresh")
            val newAccessToken = "new-access"

            When("accessToken만 변경하여 복사하면") {
                val copied = original.copy(accessToken = newAccessToken)

                Then("accessToken만 변경되고 refreshToken은 유지된다") {
                    copied.accessToken shouldBe newAccessToken
                    copied.refreshToken shouldBe original.refreshToken
                }
            }
        }
    }

    context("copy - refreshToken 변경") {
        Given("원본 SignUpResponse가 있고") {
            val original = SignUpResponse("original-access", "original-refresh")
            val newRefreshToken = "new-refresh"

            When("refreshToken만 변경하여 복사하면") {
                val copied = original.copy(refreshToken = newRefreshToken)

                Then("refreshToken만 변경되고 accessToken은 유지된다") {
                    copied.accessToken shouldBe original.accessToken
                    copied.refreshToken shouldBe newRefreshToken
                }
            }
        }
    }

    context("copy - 모든 프로퍼티 변경") {
        Given("원본 SignUpResponse가 있고") {
            val original = SignUpResponse("original-access", "original-refresh")
            val newAccessToken = "new-access"
            val newRefreshToken = "new-refresh"

            When("모든 프로퍼티를 변경하여 복사하면") {
                val copied = original.copy(
                    accessToken = newAccessToken,
                    refreshToken = newRefreshToken
                )

                Then("모든 프로퍼티가 변경된다") {
                    copied.accessToken shouldBe newAccessToken
                    copied.refreshToken shouldBe newRefreshToken
                    copied shouldNotBe original
                }
            }
        }
    }

    context("copy - 모든 프로퍼티 복사") {
        Given("원본 SignUpResponse가 있고") {
            val original = SignUpResponse("access-token", "refresh-token")

            When("아무 것도 변경하지 않고 복사하면") {
                val copied = original.copy()

                Then("동등한 새로운 객체가 생성된다") {
                    copied shouldBe original
                    (copied !== original) shouldBe true
                }
            }
        }
    }

    context("toString - 문자열 표현 포함") {
        Given("SignUpResponse가 있고") {
            val accessToken = "test-access-token"
            val refreshToken = "test-refresh-token"
            val response = SignUpResponse(accessToken, refreshToken)

            When("toString을 호출하면") {
                val result = response.toString()

                Then("문자열 표현이 포함된다") {
                    result shouldNotBe null
                    result.shouldContain("SignUpResponse")
                    result.shouldContain(accessToken)
                    result.shouldContain(refreshToken)
                }
            }
        }
    }

    context("Set에서 중복 제거 - equals와 hashCode 활용") {
        Given("같은 값과 다른 값을 가진 SignUpResponse들이 있고") {
            val response1 = SignUpResponse("access", "refresh")
            val response2 = SignUpResponse("access", "refresh")
            val response3 = SignUpResponse("different-access", "different-refresh")

            When("Set으로 변환하면") {
                val responseSet = setOf(response1, response2, response3)

                Then("중복이 제거된다") {
                    responseSet.size shouldBe 2
                    responseSet shouldContain response1
                    responseSet shouldContain response3
                }
            }
        }
    }

    context("빈 문자열 토큰으로 생성 가능") {
        Given("빈 문자열이 주어지고") {
            val emptyString = ""

            When("빈 토큰으로 SignUpResponse를 생성하면") {
                val response = SignUpResponse(emptyString, emptyString)

                Then("빈 값이 설정된다") {
                    response.accessToken shouldBe ""
                    response.refreshToken shouldBe ""
                }
            }
        }
    }

    context("매우 긴 토큰 값 처리") {
        Given("매우 긴 토큰 값이 주어지고") {
            val longAccessToken = "a".repeat(1000)
            val longRefreshToken = "b".repeat(1000)

            When("긴 토큰으로 SignUpResponse를 생성하면") {
                val response = SignUpResponse(longAccessToken, longRefreshToken)

                Then("긴 값이 올바르게 저장된다") {
                    response.accessToken.length shouldBe 1000
                    response.refreshToken.length shouldBe 1000
                }
            }
        }
    }

})