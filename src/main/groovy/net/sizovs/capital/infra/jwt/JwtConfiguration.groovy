package net.sizovs.capital.infra.jwt

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwtConfiguration {

    @Value('${auth0.client_secret}')
    String clientSecret

    @Value('${auth0.client_id}')
    String clientId

    @Value('${auth0.issuer:https://craftsmans.eu.auth0.com/}')
    String issuer

    @Bean
    FilterRegistrationBean jwtFilter() {
        def registrationBean = new FilterRegistrationBean()
        registrationBean.filter = new JwtFilter(clientId, clientSecret, issuer)
        registrationBean.addUrlPatterns("/*")
        registrationBean
    }

}
