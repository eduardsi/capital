package net.sizovs.capital.infra.security

class DecryptedText {

    DesCipher cipher

    String encryptedText

    DecryptedText(String salt, String encryptedText) {
        this.encryptedText = encryptedText
        this.cipher = new DesCipher(salt)
    }


    @Override
    public String toString() {
        cipher.plain(encryptedText)
    }
}
