package ru.hse.driverhelper.configuration

import java.time.Duration

object AuthenticationConfigConstants {
    val EXPIRATION_TIME = Duration.ofDays(10).toMillis()
    const val SECRET = "wow_secret"
    const val TOKEN_PREFIX = "Bearer "
    const val HEADER_STRING = "Authorization"
}

//
// import com.auth0.jwt.JWT
// import com.auth0.jwt.algorithms.Algorithm
// import com.fasterxml.jackson.databind.ObjectMapper
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.web.client.RestTemplateBuilder
// import org.springframework.context.annotation.Bean
// import org.springframework.context.annotation.Configuration
// import org.springframework.security.authentication.AuthenticationManager
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
// import org.springframework.security.config.annotation.web.builders.HttpSecurity
// import org.springframework.security.config.http.SessionCreationPolicy
// import org.springframework.security.core.GrantedAuthority
// import org.springframework.security.core.context.SecurityContextHolder
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
// import org.springframework.security.crypto.password.PasswordEncoder
// import org.springframework.security.web.SecurityFilterChain
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
// import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
// import org.springframework.web.client.RestTemplate
// import org.springframework.web.filter.CommonsRequestLoggingFilter
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
// import ru.hse.driverhelper.controller.SignUpDto
// import ru.hse.driverhelper.service.DriverService
// import springfox.documentation.builders.PathSelectors
// import springfox.documentation.builders.RequestHandlerSelectors
// import springfox.documentation.spi.DocumentationType
// import springfox.documentation.spring.web.plugins.Docket
// import springfox.documentation.swagger2.annotations.EnableSwagger2
// import java.time.Duration
// import java.util.Date
// import javax.servlet.FilterChain
// import javax.servlet.http.HttpServletRequest
// import javax.servlet.http.HttpServletResponse
//
//
//
//
// @Configuration
// class SecurityConfiguration(
//     private val driverService: DriverService
// ) {
//
//     @Autowired
//     fun configureGlobal(auth: AuthenticationManagerBuilder, passwordEncoder: PasswordEncoder) {
//         auth.userDetailsService(driverService).passwordEncoder(passwordEncoder)
//     }
//
//     @Bean
//     fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager? {
//         return configuration.authenticationManager
//     }
// }
//
// @Configuration
// @EnableSwagger2
// class DriverHelperConfiguration(
//     private val objectMapper: ObjectMapper
// ) {
//
//     @Bean
//     fun filterChain(http: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain {
//         http.cors().and().csrf().disable()
//             .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//             .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//             .authorizeRequests().antMatchers("/auth/**").permitAll()
//             .anyRequest().authenticated()
//
//         http.addFilterBefore(AuthTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
//
//         return http.build()
//     }
//
//     @Bean
//     fun passwordEncoder(): PasswordEncoder {
//         return BCryptPasswordEncoder()
//     }
//
//     @Bean
//     fun api(): Docket {
//         return Docket(DocumentationType.SWAGGER_2)
//             .select()
//             .apis(RequestHandlerSelectors.basePackage("ru.hse.driverhelper.controller"))
//             .paths(PathSelectors.any())
//             .build()
//     }
//
//     @Bean
//     fun restTemplate(builder: RestTemplateBuilder): RestTemplate? {
//         return builder.build()
//     }
// }
//
// @Configuration
// class WebMvcConfiguration : WebMvcConfigurer {
//     @Bean
//     fun logFilter(): CommonsRequestLoggingFilter {
//         val filter = CommonsRequestLoggingFilter()
//         filter.setIncludeQueryString(true)
//         filter.setIncludePayload(true)
//         filter.setMaxPayloadLength(100000)
//         filter.setIncludeHeaders(false)
//         filter.setAfterMessagePrefix("REQUEST DATA : ")
//         return filter
//     }
// }
//
// class JWTAuthenticationFilter(
//     private val authenticationManager: AuthenticationManager,
//     private val objectMapper: ObjectMapper
// ) : BasicAuthenticationFilter(authenticationManager) {
//
//     override fun doFilterInternal(
//         request: HttpServletRequest,
//         response: HttpServletResponse,
//         chain: FilterChain
//     ) {
//         if (request.getHeader(AuthenticationConfigConstants.HEADER_STRING) != null) {
//             chain.doFilter(request, response)
//             return
//         }
//
//         val creds: SignUpDto = objectMapper.readValue(request.inputStream, SignUpDto::class.java)
//         authenticationManager.authenticate(
//             UsernamePasswordAuthenticationToken(
//                 creds.login,
//                 creds.password,
//                 ArrayList<GrantedAuthority>()
//             )
//         )
//
//         val token = JWT.create()
//             .withSubject(creds.login)
//             .withExpiresAt(Date(System.currentTimeMillis() + AuthenticationConfigConstants.EXPIRATION_TIME))
//             .sign(Algorithm.HMAC512(AuthenticationConfigConstants.SECRET))
//         response.addHeader(
//             AuthenticationConfigConstants.HEADER_STRING,
//             AuthenticationConfigConstants.TOKEN_PREFIX + token
//         )
//     }
// }
//
// class JWTAuthorizationFilter(authenticationManager: AuthenticationManager?) :
//     BasicAuthenticationFilter(authenticationManager) {
//
//     override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
//         val header = request.getHeader(AuthenticationConfigConstants.HEADER_STRING)
//         if (header == null || !header.startsWith(AuthenticationConfigConstants.TOKEN_PREFIX)) {
//             chain.doFilter(request, response)
//             return
//         }
//         val authentication = getAuthentication(request)
//         SecurityContextHolder.getContext().authentication = authentication
//         chain.doFilter(request, response)
//     }
//
//     private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
//         val token = request.getHeader(AuthenticationConfigConstants.HEADER_STRING)
//         if (token != null) {
//             // parse the token.
//             val user: String = JWT.require(Algorithm.HMAC512(AuthenticationConfigConstants.SECRET))
//                 .build()
//                 .verify(token.replace(AuthenticationConfigConstants.TOKEN_PREFIX, ""))
//                 .subject
//             return UsernamePasswordAuthenticationToken(user, null, ArrayList())
//         }
//         return null
//     }
// }
//
//
//
//
