package kr.co.archan.reflect.unit.global.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ValidatorTest : BehaviorSpec({

    context("isEmailValid - 일반적인 이메일") {
        Given("일반적인 이메일이 주어지고") {
            val email = "test@example.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isEmailValid - 숫자와 특수문자 포함") {
        Given("숫자와 특수문자가 포함된 이메일이 주어지고") {
            val email = "user.name+tag123@example.co.kr"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isEmailValid - 이메일 공백") {
        Given("빈 이메일이 주어지고") {
            val email = ""

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - @ 없음") {
        Given("@ 기호가 없는 이메일이 주어지고") {
            val email = "testexample.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 255자 이상") {
        Given("255자 이상의 이메일이 주어지고") {
            val email =
                "testasdfsdfsasdfdasasdfddasfdddudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadfasdfdd@test.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 공백 포함") {
        Given("공백이 포함된 이메일이 주어지고") {
            val email = "test @example.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 로컬파트 없음") {
        Given("로컬파트가 없는 이메일이 주어지고") {
            val email = "@example.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 도메인 없음") {
        Given("도메인이 없는 이메일이 주어지고") {
            val email = "test@"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 도메인에 점으로 시작") {
        Given("도메인이 점으로 시작하는 이메일이 주어지고") {
            val email = "test@.example.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 도메인에 점으로 끝남") {
        Given("도메인이 점으로 끝나는 이메일이 주어지고") {
            val email = "test@example.com."

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 연속된 점") {
        Given("연속된 점이 있는 이메일이 주어지고") {
            val email = "test@example..com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - TLD 1글자") {
        Given("TLD가 1글자인 이메일이 주어지고") {
            val email = "test@example.c"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 로컬파트 65자 이상") {
        Given("로컬파트가 65자 이상인 이메일이 주어지고") {
            val email = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklm@example.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 허용되지 않는 문자") {
        Given("허용되지 않는 문자가 포함된 이메일이 주어지고") {
            val email = "test@#example.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 도메인 레이블 수 부족") {
        Given("도메인 레이블 수가 부족한 이메일이 주어지고") {
            val email = "test@example"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - TLD에 숫자 포함") {
        Given("TLD에 숫자가 포함된 이메일이 주어지고") {
            val email = "test@example.co2"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 로컬파트에 허용되지 않는 문자") {
        Given("로컬파트에 허용되지 않는 문자가 있는 이메일이 주어지고") {
            val email = "test@domain@example.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 도메인에 허용되지 않는 문자") {
        Given("도메인에 허용되지 않는 문자가 있는 이메일이 주어지고") {
            val email = "test@exam_ple.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 여러 개의 @ 기호") {
        Given("여러 개의 @ 기호가 있는 이메일이 주어지고") {
            val email = "test@@example.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - @ 기호만") {
        Given("@ 기호만 있는 이메일이 주어지고") {
            val email = "@"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isEmailValid - 경계값 254자") {
        Given("254자 이메일이 주어지고") {
            val localPart = "a".repeat(64)
            val domainPart = "b".repeat(63) + "." + "c".repeat(63) + "." + "d".repeat(57) + ".com"
            val email = "$localPart@$domainPart"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isEmailValid - 로컬파트 경계값 64자") {
        Given("로컬파트가 64자인 이메일이 주어지고") {
            val email = "a".repeat(64) + "@example.com"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isEmailValid - TLD 경계값 2자") {
        Given("TLD가 2자인 이메일이 주어지고") {
            val email = "test@example.co"

            When("검증하면") {
                val result = Validator.isEmailValid(email)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isNameValid - 일반적인 이름") {
        Given("일반적인 이름이 주어지고") {
            val name = "홍길동"

            When("검증하면") {
                val result = Validator.isNameValid(name)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isNameValid - 영문 이름") {
        Given("영문 이름이 주어지고") {
            val name = "John Doe"

            When("검증하면") {
                val result = Validator.isNameValid(name)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isNameValid - 20자 이름") {
        Given("20자 이름이 주어지고") {
            val name = "가나다라마바사아자차카타파하가나다라마바"

            When("검증하면") {
                val result = Validator.isNameValid(name)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isNameValid - 이름 공백") {
        Given("빈 이름이 주어지고") {
            val name = ""

            When("검증하면") {
                val result = Validator.isNameValid(name)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isNameValid - 이름 공백만") {
        Given("공백만 있는 이름이 주어지고") {
            val name = "   "

            When("검증하면") {
                val result = Validator.isNameValid(name)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isNameValid - 21자 이상") {
        Given("21자 이상의 이름이 주어지고") {
            val name = "가나다라마바사아자차카타파하가나다라마바사"

            When("검증하면") {
                val result = Validator.isNameValid(name)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isPasswordValid - 일반적인 패스워드") {
        Given("일반적인 패스워드가 주어지고") {
            val password = "password123!"

            When("검증하면") {
                val result = Validator.isPasswordValid(password)

                Then("true가 반환된다") {
                    result shouldBe true
                }
            }
        }
    }

    context("isPasswordValid - 7자 이하") {
        Given("7자 이하의 패스워드가 주어지고") {
            val password = "pass1!"

            When("검증하면") {
                val result = Validator.isPasswordValid(password)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isPasswordValid - 65자 이상") {
        Given("65자 이상의 패스워드가 주어지고") {
            val password = "a".repeat(63) + "1!"

            When("검증하면") {
                val result = Validator.isPasswordValid(password)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isPasswordValid - 숫자 없음") {
        Given("숫자가 없는 패스워드가 주어지고") {
            val password = "passwordwithoutdigit!"

            When("검증하면") {
                val result = Validator.isPasswordValid(password)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isPasswordValid - 특수문자 없음") {
        Given("특수문자가 없는 패스워드가 주어지고") {
            val password = "passwordwithoutspecial123"

            When("검증하면") {
                val result = Validator.isPasswordValid(password)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isPasswordValid - 숫자와 특수문자 모두 없음") {
        Given("숫자와 특수문자가 모두 없는 패스워드가 주어지고") {
            val password = "passwordonly"

            When("검증하면") {
                val result = Validator.isPasswordValid(password)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isPasswordValid - 빈 문자열") {
        Given("빈 패스워드가 주어지고") {
            val password = ""

            When("검증하면") {
                val result = Validator.isPasswordValid(password)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }

    context("isPasswordValid - 공백 포함") {
        Given("공백이 포함된 패스워드가 주어지고") {
            val password = "password 123"

            When("검증하면") {
                val result = Validator.isPasswordValid(password)

                Then("false가 반환된다") {
                    result shouldBe false
                }
            }
        }
    }
})
