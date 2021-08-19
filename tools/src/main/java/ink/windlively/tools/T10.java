package ink.windlively.tools;

import org.joda.time.DateTime;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class T10 {

    public static void main(String[] args) {
        DateTime dateTime = new DateTime(new Date(122 * 1000)).minusHours(8);
        String s = dateTime.toString("HH`mm`ss");
        System.out.println(s);
    }
}
