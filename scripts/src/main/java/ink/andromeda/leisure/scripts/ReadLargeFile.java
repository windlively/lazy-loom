package ink.andromeda.leisure.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Scanner;

public class ReadLargeFile {


    public static int[] arr =
            Arrays.stream(new int[]{350000, 1350000, 1600000, 2600000, 2850000, 3850000, 4100000, 5100000, 5350000, 6350000, 6600000, 7600000, 7850000, 8850000})
                    .map(e -> e)
                    .toArray();


    public static void main(String[] args) throws IOException {

        System.out.println(Arrays.toString(arr));

        Path path = Paths.get("/Users/windlively/Downloads/bonds.reax");

        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {

            Scanner scanner = new Scanner(bufferedReader);

            //实际处理的第N个step
            int i = 0;
            // 第N个文件
            int fileId = 0;
            // '# Timestep' 后面的数字索引
            int stepId = 0;
            StringBuilder singleFile = new StringBuilder();

            while (scanner.hasNextLine()) {

                StringBuilder stepBlock = new StringBuilder();

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    stepBlock.append(line).append('\n');
                    if (line.startsWith("# Timestep ")) {
                        stepId = Integer.parseInt(line.substring(10).trim());
                        if(fileId == 1) {
                            System.out.println(stepId);
                            System.out.println(stepBlock);
                            return;
                        }

                        String titleLine;
                        while (scanner.hasNextLine() && (titleLine = scanner.nextLine()).startsWith("#")) {
                            stepBlock.append(titleLine).append('\n');
                        }

                        continue;
                    }

                    if (line.startsWith("#")) {
                        break;
                    }
                }

                if (stepId % 1000 == 0) {
                    singleFile.append(stepBlock);
                }

                if(stepId == arr[fileId]) {
                    System.out.println(i + "," + stepId);
                    Files.writeString(Path.of("/Users/windlively/Downloads/test/" + i + ".txt"), singleFile, StandardOpenOption.CREATE_NEW);
                    fileId ++;
                    singleFile = new StringBuilder();
                }

                i++;

            }


        }


    }
}


