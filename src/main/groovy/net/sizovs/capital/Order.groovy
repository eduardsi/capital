package net.sizovs.capital

import com.fasterxml.jackson.annotation.JsonIgnore
import com.firebase.client.Firebase
import groovy.transform.Canonical

@Canonical
class Order implements FirebaseRepository.FirebaseRef {

    OrderStatus status

    // this is actually a code!!!
    String productName

    String pricing

    String couponCode

    ProductInfo productInfo

    InvoiceInfo invoice

    PricingInfo pricingInfo

    BillingInfo billingInformation

    Collection<Ticket> tickets = []

    @JsonIgnore
    boolean isFree() {
        pricingInfo.discountPercent == 100.00
    }

    @JsonIgnore
    boolean isNew() {
        !status
    }

    def withProductInfo(Product product) {
        this.productInfo = new ProductInfo(name: product.name, description: product.description)
        this.pricingInfo = new PricingInfo(price: product.pricing[pricing].price, discountPercent: product.coupons[couponCode]?.discountPercent)
        this
    }

    Invoice newInvoice(String documentNumber) {
        def invoice = new Invoice(
                recipient: new Recipient(billingInformation.email),
                orderRef: ref,
                documentNumber: documentNumber,
                billedName: billingInformation.name,
                billedAddress: billingInformation.address,
                billedIdentificationNumber: billingInformation.identificationNumber,
                quantity: tickets.size(),
                vatPercents: 21.00,
                discountPercents: pricingInfo.discountPercent,
                rate: pricingInfo.price,
                itemName: productInfo.name,
                itemDescription: productInfo.description
        )

        invoice
    }


    Firebase firebaseRef() {
        OrderRepository.ordersRef.child(ref)
    }

}
