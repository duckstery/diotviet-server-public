package diotviet.server.exceptions;

import lombok.Getter;

@Getter
public class ExportCSVException extends RuntimeException {

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

    public ExportCSVException(String key) {
        this.key = key;
    }
}
