package diotviet.server.exceptions;

import lombok.Getter;

@Getter
public class ServiceValidationException extends RuntimeException {

    // ****************************
    // Properties
    // ****************************

    /**
     * Exception message key
     */
    private String key;
    /**
     * Attribute prefix
     */
    private String prefix;
    /**
     * Invalid attributes
     */
    private String attribute;
    /**
     * Interpolate args
     */
    private String[] args;

    // ****************************
    // Constructor
    // ****************************

    public ServiceValidationException(String key, String prefix, String attribute, String ...args) {
        this.key = key;
        this.prefix = prefix;
        this.attribute = attribute;
        this.args = args;
    }
}
