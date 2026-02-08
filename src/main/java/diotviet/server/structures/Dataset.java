package diotviet.server.structures;

import diotviet.server.views.Point;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Dataset<X, Y> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Dataset's key
     */
    private String key;

    /**
     * Dataset's hint
     */
    private String hint;

    /**
     * Dataset's stack group
     */
    private String stack;

    /**
     * Dataset's color
     */
    private String color;

    /**
     * Dataset's data
     */
    private List<DataPoint<X, Y>> data;

    // ****************************
    // Public API
    // ****************************

    /**
     * Create Dataset with key and data
     *
     * @param key
     * @param data
     * @param <X>
     * @param <Y>
     * @return
     */
    public static <X, Y> Dataset<X, Y> of(String key, String stack, String color) {
        // Create
        Dataset<X, Y> dataset = new Dataset<>();

        dataset.setKey(key);
        dataset.setHint(key + "_hint");
        dataset.setStack(stack);
        dataset.setColor(color);
        dataset.setData(new ArrayList<>());

        return dataset;
    }

    /**
     * Add DataPoint to dataset
     *
     * @param dataPoint
     */
    public void add(DataPoint<X, Y> dataPoint) {
        this.data.add(dataPoint);
    }

    /**
     * Add Point to dataset
     *
     * @param dataPoint
     */
    public void add(Point<X, Y> dataPoint) {
        this.data.add(DataPoint.of(dataPoint.getX(), dataPoint.getY()));
    }


    /**
     * Get size of Dataset
     *
     * @return
     */
    public int size() {
        return this.data.size();
    }
}
