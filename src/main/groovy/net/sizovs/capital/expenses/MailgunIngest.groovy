package net.sizovs.capital.expenses

import com.amazonaws.auth.BasicAWSCredentials
import com.fasterxml.jackson.databind.ObjectMapper
import net.sizovs.capital.infra.mailgun.Mailgun
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MailgunIngest {

    def domain = "sandbox3f0ecc7319334273bbe7975545db1f13.mailgun.org"

    @Autowired
    @Mailgun
    HttpClient httpClient

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    ExpenseRepository expenseRepository

    @Value('${aws.access_key}')
    String awsAccessKey

    @Value('${aws.secret}')
    String awsSecret


    @Scheduled(fixedDelay = 30000L)
    void ingest() {
        def eventsGet = new HttpGet("https://api.mailgun.net/v3/$domain/events?event=stored")
        def events = objectMapper.readValue(exec(eventsGet), Events)

        events.items.findAll { it.message.attachments }.each {
            it.message.attachments.each { attachment ->
                def awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecret)
                def s3Attachment = new S3Attachment(awsCredentials, it.storage.key)
                if (!s3Attachment.exists()) {
                    def mailgunAttachment = exec(new HttpGet("$it.storage.url/attachments/0"))
                    s3Attachment.write(mailgunAttachment)
                    expenseRepository.save(new Expense(url: "https://s3.eu-central-1.amazonaws.com/capital-expenses/${s3Attachment.key()}"))
                }
            }

        }
    }
    // https://so.api.mailgun.net/v3/domains/sandbox3f0ecc7319334273bbe7975545db1f13.mailgun.org/messages/eyJwIjp0cnVlLCJrIjoiNzljNjM2MTktYWJjNy00ZDIxLWI2YjctZDExNjEyYjhiYWViIiwicyI6ImNmOGQ0ZjhjOTciLCJjIjoiYmJvcmQifQ==
    // https://so.api.mailgun.net/v3/domains/sandbox3f0ecc7319334273bbe7975545db1f13.mailgun.org/messages/eyJwIjp0cnVlLCJrIjoiNzljNjM2MTktYWJjNy00ZDIxLWI2YjctZDExNjEyYjhiYWViIiwicyI6ImNmOGQ0ZjhjOTciLCJjIjoiYmJvcmQifQ==/attachments/0

    private exec(HttpGet action) {
        httpClient.execute(action).entity.content
    }

    static class Events {
        Collection<Event> items
    }

    static class Event {
        Storage storage
        Message message
    }

    static class Message {
        Collection<Attachment> attachments
    }

    static class Attachment {
        String filename
        long size
    }

    static class Storage {
        String url
        String key
    }

}
