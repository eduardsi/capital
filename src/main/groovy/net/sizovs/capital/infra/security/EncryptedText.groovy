package net.sizovs.capital.infra.security

class EncryptedText {

    DesCipher cipher

    String rawText

    EncryptedText(String salt, String raw) {
        this.rawText = raw;
        this.cipher = new DesCipher(salt)
    }

    @Override
    String toString() {
        cipher.secret(rawText)
    }

    public static void main(String[] args) {
        def raw = "jurgis.brauers@if.lv"

        def secure = new EncryptedText("secret13", raw) as String
        def newRaw = new DecryptedText("secret13", secure) as String
        println newRaw
    }
}