package kr.co.archan.reflect.unit.auth.dto.request

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldContain
import jakarta.validation.Validation
import jakarta.validation.Validator
import kr.co.archan.reflect.auth.dto.request.SignUpRequest

class SignUpRequestTest : BehaviorSpec({

    val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    context("SignUpRequest 생성 성공") {
        Given("유효한 이메일, 패스워드, 이름이 주어지고") {
            val email = "test@example.com"
            val password = "securePassword123!"
            val name = "홍길동"

            When("SignUpRequest를 생성하면") {
                val request = SignUpRequest(email, password, name)

                Then("모든 값이 올바르게 설정된다") {
                    request.email shouldBe email
                    request.password shouldBe password
                    request.name shouldBe name
                }
            }
        }
    }

    context("검증 성공 - 유효한 이메일, 패스워드, 이름") {
        Given("유효한 SignUpRequest가 있고") {
            val request = SignUpRequest("valid@example.com", "validPass123!", "홍길동")

            When("검증하면") {
                val violations = validator.validate(request)

                Then("검증 에러가 없다") {
                    violations.shouldBeEmpty()
                }
            }
        }
    }

    context("이메일 검증 실패 - 이메일 공백") {
        Given("빈 이메일이 주어지고") {
            val email = ""
            val password = "securePassword123!"
            val name = "홍길동"

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("이메일 검증 에러가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "email" } shouldBe true
                }
            }
        }
    }

    context("이메일 검증 실패 - @ 없음") {
        Given("@가 없는 이메일이 주어지고") {
            val email = "invalid-email"
            val password = "securePassword123!"
            val name = "홍길동"

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("이메일 검증 에러가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "email" } shouldBe true
                }
            }
        }
    }

    context("이메일 검증 실패 - 255자 이상") {
        Given("255자 이상의 이메일이 주어지고") {
            val email = "testasdfsdfsasdfdasasdfddasfdddudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadfasdfdd@test.com"
            val password = "securePassword123!"
            val name = "홍길동"

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("이메일 검증 에러가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "email" } shouldBe true
                }
            }
        }
    }

    context("패스워드 검증 실패 - 패스워드 7자 이하") {
        Given("7자 이하의 패스워드가 주어지고") {
            val email = "test@example.com"
            val password = "pass1!"
            val name = "홍길동"

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("패스워드 검증 에러가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "password" } shouldBe true
                }
            }
        }
    }

    context("패스워드 검증 실패 - 패스워드 65자 이상") {
        Given("65자 이상의 패스워드가 주어지고") {
            val email = "test@example.com"
            val password = "verylongpasswordverylongpasswordverylongpasswordverylongpaslong1!"
            val name = "홍길동"

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("패스워드 검증 에러가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "password" } shouldBe true
                }
            }
        }
    }

    context("패스워드 검증 실패 - 숫자 없음") {
        Given("숫자가 없는 패스워드가 주어지고") {
            val email = "test@example.com"
            val password = "passwordwithoutdigit!"
            val name = "홍길동"

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("패스워드 검증 에러가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "password" } shouldBe true
                }
            }
        }
    }

    context("패스워드 검증 실패 - 특수문자 없음") {
        Given("특수문자가 없는 패스워드가 주어지고") {
            val email = "test@example.com"
            val password = "passwordwithoutspecial123"
            val name = "홍길동"

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("패스워드 검증 에러가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "password" } shouldBe true
                }
            }
        }
    }

    context("이름 검증 실패 - 이름 공백") {
        Given("빈 이름이 주어지고") {
            val email = "test@example.com"
            val password = "securePassword123!"
            val name = ""

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("이름 검증 에러가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "name" } shouldBe true
                }
            }
        }
    }

    context("이름 검증 실패 - 이름 21자 이상") {
        Given("21자 이상의 이름이 주어지고") {
            val email = "test@example.com"
            val password = "securePassword123!"
            val name = "가".repeat(21)

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("이름 검증 에러가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "name" } shouldBe true
                }
            }
        }
    }

    context("이름 검증 성공 - 이름 1자") {
        Given("1자 이름이 주어지고") {
            val email = "test@example.com"
            val password = "securePassword123!"
            val name = "홍"

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("검증 에러가 없다") {
                    violations.shouldBeEmpty()
                }
            }
        }
    }

    context("이름 검증 성공 - 이름 20자") {
        Given("20자 이름이 주어지고") {
            val email = "test@example.com"
            val password = "securePassword123!"
            val name = "가".repeat(20)

            When("SignUpRequest를 검증하면") {
                val violations = validator.validate(SignUpRequest(email, password, name))

                Then("검증 에러가 없다") {
                    violations.shouldBeEmpty()
                }
            }
        }
    }

    context("equals - 같은 값을 가진 SignUpRequest는 동등함") {
        Given("같은 값을 가진 두 개의 SignUpRequest가 있고") {
            val request1 = SignUpRequest("test@example.com", "password123!", "홍길동")
            val request2 = SignUpRequest("test@example.com", "password123!", "홍길동")

            When("두 객체를 비교하면") {
                val result = request1 == request2

                Then("동등하다") {
                    result shouldBe true
                    request1 shouldBe request2
                }
            }
        }
    }

    context("equals - 다른 값을 가진 SignUpRequest는 동등하지 않음") {
        Given("다른 값을 가진 두 개의 SignUpRequest가 있고") {
            val request1 = SignUpRequest("test1@example.com", "password123!", "홍길동")
            val request2 = SignUpRequest("test2@example.com", "password123!", "김철수")

            When("두 객체를 비교하면") {
                val result = request1 == request2

                Then("동등하지 않다") {
                    result shouldBe false
                    request1 shouldNotBe request2
                }
            }
        }
    }

    context("hashCode - 같은 값을 가진 SignUpRequest는 같은 hashCode") {
        Given("같은 값을 가진 두 개의 SignUpRequest가 있고") {
            val request1 = SignUpRequest("test@example.com", "password123!", "홍길동")
            val request2 = SignUpRequest("test@example.com", "password123!", "홍길동")

            When("hashCode를 비교하면") {
                val hashCode1 = request1.hashCode()
                val hashCode2 = request2.hashCode()

                Then("같은 hashCode를 가진다") {
                    hashCode1 shouldBe hashCode2
                }
            }
        }
    }

    context("copy - 일부 프로퍼티 변경") {
        Given("원본 SignUpRequest가 있고") {
            val original = SignUpRequest("original@example.com", "password123!", "홍길동")
            val newEmail = "new@example.com"

            When("이메일만 변경하여 복사하면") {
                val copied = original.copy(email = newEmail)

                Then("이메일만 변경되고 나머지는 유지된다") {
                    copied.email shouldBe newEmail
                    copied.password shouldBe original.password
                    copied.name shouldBe original.name
                }
            }
        }
    }

    context("toString - 문자열 표현 포함") {
        Given("SignUpRequest가 있고") {
            val request = SignUpRequest("test@example.com", "password123!", "홍길동")

            When("toString을 호출하면") {
                val result = request.toString()

                Then("문자열 표현이 포함된다") {
                    result shouldNotBe null
                    result.shouldContain("SignUpRequest")
                    result.shouldContain("test@example.com")
                    result.shouldContain("홍길동")
                }
            }
        }
    }

})