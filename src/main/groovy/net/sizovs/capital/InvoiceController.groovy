package net.sizovs.capital

import com.firebase.client.Firebase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

import java.util.concurrent.atomic.AtomicLong

@RestController
class InvoiceController {

    @Autowired
    OrderRepository orderRepository

    @Autowired
    ProductRepository productRepository

    @Autowired
    InvoiceRepository invoiceRepository

    @Autowired
    EmailSender emailSender

    @Autowired
    TicketLinkRepository ticketLinkRepository


    def firebaseRoot = new Firebase("https://money-edc4a.firebaseio.com")
    def ordersRef = firebaseRoot.child("orders")


    @PostMapping("/invoices")
    void newInvoice(@RequestBody InvoiceRepresentation invoiceR) {
        invoiceRepository.nextInvoiceNumber().subscribe {
            def invoice = new Invoice(
                    recipient: new Recipient(invoiceR.billableEmail),
                    documentNumber: it,
                    billedName: invoiceR.billableName,
                    billedAddress: invoiceR.billableAddress,
                    billedIdentificationNumber: invoiceR.billableId,
                    quantity: invoiceR.quantity,
                    vatPercents: invoiceR.vatPercents,
                    discountPercents: invoiceR.discountPercents,
                    rate: invoiceR.price,
                    itemName: invoiceR.serviceName,
                    itemDescription: invoiceR.serviceDescription
            )
            invoiceRepository.save(invoice)
        }
    }


    @PostMapping("/email/{to}/{cc}/{documentNumber}")
    void sendInvoice(@PathVariable to, @PathVariable cc, @PathVariable documentNumber, @RequestBody String body) {

        println "Received request"
        def emailTemplate = new EmailTemplate("default")
        def emailBody = emailTemplate.render([lines: body.readLines(), documentNumber: documentNumber])
        def email = new Email(body: emailBody, subject: "Invoice $documentNumber")
        email.to = to
        email.cc = cc

        println "Preparing to send"
        emailSender.send(email)
    }

    @PostMapping(path = "/tickets/{orderRef}/send/{test}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    WebResult sendTickets(@PathVariable String orderRef, @PathVariable boolean test) {
        ticketLinkRepository.ticketLinksByOrderRef(orderRef) { TicketController.TicketLinks ticketLinks ->
            def email = generateEmail(ticketLinks)
            email.cc = "eduards@sizovs.net"
            if (test) {
                email.to = "eduards@sizovs.net"
            }
            emailSender.send(email)
        }

    }

    @RequestMapping(path = "/invoices/{orderRef}/send/{test}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    WebResult sendInvoice(@PathVariable String orderRef, @PathVariable boolean test) {

        invoiceRepository.invoiceByOrderRef(orderRef) { Invoice invoice ->
            def email = generateEmail(invoice)
            email.cc = "eduards@sizovs.net"
            if (test) {
                email.to = "eduards@sizovs.net"
            }

            emailSender.send(email)
        }

    }

    Email generateEmail(TicketController.TicketLinks it) {
        def emailTemplate = new EmailTemplate("tickets")
        def emailBody = emailTemplate.render([ticketLinks: it.links])
        def email = new Email(body: emailBody, subject: "DevTernity tickets")
        email.to = it.recipient
        email
    }

    @RequestMapping(path = "/orders/{orderRef}/invoices", method = RequestMethod.POST)
    void generateInvoice(@PathVariable String orderRef) {
        def inc = new AtomicLong()
        def order = orderRepository.orderByRef(orderRef)

        // @formatter:off
        order
            .flatMap { productRepository.get(it.productName).map { product -> it.withProductInfo(product) } }
            .flatMap {
                invoiceRepository.nextInvoiceNumber(inc).map { documentNumber -> it.newInvoice(documentNumber) }
            }
            .doOnNext {
                invoiceRepository.save it
            }
            .subscribe()
        // @formatter:on
    }

    Email generateEmail(Invoice it) {
        def emailTemplate = new EmailTemplate("devternity")
        def emailBody = emailTemplate.render([documentNumber: it.documentNumber, name: it.billedName])
        def email = new Email(body: emailBody, subject: "Invoice $it.documentNumber")
        email.to = it.recipient
        email
    }

    @RequestMapping(path = "/orders/{orderRef}/status/{status}", method = RequestMethod.POST)
    void setStatus(@PathVariable String orderRef, @PathVariable String status) {
        ordersRef.child(orderRef).child("status").value = status
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    void createForRegistrations() {


        def inc = new AtomicLong()

        // @formatter:off
        orderRepository.listTyped()
            .filter { it.new }
            .flatMap { productRepository.get(it.productName).map { product -> it.withProductInfo(product) } }
            .doOnNext { println it }
            .filter { !it.free }
            .flatMap { invoiceRepository.nextInvoiceNumber(inc).map { documentNumber -> it.newInvoice(documentNumber) } }
            .doOnNext {
                invoiceRepository.save(it)
                ordersRef.child(it.orderRef).child("invoice").value = new InvoiceInfo(documentNumber: it.documentNumber, issuedAt: it.issuedAt)
            }
            .subscribe({ println it }, { println "Exception, $it" })

        // @formatter:on

    }


}
