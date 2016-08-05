package net.sizovs.capital

import net.sizovs.capital.infra.rx.RxFirebase
import org.springframework.stereotype.Repository
import rx.Observable

@Repository
class OrderRepository extends FirebaseRepository {

    static def ordersRef = root.child("orders")

    Observable listTyped() {
        def rx = RxFirebase.observe ordersRef
        rx.flatMap { Observable.from new FirebaseIterable<Order>(it.value, Order) }
    }

    Observable orderByRef(String ref) {
        def rx = RxFirebase.observe ordersRef.child(ref)
        rx.map {
            def order = mapper.convertValue(it.value, Order)
            order.ref = ref
            order
        }
    }




}
