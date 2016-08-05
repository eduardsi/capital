package net.sizovs.capital.infra.security

class Utf8String {

    private byte[] bytes

    Utf8String(byte[] bytes) {

        this.bytes = bytes
    }

    @Override
    String toString() {
        new String(bytes, "UTF-8")
    }
}
