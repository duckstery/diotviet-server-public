package diotviet.server.exceptions;

import lombok.Getter;

@Getter
public class FileUploadingException extends RuntimeException {

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

    public FileUploadingException() {
        this.key = "invalid_file";
    }
}
