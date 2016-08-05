package net.sizovs.capital.infra.security

import groovy.transform.Immutable

interface Base64 {

    @Immutable
    class Encoded {

        byte[] origin

        @Override
        String toString() {
            new Utf8String(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(origin))
        }
    }

    @Immutable
    class Decoded {

        String origin

        byte[] bytes() {
            org.apache.commons.codec.binary.Base64.decodeBase64(origin)
        }

    }


}
