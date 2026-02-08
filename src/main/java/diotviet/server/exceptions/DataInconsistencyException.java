package diotviet.server.exceptions;

import lombok.Getter;

@Getter
public class DataInconsistencyException extends RuntimeException {

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

    public DataInconsistencyException(String key) {
        super(key);
        this.key = key;
    }
}
