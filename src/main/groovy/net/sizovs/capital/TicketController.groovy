package net.sizovs.capital

import groovy.transform.Immutable
import groovy.transform.ToString
import net.sizovs.capital.infra.security.EncryptedText
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import rx.Observable

@RestController
class TicketController {

    @Value('${security.crypt.email.salt}')
    String emailSalt

    @Value('${tickets.api.key}')
    String ticketApiKey

    @Autowired
    OrderRepository orderRepository

    @Autowired
    TicketLinkRepository ticketLinkRepository

    RestTemplate rest = new RestTemplate()

    @RequestMapping(path = "/orders/{orderRef}/participants", method = RequestMethod.POST)
    void addParticipant(@PathVariable String orderRef, @RequestBody Participant participant) {
        orderRepository.addParticipant(orderRef, participant)
    }

    @RequestMapping(path = "/orders/{orderRef}/tickets", method = RequestMethod.POST)
    void generateTicket(@PathVariable String orderRef) {


        orderRepository.orderByRef(orderRef)
                .flatMap { Observable.from it.tickets.collect { ticket -> [ticket: ticket, order: it] } }
                .filter { it.ticket != null }
                .map {

            def body = new TicketRequest(email: it.ticket.email, product: it.order.productName, name: it.ticket.name, company: it.ticket.employer)
            def headers = new HttpHeaders()
            headers.add("x-api-key", ticketApiKey)
            def entity = new HttpEntity<TicketRequest>(body, headers)


            println "Generating ticket for $body"
            def ticket = rest.postForObject("https://3uq3x7hd8c.execute-api.eu-west-1.amazonaws.com/prod/ticket", entity, TicketResponse)
            println "Got $ticket"
            [ticketLink: ticket.pdf, order: it.order, owner: it.ticket.name]
        }.retry(2)
                .collect({
            new TicketLinks()
        }, { acc, it -> acc.links << new TicketLink(link: it.ticketLink, owner: it.owner); acc.orderRef = it.order.ref; acc.recipient = it.order.billingInformation.email })
                .subscribe({ ticketLinkRepository.save(it) }, { println "WTF $it" })

    }

    @ToString
    static class TicketLinks {
        String recipient
        String orderRef
        Collection<TicketLink> links = []
    }

    static class TicketLink {
        String link
        String owner
    }

    @ToString
    class TicketRequest {
        String email
        String product
        String name
        String company

        String getTicketId() {
            def encryptedEmail = new EncryptedText(emailSalt, email)
            "${encryptedEmail}___$product"
        }
    }

    @Immutable
    static class TicketResponse {
        String status
        String qr
        String pdf
    }

}
