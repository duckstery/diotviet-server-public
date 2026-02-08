package diotviet.server.traits;

/**
 * Entity provider
 *
 * @param <S> output
 * @param <T> param
 */
public interface EntityProvider<S, T> {
    /**
     * Provide Entity
     *
     * @param param
     * @return
     */
    S provide(T param);
}
