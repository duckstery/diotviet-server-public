package diotviet.server.views;

public interface Identifiable {
    /**
     * Get id
     *
     * @return
     */
    long getId();

    /**
     * Set id
     *
     * @param id
     * @return
     */
    Identifiable setId(long id);

    /**
     * Get code
     *
     * @return
     */
    String getCode();

    /**
     * Set code
     *
     * @param code
     * @return
     */
    Identifiable setCode(String code);
}
