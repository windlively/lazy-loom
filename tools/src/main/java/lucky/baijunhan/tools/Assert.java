package lucky.baijunhan.tools;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class Assert {

    public static void notNull(Object obj, String message) {
        if (obj == null)
            throw message == null ? new NullPointerException() : new NullPointerException(message);
    }

    public static void notNull(Object obj) {
        notNull(obj, null);
    }

    public static void notEmpty(String str, String message) {
        if (StringUtils.isEmpty(str))
            throw message == null ? new IllegalArgumentException() : new IllegalArgumentException(message);
    }

    public static void notEmpty(String str) {
        notEmpty(str, null);
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty())
            throw message == null ? new IllegalArgumentException() : new IllegalArgumentException(message);
    }

    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection, null);
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression)
            throw message == null ? new IllegalStateException() : new IllegalStateException(message);
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, null);
    }

    public static void isFalse(boolean expression, String message) {
        isTrue(!expression, message);
    }

    public static void isFalse(boolean expression) {
        isFalse(expression, null);
    }
}
