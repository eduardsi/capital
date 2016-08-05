package net.sizovs.capital.infra.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

import java.time.LocalDate

class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    LocalDate deserialize(JsonParser p, DeserializationContext ctx) {
        def oc = p.codec
        def node = oc.readTree(p) as JsonNode

        LocalDate.of(
                node.get("year").intValue(),
                node.get("monthValue").intValue(),
                node.get("dayOfMonth").intValue())
    }
}
