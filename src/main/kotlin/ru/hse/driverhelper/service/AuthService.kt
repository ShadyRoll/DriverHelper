package ru.hse.driverhelper.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.hse.driverhelper.auth.JwtAuthentication
import ru.hse.driverhelper.auth.JwtProvider
import ru.hse.driverhelper.auth.JwtRequest
import ru.hse.driverhelper.auth.JwtResponse
import ru.hse.driverhelper.model.Driver
import javax.security.auth.message.AuthException

@Service
class AuthService(
    private val userService: DriverService,
    private val jwtProvider: JwtProvider,
    private val passwordEncoder: PasswordEncoder
) {
    private val refreshStorage: MutableMap<String, String> = HashMap()

    fun login(authRequest: JwtRequest): JwtResponse {
        val user: Driver = userService.getByLogin(authRequest.login)
            ?: throw AuthException("Пользователь не найден")
        return if (passwordEncoder.matches(authRequest.password, user.passwordHash)) {
            val accessToken = jwtProvider.generateAccessToken(user)
            val refreshToken = jwtProvider.generateRefreshToken(user)
            refreshStorage[user.login] = refreshToken
            JwtResponse(accessToken, refreshToken)
        } else {
            throw AuthException("Неправильный пароль")
        }
    }

    fun getAccessToken(refreshToken: String): JwtResponse {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            val claims = jwtProvider.getRefreshClaims(refreshToken)
            val login = claims.subject
            val saveRefreshToken = refreshStorage[login]
            if (saveRefreshToken != null && saveRefreshToken == refreshToken) {
                val user: Driver = userService.getByLogin(login)
                    ?: throw AuthException("Пользователь не найден")
                val accessToken = jwtProvider.generateAccessToken(user)
                return JwtResponse(accessToken, null)
            }
        }
        return JwtResponse(null, null)
    }

    fun refresh(refreshToken: String): JwtResponse {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            val claims = jwtProvider.getRefreshClaims(refreshToken)
            val login = claims.subject
            val saveRefreshToken = refreshStorage[login]
            if (saveRefreshToken != null && saveRefreshToken == refreshToken) {
                val user: Driver = userService.getByLogin(login)
                    ?: throw AuthException("Пользователь не найден")
                val accessToken = jwtProvider.generateAccessToken(user)
                val newRefreshToken = jwtProvider.generateRefreshToken(user)
                refreshStorage[user.login] = newRefreshToken
                return JwtResponse(accessToken, newRefreshToken)
            }
        }
        throw AuthException("Невалидный JWT токен")
    }

    val authInfo: JwtAuthentication
        get() = SecurityContextHolder.getContext().authentication as JwtAuthentication
}
