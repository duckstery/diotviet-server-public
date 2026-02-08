package diotviet.server.traits;

import diotviet.server.exceptions.BadRequestException;
import diotviet.server.exceptions.DataInconsistencyException;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.utils.OtherUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public abstract class BaseValidator<T> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Key of GenericType (T)
     */
    private String key;

    // ****************************
    // Public API
    // ****************************

    /**
     * Interrupt validator with an Exception
     *
     * @param reason
     * @param prefix
     * @param attribute
     * @param args
     */
    public void interrupt(String reason, String prefix, String attribute, String... args) {
        throw new ServiceValidationException(reason, prefix, attribute, args);
    }

    /**
     * Interrupt validator with an Exception
     *
     * @param reason
     * @param payload
     */
    public void interrupt(String reason, String payload) {
        interrupt(reason, "", payload);
    }

    /**
     * Interrupt validator with a DataInconsistencyException
     *
     * @param key
     */
    public void inconsistent(String key) {
        throw new DataInconsistencyException(key);
    }

    /**
     * Interrupt validator with a BadRequestException
     *
     * @param key
     */
    public void abort(String key) {
        throw new BadRequestException(key);
    }

    /**
     * Check if obj is not null
     *
     * @param obj
     * @return
     */
    public <S> S isExist(S obj) {
        if (Objects.isNull(obj)) {
            inconsistent("inconsistent_data");
        }

        return obj;
    }

    // ****************************
    // Protected API
    // ****************************

    /**
     * Assert for object
     *
     * @param isRequired
     */
    protected void assertObject(Object object, String attribute, boolean isRequired) {
        try {
            if (isRequired && Objects.isNull(OtherUtils.invokeGetter(object, attribute))) {
                interrupt("required", getKey(), attribute);
            }
        } catch (NoSuchMethodException e) {
            interrupt("required", getKey(), attribute);
        }
    }

    /**
     * Assert for string
     *
     * @param isRequired
     * @param min
     * @param max
     */
    protected void assertString(Object object, String attribute, boolean isRequired, int min, int max) {
        assertObject(object, attribute, isRequired);

        try {
            // Cast object to String
            String value = (String) OtherUtils.invokeGetter(object, attribute);

            // String is required
            if (isRequired && value.isBlank()) {
                interrupt("required", getKey(), attribute);
            }

            if (StringUtils.isEmpty(value)) {
                return;
            }

            // Assert if string length is less than min
            if (value.length() < min || value.length() > max) {
                interrupt("string_min_max", getKey(), attribute, String.valueOf(min), String.valueOf(max));
            }
        } catch (NoSuchMethodException e) {
            interrupt("required", getKey(), attribute);
        }
    }

    /**
     * Assert for string
     *
     * @param object
     * @param attribute
     * @param max
     */
    protected void assertStringRequired(Object object, String attribute, int max) {
        assertString(object, attribute, true, 1, max);
    }

    /**
     * Assert for string
     *
     * @param object
     * @param attribute
     * @param min
     * @param max
     */
    protected void assertStringNonRequired(Object object, String attribute, int min, int max) {
        assertString(object, attribute, false, min, max);
    }

    /**
     * Assert for number
     *
     * @param isRequired
     * @param min
     * @param max
     */
    protected void assertNumb(Object object, String attribute, boolean isRequired, long min, long max) {
        assertObject(object, attribute, isRequired);

        try {
            // Cast object to String
            long value = Long.parseLong(OtherUtils.invokeGetter(object, attribute).toString());

            // Assert if string length is less than min
            if (value < min || value > max) {
                interrupt("int_min_max", getKey(), attribute, String.valueOf(min), String.valueOf(max));
            }
        } catch (NoSuchMethodException e) {
            interrupt("required", getKey(), attribute);
        }
    }

    /**
     * Get key (from ParameterizedType)
     *
     * @return
     */
    protected String getKey() {
        // Return cached key
        if (Objects.nonNull(key)) {
            return key;
        }

        try {
            // Get actual type argument of superclass (It's T)
            key = OtherUtils.getTypeArguments(getClass())[0].getSimpleName().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            key = "";
        }

        return key;
    }
}
