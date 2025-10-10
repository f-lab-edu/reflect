package kr.co.archan.reflect.global.config

import kr.co.archan.reflect.global.properties.RedisProperties
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig (
    private val redisProperties: RedisProperties
){
    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        val address = "redis://" + redisProperties.host + ":" + redisProperties.port
        config.useSingleServer()
            .setAddress(address)
            .setDatabase(0)
        return Redisson.create(config)
    }
}