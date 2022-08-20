package ink.windlively.tools;

import org.joda.time.DateTime;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class T10 {

    public static void main(String[] args) {
        int[] ints = new int[12];
        List<Integer> collect = IntStream.of(ints).boxed().collect(Collectors.toList());
    }
}
