package diotviet.server.utils;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.codec.Hex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * Other utility
 */
public abstract class OtherUtils {

    // ****************************
    // Public API
    // ****************************

    /**
     * Try to get o1, if it's null, get o2 instead
     *
     * @param o1
     * @param o2
     * @return
     */
    public static <T> T get(T o1, T o2) {
        return Objects.nonNull(o1) ? o1 : o2;
    }

    /**
     * Sort
     *
     * @param list
     * @return
     */
    public static List<String> sort(List<String> list) {
        // Sort list
        Collections.sort(list);

        return list;
    }

    /**
     * Hash
     *
     * @param bytes
     * @return
     */
    public static String hash(byte[] bytes, boolean useSalt) {
        // Output
        String hash = "";

        try {
            // Get instance
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            // Add salt
            if (useSalt) {
                md.update(salt());
            }
            // Digest the file at path
            byte[] messageDigest = md.digest(bytes);

            // Convert message digest into hex value and append 0 to make it 256bit
            hash = new String(Hex.encode(messageDigest));
        } catch (NoSuchAlgorithmException e) {
            // This should not happen
            e.printStackTrace();
        }

        return hash;
    }

    /**
     * Generate salt
     *
     * @return
     */
    public static byte[] salt() {
        // Create a secure random
        SecureRandom random = new SecureRandom();
        // Output
        byte[] salt = new byte[16];
        // Generate salt
        random.nextBytes(salt);

        return salt;
    }

    /**
     * Convert Date to DateTimeString
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDateTime(TemporalAccessor date, String format) {
        if (Objects.isNull(date)) {
            return "";
        }

        try {
            return DateTimeFormatter.ofPattern(format).format(date);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * Format money
     *
     * @param value
     * @return
     */
    public static String formatMoney(String value) {
        try {
            // Try to change value to Number
            Long holder = Long.parseLong(value);
            return NumberFormat.getInstance(LocaleContextHolder.getLocale()).format(holder).replace(".", ",");
        } catch (NumberFormatException e) {
            return value;
        }
    }

    /**
     * Get type arguments of clazz
     *
     * @param clazz
     * @return
     */
    public static Class<?>[] getTypeArguments(Class<?> clazz) {
        // Output holder
        List<Class<?>> arguments = new ArrayList<>();

        try {
            // Get GenericSuperclass (It's the class that after the "extends". Ex: Abstract<T>)
            ParameterizedType superclass = (ParameterizedType) clazz.getGenericSuperclass();
            // Iterate through each actual type argument of superclass (It's T or multiple T)
            for (Type type : superclass.getActualTypeArguments()) {
                arguments.add(Class.forName(type.getTypeName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arguments.toArray(new Class<?>[0]);
    }

    /**
     * Use reflection to invoke getter of object
     *
     * @param object
     * @param method
     * @return
     */
    public static Object invokeGetter(Object object, String method) throws NoSuchMethodException {
        // Holder
        Object output = "";
        try {
            output = object.getClass().getMethod(method).invoke(object);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return output;
    }

    /**
     * Get property of first item or use default value
     *
     * @param items
     * @param getter
     * @param defaultValue
     * @return
     */
    public static Object getFirstOrUseDefault(List<Object> items, String getter, Object defaultValue) {
        // Create output
        Object output = defaultValue;

        try {
            // Try to invoke getter
            output = invokeGetter(items.get(0), getter);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("List is null");
        } catch (Exception e) {
            System.out.println("No getter " + getter + " for " + items.get(0));
        }
        return output;
    }

    /**
     * Convert any LocalDate to Date
     *
     * @param LocalDate
     * @return
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
