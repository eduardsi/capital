package net.sizovs.capital

import net.sizovs.capital.infra.rx.RxFirebase
import org.springframework.stereotype.Repository
import rx.Observable

@Repository
class OrderRepository extends FirebaseRepository {

    static def ordersRef = root.child("orders")

    void addParticipant(String ref, Participant participant) {
        ordersRef.child(ref).child("tickets").push().value = participant
    }


    Observable listTyped() {
        def rx = RxFirebase.observe ordersRef
        rx.flatMap { Observable.from new FirebaseIterable<Order>(it.value, Order) }
    }

    Observable orderByRef(String ref) {
        def rx = RxFirebase.observe ordersRef.child(ref)
        rx.map {


            def tickets
            if (it.value.tickets instanceof List) {
                tickets = it.value.tickets.collect { mapper.convertValue(it, Ticket) }
            } else {
                tickets = it.value.tickets.values().collect { mapper.convertValue(it, Ticket) }

            }

            def order = mapper.convertValue(it.value, Order)
            order.tickets = tickets
            order.ref = ref
            order
        }
    }


}
