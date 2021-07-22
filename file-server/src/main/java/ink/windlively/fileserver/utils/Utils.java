package ink.windlively.fileserver.utils;

import lombok.NonNull;

import java.util.stream.Stream;

public class Utils {

    public static String makePath(@NonNull String... paths){
        return Stream.of(paths).reduce((p1, p2) ->
                (p1.endsWith("/") ? p1 : p1 + "/") + (p2.startsWith("/") ? p2.substring(1) : p2)).orElse("");
    }
}
