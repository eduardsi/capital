package net.sizovs.capital

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.firebase.client.Firebase

abstract class FirebaseRepository {

    protected static final def mapper = new ObjectMapper()

    static {
        mapper.registerModule(new JavaTimeModule())
        mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));

    }

    protected static final root = new Firebase("https://money-edc4a.firebaseio.com")

    static class FirebaseIterable<T extends FirebaseRef> implements Iterable<T> {

        private Map map
        private Class<T> type

        FirebaseIterable(Object map, Class<T> type) {
            this.type = type
            this.map = map as Map
        }

        @Override
        Iterator<T> iterator() {
            map.entrySet().collect {
                def entity = mapper.convertValue(it.value, type)
                entity.ref = it.key
                entity
            }.iterator()
        }
    }


    static trait FirebaseRef {
        String ref
    }

}
