package net.sizovs.capital

import net.sizovs.capital.infra.rx.RxFirebase
import org.springframework.stereotype.Repository
import rx.Observable

import java.util.concurrent.atomic.AtomicLong

@Repository
class InvoiceRepository extends FirebaseRepository {

    def invoicesRef = root.child("invoices")

    void save(Invoice invoice) {
        invoicesRef.child(invoice.documentNumber).value = invoice
    }

    Observable<Integer> count(String ref) {
        RxFirebase.observe(invoicesRef.child(ref)).count()
    }


    WebResult invoiceByOrderRef(String ref, Closure onSubscribe) {
        def deferredResult = new WebResult()
        def rx = RxFirebase.observe invoicesRef.orderByChild("orderRef").equalTo(ref)
        rx.map {
            mapper.convertValue((it.value as Map).values().first(), Invoice)
        }
        .subscribe({ item ->
            onSubscribe(item)
            deferredResult.result = "{}"
        }, { exception ->
            deferredResult.errorResult = exception
        })

        deferredResult
    }


    Observable list() {
        RxFirebase.observe(invoicesRef)
    }

    Observable<Invoice> listTyped() {
        list().flatMap { Observable.from it.getValue(Map).values() }.map { it as Invoice }
    }

    Observable<String> nextInvoiceNumber() {
        nextInvoiceNumber(new AtomicLong())
    }


    @SuppressWarnings("GroovyAssignabilityCheck")
    Observable<String> nextInvoiceNumber(AtomicLong inc) {
        list()
                .map { it.getValue(Map)?.keySet() ?: ["2016-55"] }
                .map { it.collect { ((it =~ /\d{4}-(\d+)/)[0][1] as long) }.max() }
                .map { it + inc.incrementAndGet() }
                .map { "2016-$it" }
    }

}
