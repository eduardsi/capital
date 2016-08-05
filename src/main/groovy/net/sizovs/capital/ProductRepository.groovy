package net.sizovs.capital

import org.springframework.stereotype.Repository
import rx.Observable

import static net.sizovs.capital.infra.rx.RxFirebase.observe

@Repository
class ProductRepository extends FirebaseRepository {

    def productRx = { product ->
        observe root.child("pricing/$product") map {
            it.value
        }
    }

    Observable get(String productCode) {
        productRx(productCode).map {
            mapper.convertValue(it, Product)
        }
        .doOnEach { println it }
    }

}
