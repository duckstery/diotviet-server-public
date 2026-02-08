package diotviet.server.generators;

import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.Base64;

public class Base64EncodingTextEncryptor implements TextEncryptor {
    private final BytesEncryptor encryptor;

    public Base64EncodingTextEncryptor(BytesEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    public String encrypt(String text) {
        return Base64.getUrlEncoder().encodeToString(this.encryptor.encrypt(Utf8.encode(text)));
    }

    public String decrypt(String encryptedText) {
        return Utf8.decode(this.encryptor.decrypt(Base64.getUrlDecoder().decode(encryptedText)));
    }
}
