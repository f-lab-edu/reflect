package kr.co.archan.reflect.auth.dto.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.co.archan.reflect.auth.domain.AccessToken
import kr.co.archan.reflect.auth.domain.RefreshToken
import java.time.Instant

class AuthTokenTest : BehaviorSpec({

    context("AuthToken 생성 및 프로퍼티 접근") {
        Given("AccessToken과 RefreshToken이 주어지고") {
            val accessToken = AccessToken("access-token-value", Instant.now().plusSeconds(3600))
            val refreshToken = RefreshToken("refresh-token-value", "12345", Instant.now().plusSeconds(1209600))

            When("AuthToken을 생성하면") {
                val authToken = AuthToken(accessToken, refreshToken)

                Then("프로퍼티에 올바르게 접근할 수 있다") {
                    authToken.accessToken shouldBe accessToken
                    authToken.refreshToken shouldBe refreshToken
                }
            }
        }
    }

    context("equals - 같은 값을 가진 AuthToken은 동등함") {
        Given("같은 값을 가진 두 개의 AuthToken이 있고") {
            val accessToken = AccessToken("access", Instant.parse("2025-10-06T12:00:00Z"))
            val refreshToken = RefreshToken("refresh", "100", Instant.parse("2025-10-20T12:00:00Z"))
            val authToken1 = AuthToken(accessToken, refreshToken)
            val authToken2 = AuthToken(accessToken, refreshToken)

            When("두 객체를 비교하면") {
                val result = authToken1 == authToken2

                Then("동등하다") {
                    result shouldBe true
                    authToken1 shouldBe authToken2
                }
            }
        }
    }

    context("equals - 다른 accessToken을 가진 AuthToken은 동등하지 않음") {
        Given("다른 accessToken을 가진 두 개의 AuthToken이 있고") {
            val accessToken1 = AccessToken("access1", Instant.now())
            val accessToken2 = AccessToken("access2", Instant.now())
            val refreshToken = RefreshToken("refresh", "100", Instant.now())
            val authToken1 = AuthToken(accessToken1, refreshToken)
            val authToken2 = AuthToken(accessToken2, refreshToken)

            When("두 객체를 비교하면") {
                val result = authToken1 == authToken2

                Then("동등하지 않다") {
                    result shouldBe false
                    authToken1 shouldNotBe authToken2
                }
            }
        }
    }

    context("equals - 다른 refreshToken을 가진 AuthToken은 동등하지 않음") {
        Given("다른 refreshToken을 가진 두 개의 AuthToken이 있고") {
            val accessToken = AccessToken("access", Instant.now())
            val refreshToken1 = RefreshToken("refresh1", "100", Instant.now())
            val refreshToken2 = RefreshToken("refresh2", "100", Instant.now())
            val authToken1 = AuthToken(accessToken, refreshToken1)
            val authToken2 = AuthToken(accessToken, refreshToken2)

            When("두 객체를 비교하면") {
                val result = authToken1 == authToken2

                Then("동등하지 않다") {
                    result shouldBe false
                    authToken1 shouldNotBe authToken2
                }
            }
        }
    }

    context("hashCode - 같은 값을 가진 AuthToken은 같은 hashCode") {
        Given("같은 값을 가진 두 개의 AuthToken이 있고") {
            val accessToken = AccessToken("access", Instant.parse("2025-10-06T12:00:00Z"))
            val refreshToken = RefreshToken("refresh", "100", Instant.parse("2025-10-20T12:00:00Z"))
            val authToken1 = AuthToken(accessToken, refreshToken)
            val authToken2 = AuthToken(accessToken, refreshToken)

            When("hashCode를 비교하면") {
                val hashCode1 = authToken1.hashCode()
                val hashCode2 = authToken2.hashCode()

                Then("같은 hashCode를 반환한다") {
                    hashCode1 shouldBe hashCode2
                }
            }
        }
    }

    context("hashCode - 다른 값을 가진 AuthToken은 다른 hashCode") {
        Given("다른 값을 가진 두 개의 AuthToken이 있고") {
            val accessToken1 = AccessToken("access1", Instant.now())
            val accessToken2 = AccessToken("access2", Instant.now())
            val refreshToken1 = RefreshToken("refresh1", "100", Instant.now())
            val refreshToken2 = RefreshToken("refresh2", "100", Instant.now())
            val authToken1 = AuthToken(accessToken1, refreshToken1)
            val authToken2 = AuthToken(accessToken2, refreshToken2)

            When("hashCode를 비교하면") {
                val hashCode1 = authToken1.hashCode()
                val hashCode2 = authToken2.hashCode()

                Then("다른 hashCode를 반환한다") {
                    hashCode1 shouldNotBe hashCode2
                }
            }
        }
    }

    context("copy - accessToken 변경") {
        Given("AuthToken이 있고") {
            val originalAccessToken = AccessToken("original-access", Instant.now())
            val newAccessToken = AccessToken("new-access", Instant.now().plusSeconds(7200))
            val refreshToken = RefreshToken("refresh", "100", Instant.now())
            val original = AuthToken(originalAccessToken, refreshToken)

            When("accessToken을 변경하여 복사하면") {
                val copied = original.copy(accessToken = newAccessToken)

                Then("accessToken만 변경된다") {
                    copied.accessToken shouldBe newAccessToken
                    copied.refreshToken shouldBe original.refreshToken
                    copied.accessToken shouldNotBe original.accessToken
                }
            }
        }
    }

    context("copy - refreshToken 변경") {
        Given("AuthToken이 있고") {
            val accessToken = AccessToken("access", Instant.now())
            val originalRefreshToken = RefreshToken("original-refresh", "100", Instant.now())
            val newRefreshToken = RefreshToken("new-refresh", "200", Instant.now().plusSeconds(14400))
            val original = AuthToken(accessToken, originalRefreshToken)

            When("refreshToken을 변경하여 복사하면") {
                val copied = original.copy(refreshToken = newRefreshToken)

                Then("refreshToken만 변경된다") {
                    copied.accessToken shouldBe original.accessToken
                    copied.refreshToken shouldBe newRefreshToken
                    copied.refreshToken shouldNotBe original.refreshToken
                }
            }
        }
    }

    context("copy - 모든 프로퍼티 변경") {
        Given("AuthToken이 있고") {
            val originalAccessToken = AccessToken("original-access", Instant.now())
            val originalRefreshToken = RefreshToken("original-refresh", "100", Instant.now())
            val newAccessToken = AccessToken("new-access", Instant.now().plusSeconds(3600))
            val newRefreshToken = RefreshToken("new-refresh", "200", Instant.now().plusSeconds(7200))
            val original = AuthToken(originalAccessToken, originalRefreshToken)

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
        Given("AuthToken이 있고") {
            val accessToken = AccessToken("access", Instant.now())
            val refreshToken = RefreshToken("refresh", "100", Instant.now())
            val original = AuthToken(accessToken, refreshToken)

            When("아무것도 변경하지 않고 복사하면") {
                val copied = original.copy()

                Then("동일한 값을 가진 새 인스턴스가 생성된다") {
                    copied shouldBe original
                }
            }
        }
    }

    context("toString - 문자열 표현 포함") {
        Given("AuthToken이 있고") {
            val accessToken = AccessToken("test-access", Instant.parse("2025-10-06T12:00:00Z"))
            val refreshToken = RefreshToken("test-refresh", "999", Instant.parse("2025-10-20T12:00:00Z"))
            val authToken = AuthToken(accessToken, refreshToken)

            When("toString을 호출하면") {
                val result = authToken.toString()

                Then("올바른 문자열 표현이 반환된다") {
                    result shouldNotBe null
                    result.contains("AuthToken") shouldBe true
                    result.contains("AccessToken") shouldBe true
                    result.contains("RefreshToken") shouldBe true
                }
            }
        }
    }

    context("Set에서 중복 제거 - equals와 hashCode 활용") {
        Given("같은 값의 AuthToken과 다른 값의 AuthToken이 있고") {
            val accessToken = AccessToken("access", Instant.parse("2025-10-06T12:00:00Z"))
            val refreshToken = RefreshToken("refresh", "100", Instant.parse("2025-10-20T12:00:00Z"))
            val authToken1 = AuthToken(accessToken, refreshToken)
            val authToken2 = AuthToken(accessToken, refreshToken)
            val authToken3 = AuthToken(
                AccessToken("different-access", Instant.now()),
                RefreshToken("different-refresh", "200", Instant.now())
            )

            When("Set에 추가하면") {
                val tokenSet = setOf(authToken1, authToken2, authToken3)

                Then("중복이 제거된다") {
                    tokenSet.size shouldBe 2
                    tokenSet.contains(authToken1) shouldBe true
                    tokenSet.contains(authToken3) shouldBe true
                }
            }
        }
    }

    context("Map의 키로 사용 - equals와 hashCode 활용") {
        Given("같은 값을 가진 두 개의 AuthToken이 있고") {
            val accessToken = AccessToken("access", Instant.parse("2025-10-06T12:00:00Z"))
            val refreshToken = RefreshToken("refresh", "100", Instant.parse("2025-10-20T12:00:00Z"))
            val authToken1 = AuthToken(accessToken, refreshToken)
            val authToken2 = AuthToken(accessToken, refreshToken)
            val map = mutableMapOf<AuthToken, String>()

            When("Map의 키로 사용하면") {
                map[authToken1] = "first"
                map[authToken2] = "second"

                Then("같은 키로 인식된다") {
                    map.size shouldBe 1
                    map[authToken1] shouldBe "second"
                    map[authToken2] shouldBe "second"
                }
            }
        }
    }

    context("AccessToken과 RefreshToken의 만료 시간이 독립적으로 관리됨") {
        Given("AccessToken과 RefreshToken이 다른 만료 시간을 가지고") {
            val now = Instant.now()
            val accessExpiry = now.plusSeconds(3600)
            val refreshExpiry = now.plusSeconds(1209600)
            val accessToken = AccessToken("access", accessExpiry)
            val refreshToken = RefreshToken("refresh", "100", refreshExpiry)

            When("AuthToken을 생성하면") {
                val authToken = AuthToken(accessToken, refreshToken)

                Then("각 토큰의 만료 시간이 독립적으로 관리된다") {
                    authToken.refreshToken.expiresAt.isAfter(authToken.accessToken.expiresAt) shouldBe true
                    authToken.accessToken.expiresAt shouldBe accessExpiry
                    authToken.refreshToken.expiresAt shouldBe refreshExpiry
                }
            }
        }
    }

    context("서로 다른 memberId를 가진 AuthToken은 구분됨") {
        Given("서로 다른 memberId를 가진 두 개의 AuthToken이 있고") {
            val accessToken = AccessToken("access", Instant.now())
            val refreshToken1 = RefreshToken("refresh", "100", Instant.now())
            val refreshToken2 = RefreshToken("refresh", "200", Instant.now())
            val authToken1 = AuthToken(accessToken, refreshToken1)
            val authToken2 = AuthToken(accessToken, refreshToken2)

            When("두 AuthToken을 비교하면") {
                val result = authToken1 == authToken2

                Then("서로 다른 토큰으로 구분된다") {
                    result shouldBe false
                    authToken1 shouldNotBe authToken2
                    authToken1.refreshToken.memberId shouldBe "100"
                    authToken2.refreshToken.memberId shouldBe "200"
                }
            }
        }
    }
})
