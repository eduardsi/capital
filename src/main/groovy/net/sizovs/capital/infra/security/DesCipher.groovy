package net.sizovs.capital.infra.security


import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

class DesCipher {

    Cipher enc
    Cipher dec

    DesCipher(String secretSalt) {
        def desKeySpec = new DESKeySpec(secretSalt.getBytes('UTF-8'))
        def salt = SecretKeyFactory.getInstance('DES').generateSecret(desKeySpec)

        enc = Cipher.getInstance('DES')
        enc.init(Cipher.ENCRYPT_MODE, salt)

        dec = Cipher.getInstance('DES')
        dec.init(Cipher.DECRYPT_MODE, salt)
    }

    String secret(String plainText) {
        def secretText = enc.doFinal(plainText.bytes)
        new Base64.Encoded(secretText)
    }

    String plain(String secretText) {
        def binary = new Base64.Decoded(secretText).bytes()
        def plainText = dec.doFinal(binary)
        new Utf8String(plainText)
    }


}
