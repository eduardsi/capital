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

    @JsonIgnore
    Collection<Ticket> tickets = []

    Long reservations

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

    Invoice newInvoice(String documentNumber, BigDecimal vat) {
        def invoice = new Invoice(
                recipient: new Recipient(billingInformation.email),
                orderRef: ref,
                documentNumber: documentNumber,
                billedName: billingInformation.name,
                billedAddress: billingInformation.address,
                billedIdentificationNumber: billingInformation.vatNumber ? billingInformation.vatNumber : billingInformation.identificationNumber,
                quantity: tickets ? tickets.size() : reservations,
                vatPercents: vat,
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
