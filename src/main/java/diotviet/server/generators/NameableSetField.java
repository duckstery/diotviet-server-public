package diotviet.server.generators;

import com.opencsv.bean.AbstractBeanField;
import diotviet.server.views.Nameable;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Convert Nameable Object to CSV line
 *
 * @param <T>
 * @param <I>
 */
public class NameableSetField<T, I> extends AbstractBeanField<T, I> {
    @Override
    protected Object convert(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }

        // Split string of ids by "," and create an Nameable with it
        return Arrays
                .stream(s.split(","))
                .map(this::createNameable)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public String convertToWrite(Object value) {
        if (Objects.isNull(value)) {
            return "";
        }

        // Join ids of Set of Nameable
        return ((Set<Nameable>) value)
                .stream()
                .map(Nameable::getName)
                .collect(Collectors.joining(","));
    }

    // ****************************
    // Private
    // ****************************

    /**
     * Convert id to Nameable
     *
     * @param name
     * @return
     */
    private Nameable createNameable(String name) {
        try {
            // Get the Generic type of Field
            ParameterizedType parameterizedType = (ParameterizedType) getField().getGenericType();
            // Get the type that parameterize the ParameterizedType
            Type type = parameterizedType.getActualTypeArguments()[0];
            // Create instance of field's type and cast to Nameable
            Nameable identifiable = (Nameable) Class.forName(type.getTypeName())
                    .getDeclaredConstructor()
                    .newInstance();

            // Set ID
            identifiable.setName(name);

            return identifiable;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
