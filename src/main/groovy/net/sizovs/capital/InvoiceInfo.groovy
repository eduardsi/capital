package net.sizovs.capital

import com.fasterxml.jackson.annotation.JsonIgnore

class InvoiceInfo {

    String documentNumber

    Object issuedAt

    @JsonIgnore
    boolean legacy
}
