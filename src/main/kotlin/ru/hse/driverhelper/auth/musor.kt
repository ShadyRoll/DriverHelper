package ru.hse.driverhelper.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.client.RestTemplate
import org.springframework.web.filter.GenericFilterBean
import ru.hse.driverhelper.model.Driver
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.security.Key
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.crypto.SecretKey
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

data class JwtRequest(
    val login: String,
    val password: String
)

data class JwtResponse(
    val accessToken: String?,
    val refreshToken: String?
) {
    private val type = "Bearer"
}

@Component
class JwtProvider(
    @Value("\${jwt.secret.access}") jwtAccessSecret: String,
    @Value("\${jwt.secret.refresh}") jwtRefreshSecret: String
) {
    private val jwtAccessSecret: SecretKey
    private val jwtRefreshSecret: SecretKey

    private val logger = LogManager.getLogger()

    init {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret))
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret))
    }

    fun generateAccessToken(user: Driver): String {
        val now = LocalDateTime.now()
        val accessExpirationInstant = now.plusDays(7).atZone(ZoneId.systemDefault()).toInstant()
        val accessExpiration: Date = Date.from(accessExpirationInstant)
        return Jwts.builder()
            .setSubject(user.login)
            .setExpiration(accessExpiration)
            .signWith(jwtAccessSecret)
            .compact()
    }

    fun generateRefreshToken(user: Driver): String {
        val now = LocalDateTime.now()
        val refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant()
        val refreshExpiration: Date = Date.from(refreshExpirationInstant)
        return Jwts.builder()
            .setSubject(user.login)
            .setExpiration(refreshExpiration)
            .signWith(jwtRefreshSecret)
            .compact()
    }

    fun validateAccessToken(accessToken: String): Boolean {
        return validateToken(accessToken, jwtAccessSecret)
    }

    fun validateRefreshToken(refreshToken: String): Boolean {
        return validateToken(refreshToken, jwtRefreshSecret)
    }

    private fun validateToken(token: String, secret: Key): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (expEx: ExpiredJwtException) {
            logger.error("Token expired", expEx)
        } catch (unsEx: UnsupportedJwtException) {
            logger.error("Unsupported jwt", unsEx)
        } catch (mjEx: MalformedJwtException) {
            logger.error("Malformed jwt", mjEx)
        } catch (sEx: SignatureException) {
            logger.error("Invalid signature", sEx)
        } catch (e: Exception) {
            logger.error("invalid token", e)
        }
        return false
    }

    fun getAccessClaims(token: String): Claims {
        return getClaims(token, jwtAccessSecret)
    }

    fun getRefreshClaims(token: String): Claims {
        return getClaims(token, jwtRefreshSecret)
    }

    private fun getClaims(token: String, secret: Key): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secret)
            .build()
            .parseClaimsJws(token)
            .body
    }
}

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtFilter: JwtFilter
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests { authz ->
                authz
                    .anyRequest().permitAll()
                    // .antMatchers("/auth/signin", "/auth/token", "/auth/signup", "/swagger-ui/**").permitAll()
                    // .anyRequest().authenticated()
                    // .and()
                    // .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            }.build()
    }
}

@Component
class JwtFilter(
    private val jwtProvider: JwtProvider
) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, fc: FilterChain) {
        val token = getTokenFromRequest(request as HttpServletRequest)
        if (token != null && jwtProvider.validateAccessToken(token)) {
            val claims = jwtProvider.getAccessClaims(token)
            val jwtInfoToken: JwtAuthentication = JwtUtils.generate(claims)
            jwtInfoToken.setAuthenticated(true)
            SecurityContextHolder.getContext().authentication = jwtInfoToken
        }
        fc.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearer = request.getHeader(AUTHORIZATION)
        return if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            bearer.substring(7)
        } else null
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"
    }
}

class JwtAuthentication : Authentication {
    var authenticated_ = false
    var username: String? = null
    var firstName: String? = null

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf()
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return username
    }

    override fun isAuthenticated(): Boolean {
        return authenticated_
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated_ = isAuthenticated
    }

    override fun getName(): String? {
        return firstName
    }
}

object JwtUtils {

    fun generate(claims: Claims): JwtAuthentication {
        val jwtInfoToken = JwtAuthentication()
        jwtInfoToken.username = claims.subject
        return jwtInfoToken
    }
}

@Configuration
@EnableSwagger2
class DriverHelperConfiguration {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("ru.hse.driverhelper.controller"))
            .paths(PathSelectors.any())
            .build()
    }

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate = builder.build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
