package ink.andromeda.leisure.scripts;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ZhaoKai {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // A0至A3点的最长距离
    private static final Map<Integer, Double> maxLengthMap = new HashMap<Integer, Double>() {{
        put(0, Math.sqrt(Math.pow(5000, 2) + Math.pow(5000, 2) + Math.pow(1300 - 3000, 2)));
        put(1, Math.sqrt(Math.pow(5000, 2) + Math.pow(5000, 2) + Math.pow(1700, 2)));
        put(2, Math.sqrt(Math.pow(5000, 2) + Math.pow(5000, 2) + Math.pow(1700, 2)));
        put(3, Math.sqrt(Math.pow(5000, 2) + Math.pow(5000, 2) + Math.pow(1300 - 3000, 2)));
    }};

    // A0-A3点两两之间的距离
    private static final Map<String, Double> sideLengthMap = new HashMap<String, Double>() {{
        put("01", Math.sqrt(Math.pow(5000, 2) + Math.pow(0, 2) + Math.pow(400, 2)));
        put("02", Math.sqrt(Math.pow(0, 2) + Math.pow(5000, 2) + Math.pow(400, 2)));
        put("03", Math.sqrt(Math.pow(5000, 2) + Math.pow(5000, 2) + Math.pow(0, 2)));
        put("12", Math.sqrt(Math.pow(5000, 2) + Math.pow(5000, 2) + Math.pow(0, 2)));
        put("13", Math.sqrt(Math.pow(0, 2) + Math.pow(5000, 2) + Math.pow(400, 2)));
        put("23", Math.sqrt(Math.pow(5000, 2) + Math.pow(0, 2) + Math.pow(400, 2)));
    }};

    public static void main(String[] args) {
//        checkFile("/Users/windlively/Desktop/2021年E题/附件1：UWB数据集/正常数据/109.正常.txt");
//        checkFile("/Users/windlively/Desktop/2021年E题/附件1：UWB数据集/异常数据/1.异常.txt", false);
        checkFile("/Users/windlively/Desktop/附件5：动态轨迹数据.txt",true);

        // 遍历324个文件，逐一处理
//        IntStream.range(1, 325).forEach(i ->
//                        checkFile("/Users/windlively/Desktop/2021年E题/附件1：UWB数据集/正常数据/" + i + ".正常.txt", true)
//                checkFile("/Users/windlively/Desktop/2021年E题/附件1：UWB数据集/异常数据/"+i+".异常.txt",false)
//
//        );

    }

    public static void checkFile(String path, boolean normally) {
        System.out.printf("处理文件: %s\n", path);
        try {
            // 这一步将文件按行读取，每一行按照":"号分割
            // 读取出来的数据形如： [T, 090531088, RR, 0, 0, 760, 760, 229, 3301]
            List<List<String>> strings = Files.readAllLines(Paths.get(path)).stream()
                    .map(s -> Arrays.asList(s.split(":")))
                    .collect(Collectors.toList());
            strings.remove(0);
//            [T, 090531088, RR, 0, 0, 760, 760, 229, 3301]


            // 按照组号分组后的数据
            // 组号: 对应的四组数据
            Map<String, List<List<String>>> collect = strings.stream().collect(Collectors.groupingBy(s -> s.get(8), TreeMap::new, Collectors.toList()));

            // 遍历，校验每一组数据的长度是否为4个
            // k为组号，v为该组的所有数据
            collect.forEach((k, v) -> {
                // 该内容未打印则表名数据正常
                if (v.size() != 4) {
                    System.err.printf("%s组数据个数不为4: %s", k, v);
                }
            });


            // 将每一组的数据进行拼接，拼接为 {锚点0}-{锚点0距离}|{锚点1}-{锚点1距离}|{锚点2}-{锚点2距离}|{锚点3}-{锚点3距离} 的形式，方便进行比较
            Map<String, String> collectForCompare = new TreeMap<>(collect.entrySet().stream().collect(
                    Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(s -> s.get(4) + "-" + s.get(5)).collect(Collectors.joining("|")))
            ));

            Map<String, String> distinctMap = new HashMap<>();

            // 遍历
            collectForCompare.forEach((k, v) -> {

                if (distinctMap.containsValue(v)) {
                    // 如果这个Map中已经含有当前数据，表明是重复的数据，直接忽略掉
                    System.err.printf("%s组数据重复。%n", k);
                } else {
                    // 如果没有，则保存起来
                    distinctMap.put(k, v);
                }
            });

            // 得到的不重复的数据的组号
            Set<String> distinctGroup = distinctMap.keySet();

            // 筛选出不重复的组及数据
            Map<String, List<List<String>>> distinctCollect = collect.entrySet().stream().filter(s -> distinctGroup.contains(s.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            System.out.printf("筛选完重复后的数据, %s条\n", distinctCollect.size());
//            System.out.println(objectMapper.writeValueAsString(distinctCollect));

            // 异常数据筛选
            Map<String, List<List<String>>> result = new TreeMap<>(distinctCollect.entrySet().stream()
                    .filter(e -> {
                        List<List<String>> value = e.getValue();

                        // 遍历四个锚点的值
                        for (List<String> v : value) {
                            // 锚点id，第四个位置的数
                            Integer stationId = Integer.parseInt(v.get(4));
                            // 距离，第五个位置的数
                            String length = v.get(5);

                            // 从maxLengthMap中取出该锚点所能达到的最大长度数据进行比较
                            if (Double.parseDouble(length) > maxLengthMap.get(stationId)) {
                                System.err.printf("异常数据, 长度校验失败, 组id：%s, 锚点id: %s, 组内值: %s\n", e.getKey(), stationId, value);
                                return false;
                            }
                        }

                        // 根据四组数据进行是否为三角形的校验
//                        if (!checkIsTriangle(value)) {
//                            System.err.printf("异常数据, 三角形规则校验失败, 组id：%s, 组内值: %s\n", e.getKey(), value);
//                            return false;
//                        }

                        return true;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            System.out.printf("筛选完错误后的数据, %s条\n", result.size());
//            System.out.println(objectMapper.writeValueAsString(result));


            if (result.size() == 0) {
//                System.err.println("筛选完错误数据后为空，");
//                String str = distinctCollect.values().stream().flatMap(Collection::stream)
//                        .sorted(Comparator.comparingInt(
//                                l -> Integer.parseInt(l.get(5))
//                        ))
//                        .map(l -> l.get(5))
//                        .collect(Collectors.joining("\n"));
//
//
//                Files.write(Paths.get("/Users/windlively/Desktop/目标/" + path.substring(path.lastIndexOf("/"), path.lastIndexOf(".")) + ".csv"), str.getBytes(StandardCharsets.UTF_8),
//                        StandardOpenOption.CREATE,
//                        StandardOpenOption.WRITE);
//                return;
//                List<List<String>> lists = new ArrayList<>();
//                min10.stream().sorted(Comparator.comparingInt(e -> Integer.parseInt(e.get(8)))).forEach(e -> {
//                    String groupId = e.get(8);
//                    boolean isExist = false;
//                    for (List<String> list : lists) {
//                        if (list.get(0).equals(groupId)) {
//                            String anchorId = e.get(4);
//                            String len = e.get(5);
//                            list.set(Integer.parseInt(anchorId) * 2 + 1, anchorId);
//                            list.set(Integer.parseInt(anchorId) * 2 + 2, len);
//                            isExist = true;
//                            break;
//                        }
//                    }
//                    if(!isExist) {
//                        List<String> arr = new ArrayList<>();
//                        for (int i = 0; i <= 8; i++) arr.add("");
//                        String anchorId = e.get(4);
//                        String len = e.get(5);
//                        arr.set(0, groupId);
//                        arr.set(Integer.parseInt(anchorId) * 2 + 1, anchorId);
//                        arr.set(Integer.parseInt(anchorId) * 2 + 2, len);
//                        lists.add(arr);
//                    }
//                });
//
//                String res = "数据编号, 锚点0, 锚点0测距值, 锚点1, 锚点1测距值, 锚点2, 锚点2测距值, 锚点3, 锚点3测距值\n" +
//                             lists.stream()
//                                     .map(s -> String.join(",", s))
//                                     .collect(Collectors.joining("\n"));

                result = distinctCollect;

            }

            // 异常文件夹下的数据
            if(!normally) {
                List<List<String>> all = result.values().stream().flatMap(Collection::stream)
                        .collect(Collectors.toList());

                List<List<String>> list = new ArrayList<>();

                for (int i = 0; i < all.size() / 4; i++) {
                    List<String> row = new ArrayList<>();
                    for (int j = 0; j <= 8; j++) row.add("");
                    list.add(row);
                }


                for (int i = 0; i < 4; i++) {
                    int finalI = i;
                    List<String> min10 = all.stream()
                            .filter(e -> Integer.parseInt(e.get(4)) == finalI)
                            .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.get(5))))
                            .map(e -> e.get(5))
                            .collect(Collectors.toList());
                    for (int j = 0; j < min10.size(); j++) {
                        List<String> row = list.get(j);
                        row.set(i * 2 + 2, min10.get(j));
                    }
                }
                AtomicInteger c = new AtomicInteger(0);
                Map<Integer, List<String>> map = new TreeMap<>(list.stream().collect(Collectors.toMap(e -> c.getAndIncrement(), e -> e)));

                Map<Integer, String> distinct = new HashMap<>();
                map.forEach(
                        (k, v) -> {
                            String join = String.join("|", v);
                            if(!distinct.containsValue(join)){
                                distinct.put(k, join);
                            }
                        }
                );
                System.out.println(distinct.size());
                map = new TreeMap<>(map.entrySet().stream().filter(e -> distinct.containsKey(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                System.out.println(map.size());
                list = new ArrayList<>(map.values()).stream().limit(10).collect(Collectors.toList());

                String res = "数据编号, 锚点0, 锚点0测距值, 锚点1, 锚点1测距值, 锚点2, 锚点2测距值, 锚点3, 锚点3测距值\n" +
                             list.stream()
                                     .map(s -> String.join(",", s))
                                     .collect(Collectors.joining("\n"));
                Files.write(Paths.get("/Users/windlively/Desktop/目标/" + path.substring(path.lastIndexOf("/"), path.lastIndexOf(".")) + ".csv"), res.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE);
                return;
            }

            //========== 以下部分为数据输出

            StringBuilder res = new StringBuilder();

            // 组成csv格式的字符串文本
            res.append("数据编号, 锚点0, 锚点0测距值, 锚点1, 锚点1测距值, 锚点2, 锚点2测距值, 锚点3, 锚点3测距值\n");
            result.forEach(
                    (k, v) -> {
                        String text = k + "," + v.stream().map(
                                s -> s.get(4) + "," + s.get(5)
                        ).collect(Collectors.joining(","));
                        res.append(text).append("\n");
                    }
            );

            // 写入文件
            Files.write(Paths.get("/Users/windlively/Desktop/目标/" + path.substring(path.lastIndexOf("/"), path.lastIndexOf(".")) + ".csv"), res.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);

            System.out.println("-------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static double max(double... n) {
        return DoubleStream.of(n).max().orElseThrow(IllegalStateException::new);
    }

    public static boolean checkIsTriangle(List<List<String>> lists) {
        // 将四组数据改成 锚点id：当前锚点的数据 的形式，方便后续判断
        Map<Integer, List<String>> collect = lists.stream().collect(Collectors.toMap(e -> Integer.parseInt(e.get(4)), s -> s));

        // 列举6种三角形组合情况，
        int[][] ints = new int[][]{new int[]{0, 1}, new int[]{0, 2}, new int[]{0, 3}, new int[]{1, 2}, new int[]{1, 3}, new int[]{2, 3}};
        // 遍历每一种情况，全部符合才返回true
        return Arrays.stream(ints).allMatch(i -> checkIsTriangle(collect, i[0], i[1]));
    }

    public static boolean checkIsTriangle(Map<Integer, List<String>> collect, int i1, int i2) {
        // 第一个锚点
        List<String> l1 = collect.get(i1);
        // 第二个锚点
        List<String> l2 = collect.get(i2);

        // 第一个锚点的测距
        double len1 = Double.parseDouble(l1.get(5));
        // 第二个锚点的测距
        double len2 = Double.parseDouble(l2.get(5));

        // 两个锚点之间的距离
        double len3 = sideLengthMap.get(String.format("%s%s", i1, i2));

        return len1 + len2 > len3
               && len1 + len3 > len2
               && len2 + len3 > len1;

    }

}
