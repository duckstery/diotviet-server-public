package diotviet.server.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    // ****************************
    // Properties
    // ****************************

    /**
     * Exception message key
     */
    private String key;

    // ****************************
    // Constructor
    // ****************************

    public BadRequestException(String key) {
        super(key);
        this.key = key;
    }
}
