package diotviet.server.structures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@AllArgsConstructor
@Data
public class Tuple<L, R> extends Pair<L, R> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Left value
     */
    private L left;
    /**
     * Right value
     */
    private R right;

    // ****************************
    // Public API
    // ****************************

    /**
     * Create Tuple
     *
     * @param left
     * @param right
     * @param <L>
     * @param <R>
     * @return
     */
    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }

    /**
     * Replace value
     *
     * @param value new value to be stored in this entry
     * @return
     */
    @Override
    public R setValue(R value) {
        // Save temp
        R temp = right;
        // Set value
        right = value;

        return temp;
    }
}
