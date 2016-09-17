package net.sizovs.capital.infra.mailgun

import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MailgunConfiguration {

    @Value('${mailgun.password}')
    String password

    @Mailgun
    @Bean
    CloseableHttpClient mailgun() {
        def credentialsProvider = credentialsProvider()
        def httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build()
        httpClient
    }

    private credentialsProvider() {
        def credentialsProvider = new BasicCredentialsProvider()
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("api", password))
        credentialsProvider
    }


}
