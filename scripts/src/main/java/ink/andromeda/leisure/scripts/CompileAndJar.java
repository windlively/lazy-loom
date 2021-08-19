package ink.andromeda.leisure.scripts;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CompileAndJar {

    public static String workDir = "/Users/windlively/tmp";

    public static String packageName = "ink.windlively.common.udf";

    public static void main(String[] args) {
        compileAndJar(Arrays.asList("\n" +
                                    "public class SayHello {\n" +
                                    "    " +
                                    "public void sayHello() {\n" +
                                    "        System.out.println(\"Hello World\");\n" +
                                    "    " +
                                    "}\n" +
                                    "}", "\n" +
                                         "public class SayHello2 {\n" +
                                         "    " +
                                         "public void sayHello() {\n" +
                                         "        System.out.println(\"Hello World\");\n" +
                                         "    " +
                                         "}\n" +
                                         "}",
                "public class SayHello3 {\n" +
                "    " +
                "public void sayHello() {\n" +
                "        System.out.println(\"Hello World\");\n" +
                "    " +
                "}\n" +
                "}"));
    }

    public static Pattern FIND_CLASS_NAME_PATTERN = Pattern.compile("(?i)(?<=class)(\\s+?\\w+?\\s+?)(?=\\{)");

    public static void compileAndJar(List<String> javaCode) {
        Runtime runtime = Runtime.getRuntime();

        try {
            File file = new File(workDir);
            if (file.isDirectory()) {
                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            boolean mkdirs = file.mkdirs();

            for (String code : javaCode) {

                Matcher matcher = FIND_CLASS_NAME_PATTERN.matcher(code);
                if (!matcher.find())
                    throw new IllegalStateException("could not found class name");
                String className = matcher.group().trim();
                log.info("class name: {}", className);
                String sourceCodeFile = String.format("%s/%s.java", workDir, className);
                code = "package " + packageName + ";\n"
                       + code;
                Files.write(Paths.get(sourceCodeFile), code.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE);
            }


            String path = CompileAndJar.class.getResource("/compile_and_jar.sh").getPath();
            log.info(path);
            String cmd = String.format("sh %s %s", path, workDir);
            Process exec = runtime.exec(cmd);
            exec.waitFor();

            InputStream errorStream = exec.getErrorStream();
            String errorText = readInputStream(errorStream);

            if (errorText != null && !errorText.isEmpty())
                log.error(errorText);

            log.info("bash output: \n{}", readInputStream(exec.getInputStream()));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void compileAndJar(String code) {
        compileAndJar(Collections.singletonList(code));
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream reader = new ByteArrayOutputStream();
        int b;
        while ((b = inputStream.read()) != -1) {
            reader.write(b);
        }
        return reader.toString("UTF-8");
    }

}
