package net.sizovs.capital

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.sizovs.capital.infra.json.LocalDateDeserializer

import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
class Invoice {

    Recipient recipient

    String documentNumber

    String orderRef

    @JsonDeserialize(using = LocalDateDeserializer)
    LocalDate issuedAt = LocalDate.now()

    @JsonDeserialize(using = LocalDateDeserializer)
    LocalDate dueAt = issuedAt.plusWeeks(2)

    String billedName

    String billedIdentificationNumber

    String billedAddress

    String itemName

    String itemDescription

    BigDecimal vatPercents

    BigDecimal discountPercents

    BigDecimal rate

    BigDecimal quantity

    BigDecimal getPrice() {
        (discountedRate * quantity).setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    BigDecimal getSubtotal() {
        price.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    BigDecimal getRateDiscount() {
        rate * (discountPercents ?: 0.00) / 100.00
    }

    BigDecimal getVat() {
        (price * vatPercents / 100.00).setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    BigDecimal getTotal() {
        (subtotal + vat).setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    BigDecimal getDiscountedRate() {
        (rate - rateDiscount).setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    void setVatPercents(BigDecimal vatPercents) {
        this.vatPercents = vatPercents.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    void setDiscountPercents(BigDecimal discountPercents) {
        this.discountPercents = discountPercents?.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    void setRate(BigDecimal rate) {
        this.rate = rate.setScale(2, BigDecimal.ROUND_HALF_UP)
    }

    void setQuantity(BigDecimal quantity) {
        this.quantity = quantity.setScale(2, BigDecimal.ROUND_HALF_UP)
    }
}
