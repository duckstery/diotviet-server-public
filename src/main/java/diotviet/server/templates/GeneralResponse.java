package diotviet.server.templates;

/**
 * General response object
 *
 * @param success
 * @param message
 * @param payload
 */
public record GeneralResponse(boolean success, String message, Object payload) {
    /**
     * Success response body with message
     *
     * @param message
     * @param payload
     * @return
     */
    public static GeneralResponse success(String message, Object payload) {
        return new GeneralResponse(true, message, payload);
    }

    /**
     * Success response body without message
     *
     * @param payload
     * @return
     */
    public static GeneralResponse success(Object payload) {
        return new GeneralResponse(true, "", payload);
    }

    /**
     * Fail response body with message
     *
     * @param message
     * @param payload
     * @return
     */
    public static GeneralResponse fail(String message, Object payload) {
        return new GeneralResponse(true, message, payload);
    }

    /**
     * Fail response body without message
     *
     * @param payload
     * @return
     */
    public static GeneralResponse fail(Object payload) {
        return new GeneralResponse(true, "", payload);
    }
}
