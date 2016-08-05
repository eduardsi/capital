package net.sizovs.capital

import groovy.util.logging.Slf4j
import org.apache.http.Consts
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Slf4j
@Component
class EmailSender {

    @Value("classpath:facebook.png")
    Resource facebook

    @Value("classpath:twitter.png")
    Resource twitter

    @Value("classpath:linkedin.png")
    Resource linkedin

    void send(Email email) {
        def credentialsProvider = new BasicCredentialsProvider()
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("api", "key-bc4508bf6d471ade7470e14af8c5d8d6"))

        def httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build()
        def post = new HttpPost("https://api.mailgun.net/v3/sizovs.net/messages")


        def utf8Text = ContentType.create("text/plain", Consts.UTF_8)
        def builder = MultipartEntityBuilder
                .create()
                .addTextBody("to", email.to)
                .addTextBody("from", "Eduards Sizovs <eduards@sizovs.net>")
                .addTextBody("html", email.body, utf8Text)
                .addTextBody("subject", email.subject)
                .addBinaryBody("inline", facebook.inputStream.bytes, ContentType.APPLICATION_OCTET_STREAM, "facebook.png")
                .addBinaryBody("inline", twitter.inputStream.bytes, ContentType.APPLICATION_OCTET_STREAM, "twitter.png")
                .addBinaryBody("inline", linkedin.inputStream.bytes, ContentType.APPLICATION_OCTET_STREAM, "linkedin.png")

        if (email.cc) {
            builder.addTextBody("cc", email.cc)
        }

        def entity = builder.build()

        post.entity = entity

        log.info "Sending email ($email.subject) to $email.to"

        httpClient.execute(post)
    }

}
