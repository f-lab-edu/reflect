package kr.co.archan.reflect.global.util

import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.core.ParameterNameDiscoverer
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class LockKeyEvaluator(
    private val parser: ExpressionParser = SpelExpressionParser(),
    private val parameterNameDiscoverer: ParameterNameDiscoverer = DefaultParameterNameDiscoverer()
) {
    fun evaluateKey(keyExpression: String, method: Method, args: Array<Any?>): String {
        val paramNames = parameterNameDiscoverer.getParameterNames(method) ?: emptyArray()
        val context = StandardEvaluationContext().apply {
            for (i in args.indices) {
                setVariable(paramNames.getOrNull(i) ?: "p$i", args[i])
                setVariable("a$i", args[i])
            }
        }
        return parser.parseExpression(keyExpression).getValue(context, String::class.java)
            ?: throw IllegalArgumentException("SpEL 키 평가 실패: $keyExpression")
    }
}