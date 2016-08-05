package net.sizovs.capital

import net.sizovs.capital.TicketController.TicketLinks
import net.sizovs.capital.infra.rx.RxFirebase
import org.springframework.stereotype.Repository

@Repository
class TicketLinkRepository extends FirebaseRepository {

    def ticketLinksRef = root.child("ticketLinks")

    void save(TicketLinks ticketLinks) {
        ticketLinksRef.child(ticketLinks.orderRef).value = ticketLinks
    }

    WebResult ticketLinksByOrderRef(String ref, Closure onSubscribe) {
        def deferredResult = new WebResult()
        def rx = RxFirebase.observe ticketLinksRef.child(ref)
        rx.map {
            mapper.convertValue(it.value, TicketLinks)
//            new TicketLinks(links: it.value as Collection)
        }
        .subscribe({ item ->
            onSubscribe(item)
            deferredResult.result = "{}"
        }, { exception ->
            deferredResult.errorResult = exception
        })

        deferredResult
    }




}
