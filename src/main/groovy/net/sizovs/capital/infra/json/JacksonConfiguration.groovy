package net.sizovs.capital.infra.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean

class JacksonConfiguration {

    protected static final def mapper = new ObjectMapper()

    static {
        mapper.registerModule(new JavaTimeModule())
        mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));

    }

    @Bean
    ObjectMapper objectMapper() {
        mapper
    }


}
