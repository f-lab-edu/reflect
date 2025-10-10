package kr.co.archan.reflect.unit.global.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith
import kr.co.archan.reflect.global.properties.CryptoProperties
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

class CryptoTest : BehaviorSpec({
    
    val testPepperKey = "test-pepper-key"
    val cryptoProperties = CryptoProperties(
        hashKey = "test-secret-key",
        pepperKey = testPepperKey
    )
    val passwordEncoder = Argon2PasswordEncoder(16, 32, 1, 64 * 1024, 3)
    val crypto = Crypto(cryptoProperties, passwordEncoder)

    context("sha256WithNoSalt - 기본 문자열 해시 생성") {
        Given("기본 문자열이 주어지고") {
            val input = "hello world"

            When("해시를 생성하면") {
                val result = crypto.sha256WithNoSalt(input)

                Then("Base64 URL 인코딩된 43자 해시가 생성된다") {
                    result shouldNotBe null
                    result.isEmpty() shouldBe false
                    result.length shouldBe 43
                    result.shouldNotContain("=")
                    result shouldMatch Regex("^[A-Za-z0-9_-]+$")
                }
            }
        }
    }

    context("sha256WithNoSalt - 동일한 입력에 대해 동일한 해시 생성") {
        Given("동일한 문자열이 주어지고") {
            val input = "test string"

            When("여러 번 해시를 생성하면") {
                val result1 = crypto.sha256WithNoSalt(input)
                val result2 = crypto.sha256WithNoSalt(input)

                Then("동일한 해시가 생성된다") {
                    result1 shouldBe result2
                }
            }
        }
    }

    context("sha256WithNoSalt - 다른 입력에 대해 다른 해시 생성") {
        Given("다른 문자열이 주어지고") {
            val input1 = "string1"
            val input2 = "string2"

            When("해시를 생성하면") {
                val result1 = crypto.sha256WithNoSalt(input1)
                val result2 = crypto.sha256WithNoSalt(input2)

                Then("다른 해시가 생성된다") {
                    result1 shouldNotBe result2
                }
            }
        }
    }

    context("sha256WithNoSalt - 빈 문자열 해시 생성") {
        Given("빈 문자열이 주어지고") {
            val input = ""

            When("해시를 생성하면") {
                val result = crypto.sha256WithNoSalt(input)

                Then("43자 해시가 생성되고 일관성이 있다") {
                    result shouldNotBe null
                    result.length shouldBe 43
                    val expectedEmpty = crypto.sha256WithNoSalt("")
                    result shouldBe expectedEmpty
                }
            }
        }
    }

    context("sha256WithNoSalt - 긴 문자열 해시 생성") {
        Given("1000자 문자열이 주어지고") {
            val input = "a".repeat(1000)

            When("해시를 생성하면") {
                val result = crypto.sha256WithNoSalt(input)

                Then("입력 길이와 상관없이 항상 43자 해시가 생성된다") {
                    result shouldNotBe null
                    result.length shouldBe 43
                }
            }
        }
    }

    context("sha256WithNoSalt - 특수문자 포함 문자열 해시 생성") {
        Given("특수문자가 포함된 문자열이 주어지고") {
            val input = "!@#$%^&*()_+-=[]{}|;:'\",.<>?/~`"

            When("해시를 생성하면") {
                val result = crypto.sha256WithNoSalt(input)

                Then("Base64 URL 인코딩된 43자 해시가 생성된다") {
                    result shouldNotBe null
                    result.length shouldBe 43
                    result shouldMatch Regex("^[A-Za-z0-9_-]+$")
                }
            }
        }
    }

    context("sha256WithNoSalt - 유니코드 문자열 해시 생성") {
        Given("유니코드 문자열이 주어지고") {
            val input = "안녕하세요 🌟 こんにちは"

            When("해시를 생성하면") {
                val result = crypto.sha256WithNoSalt(input)

                Then("Base64 URL 인코딩된 43자 해시가 생성된다") {
                    result shouldNotBe null
                    result.length shouldBe 43
                    result shouldMatch Regex("^[A-Za-z0-9_-]+$")
                }
            }
        }
    }

    context("sha256WithNoSalt - 대소문자 구분 검증") {
        Given("대소문자가 다른 문자열이 주어지고") {
            val input1 = "Test"
            val input2 = "test"

            When("해시를 생성하면") {
                val result1 = crypto.sha256WithNoSalt(input1)
                val result2 = crypto.sha256WithNoSalt(input2)

                Then("다른 해시가 생성된다") {
                    result1 shouldNotBe result2
                }
            }
        }
    }

    context("sha256WithNoSalt - 공백 포함 문자열 해시 생성") {
        Given("공백 위치가 다른 문자열들이 주어지고") {
            val input1 = "hello world"
            val input2 = "helloworld"
            val input3 = " hello world "

            When("해시를 생성하면") {
                val result1 = crypto.sha256WithNoSalt(input1)
                val result2 = crypto.sha256WithNoSalt(input2)
                val result3 = crypto.sha256WithNoSalt(input3)

                Then("모두 다른 해시가 생성된다") {
                    result1 shouldNotBe result2
                    result1 shouldNotBe result3
                    result2 shouldNotBe result3
                }
            }
        }
    }

    context("sha256WithNoSalt - Base64 URL 인코딩 형식 검증") {
        Given("여러 문자열이 주어지고") {
            val inputs = listOf("test1", "test2", "test3", "very_long_string_for_testing")

            When("해시를 생성하면") {
                Then("모든 해시가 Base64 URL 형식을 따른다") {
                    inputs.forEach { input ->
                        val result = crypto.sha256WithNoSalt(input)
                        result shouldMatch Regex("^[A-Za-z0-9_-]+$")
                        result.shouldNotContain("+")
                        result.shouldNotContain("/")
                        result.shouldNotContain("=")
                        result.length shouldBe 43
                    }
                }
            }
        }
    }

    context("hashPassword - 비밀번호 해싱이 정상 작동") {
        Given("비밀번호가 주어지고") {
            val password = "myPassword123"

            When("해시를 생성하면") {
                val result = crypto.hashPassword(password)

                Then("Argon2 해시가 생성된다") {
                    result shouldNotBe null
                    result.isEmpty() shouldBe false
                    result.shouldStartWith("\$argon2")
                }
            }
        }
    }

    context("hashPassword - 동일한 비밀번호도 매번 다른 해시 생성 (salt 때문)") {
        Given("동일한 비밀번호가 주어지고") {
            val password = "samePassword"

            When("여러 번 해시를 생성하면") {
                val hash1 = crypto.hashPassword(password)
                val hash2 = crypto.hashPassword(password)

                Then("salt가 매번 달라서 다른 해시가 생성된다") {
                    hash1 shouldNotBe hash2
                }
            }
        }
    }

    context("isPasswordMatches - 올바른 비밀번호는 true 반환") {
        Given("비밀번호와 그 해시가 주어지고") {
            val password = "correctPassword"
            val hashedPassword = crypto.hashPassword(password)

            When("비밀번호를 검증하면") {
                val result = crypto.isPasswordMatches(hashedPassword, password)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isPasswordMatches - 잘못된 비밀번호는 false 반환") {
        Given("비밀번호 해시와 다른 비밀번호가 주어지고") {
            val password = "correctPassword"
            val wrongPassword = "wrongPassword"
            val hashedPassword = crypto.hashPassword(password)

            When("잘못된 비밀번호를 검증하면") {
                val result = crypto.isPasswordMatches(hashedPassword, wrongPassword)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isPasswordMatches - 대소문자 구분 검증") {
        Given("대소문자가 다른 비밀번호가 주어지고") {
            val password = "Password"
            val hashedPassword = crypto.hashPassword(password)

            When("비밀번호를 검증하면") {
                val correctResult = crypto.isPasswordMatches(hashedPassword, "Password")
                val wrongResult = crypto.isPasswordMatches(hashedPassword, "password")

                Then("대소문자가 다르면 false가 반환된다") {
                    correctResult shouldBe true
                    wrongResult shouldBe false
                }
            }
        }
    }

    context("isPasswordMatches - 특수문자 포함 비밀번호 검증") {
        Given("특수문자가 포함된 비밀번호가 주어지고") {
            val password = "P@ssw0rd!#$%^&*()"
            val hashedPassword = crypto.hashPassword(password)

            When("비밀번호를 검증하면") {
                val result = crypto.isPasswordMatches(hashedPassword, password)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isPasswordMatches - 빈 비밀번호 검증") {
        Given("빈 비밀번호가 주어지고") {
            val password = ""
            val hashedPassword = crypto.hashPassword(password)

            When("빈 비밀번호를 검증하면") {
                val result = crypto.isPasswordMatches(hashedPassword, password)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isPasswordMatches - 긴 비밀번호 검증") {
        Given("100자 비밀번호가 주어지고") {
            val password = "a".repeat(100)
            val hashedPassword = crypto.hashPassword(password)

            When("비밀번호를 검증하면") {
                val correctResult = crypto.isPasswordMatches(hashedPassword, password)
                val wrongResult = crypto.isPasswordMatches(hashedPassword, "a".repeat(99))

                Then("올바른 비밀번호는 true, 다른 비밀번호는 false가 반환된다") {
                    correctResult shouldBe true
                    wrongResult shouldBe false
                }
            }
        }
    }

    context("isPasswordMatches - 유니코드 문자 포함 비밀번호 검증") {
        Given("유니코드 문자가 포함된 비밀번호가 주어지고") {
            val password = "비밀번호123!@#"
            val hashedPassword = crypto.hashPassword(password)

            When("비밀번호를 검증하면") {
                val result = crypto.isPasswordMatches(hashedPassword, password)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }
})
