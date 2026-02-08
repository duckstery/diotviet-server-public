package diotviet.server.exceptions;

import lombok.Getter;

@Getter
public class FileServingException extends RuntimeException {

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

    public FileServingException(String key) {
        super(key);
        this.key = key;
    }
}
