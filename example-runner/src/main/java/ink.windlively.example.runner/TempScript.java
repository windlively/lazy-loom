package ink.windlively.example.runner;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class TempScript {


    public static void main(String[] args) throws IOException {
        Files.walkFileTree(Path.of("/Users/windlively/IdeaProjects/huolala"), new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.getFileName().toString().startsWith(".")) return FileVisitResult.SKIP_SUBTREE;
                if (dir.getParent().getFileName().toString().endsWith("-test") && dir.getFileName().toString().equals("data")) {
                    FileUtils.deleteDirectory(dir.toFile());
                    System.out.println(STR."delete dir: \{dir}");
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

    }

}
