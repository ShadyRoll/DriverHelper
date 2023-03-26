package ru.hse.driverhelper.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.hse.driverhelper.auth.JwtRequest
import ru.hse.driverhelper.auth.JwtResponse
import ru.hse.driverhelper.configuration.AuthenticationConfigConstants
import ru.hse.driverhelper.model.Driver
import ru.hse.driverhelper.service.AuthService
import ru.hse.driverhelper.service.DriverService
import java.util.Date

@RestController("auth")
@RequestMapping("/auth")
class AuthController(
    private val driverService: DriverService,
    private val authService: AuthService,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping("signin")
    fun login(@RequestBody authRequest: JwtRequest): ResponseEntity<JwtResponse?>? {
        val token: JwtResponse = authService.login(authRequest)
        return ResponseEntity.ok(token)
    }

    @PostMapping("token")
    fun getNewAccessToken(@RequestBody request: RefreshJwtRequest): ResponseEntity<JwtResponse?>? {
        val token: JwtResponse = authService.getAccessToken(request.refreshToken)
        return ResponseEntity.ok(token)
    }

    @PostMapping("refresh")
    fun getNewRefreshToken(@RequestBody request: RefreshJwtRequest): ResponseEntity<JwtResponse?>? {
        val token: JwtResponse = authService.refresh(request.refreshToken)
        return ResponseEntity.ok(token)
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody signUpDto: SignUpDto): ResponseEntity<*>? {

        // add check for username exists in a DB
        if (driverService.existsByLogin(signUpDto.login)) {
            return ResponseEntity("Логин уже занят", HttpStatus.BAD_REQUEST)
        }

        val user = Driver(
            login = signUpDto.login,
            name = signUpDto.name,
            passwordHash = passwordEncoder.encode(signUpDto.password)
        )
        driverService.save(user)

        val token = JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + AuthenticationConfigConstants.EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(AuthenticationConfigConstants.SECRET))

        val headers = HttpHeaders()
        headers.add(AuthenticationConfigConstants.HEADER_STRING, AuthenticationConfigConstants.TOKEN_PREFIX + token)
        return ResponseEntity(
            "User registered successfully", headers, HttpStatus.OK
        )
    }
}

data class LoginDto(
    val login: String = "",
    val password: String = ""
)

data class SignUpDto(
    val login: String = "",
    val name: String = "",
    val password: String = ""
)

data class RefreshJwtRequest(
    val refreshToken: String
)