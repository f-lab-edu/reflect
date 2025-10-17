package kr.co.archan.reflect.unit.member.dto.vo

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.string.shouldContain
import kr.co.archan.reflect.member.dto.vo.HashedPassword

class HashedPasswordTest : BehaviorSpec({

    context("HashedPassword 생성 및 프로퍼티 접근") {
        Given("패스워드 값이 주어지고") {
            val passwordValue = "hashedPassword123"

            When("HashedPassword를 생성하면") {
                val hashedPassword = HashedPassword(passwordValue)

                Then("값이 올바르게 설정된다") {
                    hashedPassword.value shouldBe passwordValue
                }
            }
        }
    }

    context("equals - 같은 값을 가진 HashedPassword는 동등함") {
        Given("같은 값을 가진 두 개의 HashedPassword가 있고") {
            val password1 = HashedPassword("same-hashed-password")
            val password2 = HashedPassword("same-hashed-password")

            When("두 객체를 비교하면") {
                val result = password1 == password2

                Then("동등하다") {
                    result shouldBe true
                    password1 shouldBe password2
                }
            }
        }
    }

    context("equals - 다른 값을 가진 HashedPassword는 동등하지 않음") {
        Given("다른 값을 가진 두 개의 HashedPassword가 있고") {
            val password1 = HashedPassword("hashed-password-1")
            val password2 = HashedPassword("hashed-password-2")

            When("두 객체를 비교하면") {
                val result = password1 == password2

                Then("동등하지 않다") {
                    result shouldBe false
                    password1 shouldNotBe password2
                }
            }
        }
    }

    context("hashCode - 같은 값을 가진 HashedPassword는 같은 hashCode") {
        Given("같은 값을 가진 두 개의 HashedPassword가 있고") {
            val password1 = HashedPassword("hashed-password")
            val password2 = HashedPassword("hashed-password")

            When("hashCode를 비교하면") {
                val hashCode1 = password1.hashCode()
                val hashCode2 = password2.hashCode()

                Then("같은 hashCode를 가진다") {
                    hashCode1 shouldBe hashCode2
                }
            }
        }
    }

    context("hashCode - 다른 값을 가진 HashedPassword는 다른 hashCode") {
        Given("다른 값을 가진 두 개의 HashedPassword가 있고") {
            val password1 = HashedPassword("hashed-password-1")
            val password2 = HashedPassword("hashed-password-2")

            When("hashCode를 비교하면") {
                val hashCode1 = password1.hashCode()
                val hashCode2 = password2.hashCode()

                Then("다른 hashCode를 가진다") {
                    hashCode1 shouldNotBe hashCode2
                }
            }
        }
    }

    context("toString - 문자열 표현 포함") {
        Given("HashedPassword가 있고") {
            val passwordValue = "hashed-password-value"
            val hashedPassword = HashedPassword(passwordValue)

            When("toString을 호출하면") {
                val result = hashedPassword.toString()

                Then("문자열 표현이 포함된다") {
                    result shouldNotBe null
                    (result.contains("HashedPassword") || result.contains(passwordValue)) shouldBe true
                }
            }
        }
    }

    context("빈 문자열로 HashedPassword 생성 가능") {
        Given("빈 문자열이 주어지고") {
            val emptyString = ""

            When("HashedPassword를 생성하면") {
                val hashedPassword = HashedPassword(emptyString)

                Then("빈 값이 설정된다") {
                    hashedPassword.value shouldBe ""
                }
            }
        }
    }

    context("매우 긴 해시 값 처리") {
        Given("매우 긴 해시 값이 주어지고") {
            val longHashValue = "a".repeat(1000)

            When("HashedPassword를 생성하면") {
                val hashedPassword = HashedPassword(longHashValue)

                Then("긴 값이 올바르게 저장된다") {
                    hashedPassword.value.length shouldBe 1000
                    hashedPassword.value shouldBe longHashValue
                }
            }
        }
    }

    context("Set에서 중복 제거 - equals와 hashCode 활용") {
        Given("같은 값과 다른 값을 가진 HashedPassword들이 있고") {
            val password1 = HashedPassword("hashed-password")
            val password2 = HashedPassword("hashed-password")
            val password3 = HashedPassword("different-hashed-password")

            When("Set으로 변환하면") {
                val passwordSet = setOf(password1, password2, password3)

                Then("중복이 제거된다") {
                    passwordSet.size shouldBe 2
                    passwordSet shouldContain password1
                    passwordSet shouldContain password3
                }
            }
        }
    }

    context("Map의 키로 사용 가능") {
        Given("두 개의 다른 HashedPassword가 있고") {
            val password1 = HashedPassword("key-password-1")
            val password2 = HashedPassword("key-password-2")
            val map = mutableMapOf<HashedPassword, String>()

            When("Map의 키로 사용하면") {
                map[password1] = "value1"
                map[password2] = "value2"

                Then("각각의 키로 값을 저장할 수 있다") {
                    map.size shouldBe 2
                    map[password1] shouldBe "value1"
                    map[password2] shouldBe "value2"
                }
            }
        }
    }

    context("동일한 키로 Map 값 덮어쓰기") {
        Given("같은 값을 가진 두 개의 HashedPassword가 있고") {
            val password1 = HashedPassword("same-key")
            val password2 = HashedPassword("same-key")
            val map = mutableMapOf<HashedPassword, String>()

            When("같은 키로 Map에 값을 저장하면") {
                map[password1] = "value1"
                map[password2] = "value2"

                Then("값이 덮어써진다") {
                    map.size shouldBe 1
                    map[password1] shouldBe "value2"
                    map[password2] shouldBe "value2"
                }
            }
        }
    }

})