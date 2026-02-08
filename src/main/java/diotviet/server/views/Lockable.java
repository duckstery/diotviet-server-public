package diotviet.server.views;

public interface Lockable extends Identifiable {
    /**
     * Src
     *
     * @return
     */
    Long getVersion();

    /**
     * Set name
     *
     * @param name
     */
    Lockable setVersion(Long version);
}
