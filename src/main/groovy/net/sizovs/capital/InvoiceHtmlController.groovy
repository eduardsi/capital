package net.sizovs.capital

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class InvoiceHtmlController {

    @Autowired
    InvoiceRepository invoiceRepository

    @RequestMapping(value = "/invoice/{documentNumber}", method = RequestMethod.GET)
    public String forwardToInvoice(@PathVariable String documentNumber) {
        println documentNumber
        "forward:/invoice.html"
    }

}
