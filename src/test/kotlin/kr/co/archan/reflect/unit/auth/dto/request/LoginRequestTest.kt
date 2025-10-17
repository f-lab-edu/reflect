package kr.co.archan.reflect.unit.auth.dto.request

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldContain
import jakarta.validation.Validation
import jakarta.validation.Validator
import kr.co.archan.reflect.auth.dto.request.LoginRequest

class LoginRequestTest : BehaviorSpec({
    
    val validator: Validator = Validation.buildDefaultValidatorFactory().validator
    
    context("LoginRequest 생성 - 성공") {
        Given("유효한 이메일과 패스워드가 주어지고") {
            val email = "test@example.com"
            val password = "securePassword123!"
            
            When("LoginRequest를 생성하면") {
                val request = LoginRequest(email, password)
                
                Then("이메일과 패스워드가 정상적으로 설정된다") {
                    request.email shouldBe email
                    request.password shouldBe password
                }
            }
        }
    }
    
    context("검증 성공 - 유효한 이메일과 패스워드") {
        Given("유효한 이메일과 패스워드로 LoginRequest가 생성되고") {
            val request = LoginRequest("valid@example.com", "validPass123!")
            
            When("검증을 수행하면") {
                val violations = validator.validate(request)
                
                Then("검증 오류가 없다") {
                    violations.shouldBeEmpty()
                }
            }
        }
    }
    
    context("이메일 검증 실패 - 이메일 공백") {
        Given("이메일이 공백이고") {
            val email = ""
            val password = "securePassword123!"
            
            When("LoginRequest를 검증하면") {
                val violations = validator.validate(LoginRequest(email, password))
                
                Then("이메일 검증 오류가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "email" } shouldBe true
                }
            }
        }
    }
    
    context("이메일 검증 실패 - @ 없음") {
        Given("이메일에 @가 없고") {
            val email = "invalid-email"
            val password = "securePassword123!"
            
            When("LoginRequest를 검증하면") {
                val violations = validator.validate(LoginRequest(email, password))
                
                Then("이메일 검증 오류가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "email" } shouldBe true
                }
            }
        }
    }
    
    context("이메일 검증 실패 - 255자 이상") {
        Given("이메일이 255자 이상이고") {
            val email = "testasdfsdfsasdfdasasdfddasfdddudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadftestasdfsdfsdfsdftsadfgsdgfiuagsidufgisudgfiausgdiufgisdugfiugasidugfiausgdfigusadfasdfdd@test.com"
            val password = "securePassword123!"
            
            When("LoginRequest를 검증하면") {
                val violations = validator.validate(LoginRequest(email, password))
                
                Then("이메일 검증 오류가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "email" } shouldBe true
                }
            }
        }
    }
    
    context("패스워드 검증 실패 - 패스워드 7자 이하") {
        Given("패스워드가 7자 이하이고") {
            val email = "test@example.com"
            val password = "pass1!"
            
            When("LoginRequest를 검증하면") {
                val violations = validator.validate(LoginRequest(email, password))
                
                Then("패스워드 검증 오류가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "password" } shouldBe true
                }
            }
        }
    }
    
    context("패스워드 검증 실패 - 패스워드 65자 이상") {
        Given("패스워드가 65자 이상이고") {
            val email = "test@example.com"
            val password = "verylongpasswordverylongpasswordverylongpasswordverylongpaslong1!"
            
            When("LoginRequest를 검증하면") {
                val violations = validator.validate(LoginRequest(email, password))
                
                Then("패스워드 검증 오류가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "password" } shouldBe true
                }
            }
        }
    }
    
    context("패스워드 검증 실패 - 숫자 없음") {
        Given("패스워드에 숫자가 없고") {
            val email = "test@example.com"
            val password = "passwordwithoutdigit!"
            
            When("LoginRequest를 검증하면") {
                val violations = validator.validate(LoginRequest(email, password))
                
                Then("패스워드 검증 오류가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "password" } shouldBe true
                }
            }
        }
    }
    
    context("패스워드 검증 실패 - 특수문자 없음") {
        Given("패스워드에 특수문자가 없고") {
            val email = "test@example.com"
            val password = "passwordwithoutspecial123"
            
            When("LoginRequest를 검증하면") {
                val violations = validator.validate(LoginRequest(email, password))
                
                Then("패스워드 검증 오류가 발생한다") {
                    violations.shouldNotBeEmpty()
                    violations.any { it.propertyPath.toString() == "password" } shouldBe true
                }
            }
        }
    }
    
    context("equals - 같은 값을 가진 LoginRequest는 동등함") {
        Given("같은 값을 가진 두 개의 LoginRequest가 있고") {
            val request1 = LoginRequest("test@example.com", "password123!")
            val request2 = LoginRequest("test@example.com", "password123!")
            
            When("두 객체를 비교하면") {
                val result = request1 == request2
                
                Then("동등하다") {
                    result shouldBe true
                    request1 shouldBe request2
                }
            }
        }
    }
    
    context("equals - 다른 값을 가진 LoginRequest는 동등하지 않음") {
        Given("다른 값을 가진 두 개의 LoginRequest가 있고") {
            val request1 = LoginRequest("test1@example.com", "password123!")
            val request2 = LoginRequest("test2@example.com", "password123!")
            
            When("두 객체를 비교하면") {
                val result = request1 == request2
                
                Then("동등하지 않다") {
                    result shouldBe false
                    request1 shouldNotBe request2
                }
            }
        }
    }
    
    context("hashCode - 같은 값을 가진 LoginRequest는 같은 hashCode") {
        Given("같은 값을 가진 두 개의 LoginRequest가 있고") {
            val request1 = LoginRequest("test@example.com", "password123!")
            val request2 = LoginRequest("test@example.com", "password123!")
            
            When("hashCode를 비교하면") {
                val hashCode1 = request1.hashCode()
                val hashCode2 = request2.hashCode()
                
                Then("같은 hashCode를 반환한다") {
                    hashCode1 shouldBe hashCode2
                }
            }
        }
    }
    
    context("copy - 일부 프로퍼티 변경") {
        Given("LoginRequest가 있고") {
            val original = LoginRequest("original@example.com", "password123!")
            val newEmail = "new@example.com"
            
            When("일부 프로퍼티를 변경하여 복사하면") {
                val copied = original.copy(email = newEmail)
                
                Then("새로운 인스턴스가 생성되고 변경된 값만 반영된다") {
                    copied.email shouldBe newEmail
                    copied.password shouldBe original.password
                }
            }
        }
    }
    
    context("toString - 문자열 표현 포함") {
        Given("LoginRequest가 있고") {
            val request = LoginRequest("test@example.com", "password123!")
            
            When("toString을 호출하면") {
                val result = request.toString()
                
                Then("올바른 문자열 표현이 반환된다") {
                    result shouldNotBe null
                    result shouldContain "LoginRequest"
                    result shouldContain "test@example.com"
                }
            }
        }
    }
})
