package net.sizovs.capital

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@ContextConfiguration
@WebMvcTest (InvoiceController)
class InvoiceControllerSpecification extends Specification {

    @Autowired
    MockMvc mvc



    def "yoyo"() {
        expect:
        true
    }

}
