package ink.windlively.tools;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 文字的时间表达转换为cron表达式
 */
public class CronExpressionTools {

    public static final Pattern SQL_PATTERN = Pattern.compile("(?i)^\\s*?SELECT\\s+?(\\*|(\\w+\\s*?,?\\s*?))+?\\s+FROM.+", Pattern.CASE_INSENSITIVE);

    // 每分钟触发时间
    public static final Pattern TRIGGER_CONFIG_FOR_MINUTELY_PATTERN = Pattern.compile("[12345]?\\d秒");

    // 每小时触发时间
    public static final Pattern TRIGGER_CONFIG_FOR_HOURLY_PATTERN = Pattern.compile("[12345]?\\d分" + TRIGGER_CONFIG_FOR_MINUTELY_PATTERN + "");

    // 每日触发时间
    public static final Pattern TRIGGER_CONFIG_FOR_DAILY_PATTERN = Pattern.compile("[12]?\\d时" + TRIGGER_CONFIG_FOR_HOURLY_PATTERN + "");

    // 每月触发时间
    public static final Pattern TRIGGER_CONFIG_FOR_MONTHLY_PATTERN = Pattern.compile("(([123]?\\d日)|(月末))" + TRIGGER_CONFIG_FOR_DAILY_PATTERN + "");

    // 每年触发配置
    public static final Pattern TRIGGER_CONFIG_FOR_YEARLY_PATTERN = Pattern.compile("1?\\d月" + TRIGGER_CONFIG_FOR_MONTHLY_PATTERN + "");

    // 每周触发配置
    public static final Pattern TRIGGER_CONFIG_FOR_WEEKLY_PATTERN = Pattern.compile("周[一二三四五六日]" + TRIGGER_CONFIG_FOR_DAILY_PATTERN);

    public static String triggerConfigToCronExpression(String triggerConfig) {
        Asserts.isTrue(StringUtils.isNotEmpty(triggerConfig), "trigger config is empty");
        String[] split = triggerConfig.split("/");
        String first = split[0];
        String time = split[1];
        String s = "";
        switch (first) {
            case "每年": {
                s = "$4 $3 $2 $1 $0 ? *";
                Asserts.isTrue(time.matches("^" + TRIGGER_CONFIG_FOR_YEARLY_PATTERN.pattern() + "$"),
                        "trigger config " + time + ", not match pattern: " + TRIGGER_CONFIG_FOR_YEARLY_PATTERN);

                if(time.contains("月末")){
                    s = "$3 $2 $1 L $0 ? *";
                    time = time.replace("月末","");
                }

                s = setCronParam(time, s);
                break;
            }
            case "每月": {
                s = "$3 $2 $1 $0 * ? *";
                Asserts.isTrue(time.matches("^" + TRIGGER_CONFIG_FOR_MONTHLY_PATTERN.pattern() + "$"),
                        "trigger config " + time + ", not match pattern: " + TRIGGER_CONFIG_FOR_MONTHLY_PATTERN);

                if(time.startsWith("月末")){
                    s = "$2 $1 $0 L * ? *";
                    time = time.substring(2);
                }

                s = setCronParam(time, s);
                break;
            }
            case "每周": {
                s = "$2 $1 $0 ? * $week *";
                Asserts.isTrue(time.matches("^" + TRIGGER_CONFIG_FOR_WEEKLY_PATTERN.pattern() + "$"),
                        "trigger config " + time + ", not match pattern: " + TRIGGER_CONFIG_FOR_MONTHLY_PATTERN);
                s = s.replace("$week", "" + getWeakFromChinese(time.substring(0, 2)));
                time = time.substring(2);
                s = setCronParam(time, s);
                break;
            }
            case "每日": {
                s = "$2 $1 $0 * * ? *";
                Asserts.isTrue(time.matches("^" + TRIGGER_CONFIG_FOR_DAILY_PATTERN.pattern() + "$"),
                        "trigger config " + time + ", not match pattern: " + TRIGGER_CONFIG_FOR_DAILY_PATTERN);
                s = setCronParam(time, s);
                break;
            }
            case "每时": {
                s = "$1 $0 * * * ? *";
                Asserts.isTrue(time.matches("^" + TRIGGER_CONFIG_FOR_HOURLY_PATTERN.pattern() + "$"),
                        "trigger config " + time + ", not match pattern: " + TRIGGER_CONFIG_FOR_HOURLY_PATTERN);
                s = setCronParam(time, s);
                break;
            }
            case "每分": {
                s = "$0 * * * * ? *";
                Asserts.isTrue(time.matches("^" + TRIGGER_CONFIG_FOR_MINUTELY_PATTERN.pattern() + "$"),
                        "trigger config " + time + ", not match pattern: " + TRIGGER_CONFIG_FOR_MINUTELY_PATTERN);
                s = setCronParam(time, s);
                break;
            }
            case "每秒":
                s = "* * * * * ? *";
                break;
            default:
                throw new IllegalStateException("unknown scheduled time config: " + first);
        }
        return s;
    }

    private static int getWeakFromChinese(String word) {
        return Arrays.asList("周日", "周一", "周二", "周三", "周四", "周五", "周六").indexOf(word) + 1;
    }

    private static String setCronParam(String time, String s) {
        String[] strings = time.split("[月日时分秒]");
        for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
            String s1 = strings[i];
            String unit = "$" + i;
            s = s.replace(unit, s1);
        }
        return s;
    }


    public static void main(String[] args) {
        String i = "10 9 22 L 2 *";
         String s = "每年/2月月末22时9分10秒";
        System.out.println(triggerConfigToCronExpression(s));
    }
}
