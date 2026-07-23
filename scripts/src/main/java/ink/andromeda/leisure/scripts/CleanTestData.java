package ink.andromeda.leisure.scripts;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 清理 huolala 目录下所有项目测试模块产生的临时数据
 * <p>
 * 目标路径模式: {BASE_DIR}/{project}/{xxx-test}/data
 * 安全策略:
 * 1. 仅匹配直接位于 BASE_DIR 下的项目目录（深度限制）
 * 2. 测试模块目录名必须包含 "test"
 * 3. 仅清理名为 "data" 的目录内容，保留 data 目录本身
 * 4. 默认预览模式，需传入 --clean 参数才执行删除
 * <p>
 * 用法:
 * 运行 main 方法，默认预览；添加 VM 参数或程序参数 --clean 执行清理
 */
public class CleanTestData {

    private static final String BASE_DIR = "/Users/windlively/IdeaProjects/huolala";

    public static void main(String[] args) throws IOException {
        boolean doClean = args.length > 0 && "--clean".equals(args[0]);

        Path baseDir = Paths.get(BASE_DIR);
        if (!Files.isDirectory(baseDir)) {
            System.err.println("错误: 目录不存在 - " + BASE_DIR);
            return;
        }

        System.out.println("============================================");
        System.out.println("  测试临时数据清理工具");
        System.out.println("  扫描目录: " + BASE_DIR);
        System.out.println("  模式: " + (doClean ? "清理模式" : "预览模式"));
        System.out.println("============================================");
        System.out.println();

        List<Path> dataDirs = findTestDataDirs(baseDir);

        if (dataDirs.isEmpty()) {
            System.out.println("未找到需要清理的测试数据目录。");
            return;
        }

        long totalSize = 0;
        System.out.println("发现以下测试数据目录:");
        System.out.println();

        for (Path dir : dataDirs) {
            long size = dirSize(dir);
            totalSize += size;
            System.out.printf("  ✗ %s  (%s)%n", dir, humanReadableSize(size));
        }

        System.out.println();
        System.out.printf("共 %d 个目录，总计占用: %s%n", dataDirs.size(), humanReadableSize(totalSize));
        System.out.println();

        if (!doClean) {
            System.out.println("当前为预览模式，未执行任何删除操作。");
            System.out.println("如需执行清理，请添加参数 --clean 运行。");
            return;
        }

        // 执行清理
        System.out.println("正在清理...");
        System.out.println();
        for (Path dir : dataDirs) {
            System.out.println("  清理: " + dir);
            cleanDirectory(dir);
        }
        System.out.println();
        System.out.printf("✓ 清理完成！已释放约 %s 磁盘空间。%n", humanReadableSize(totalSize));
    }

    /**
     * 查找所有符合条件的测试数据目录
     * 匹配规则: BASE_DIR/{project}/{module-name-contains-test}/data
     */
    private static List<Path> findTestDataDirs(Path baseDir) throws IOException {
        List<Path> result = new ArrayList<>();

        // 遍历 BASE_DIR 下的项目目录（第一层）
        try (Stream<Path> projects = Files.list(baseDir)) {
            for (Path project : projects.filter(Files::isDirectory).toList()) {
                // 遍历项目下的模块目录（第二层）
                try (Stream<Path> modules = Files.list(project)) {
                    for (Path module : modules.filter(Files::isDirectory).toList()) {
                        String moduleName = module.getFileName().toString();
                        // 安全校验: 模块名必须包含 "test"，防止误删正常模块
                        if (!moduleName.contains("test")) {
                            continue;
                        }
                        Path dataDir = module.resolve("data");
                        if (Files.isDirectory(dataDir)) {
                            result.add(dataDir);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 清理目录内容，保留目录本身
     */
    private static void cleanDirectory(Path dir) throws IOException {
        try (Stream<Path> children = Files.list(dir)) {
            for (Path child : children.toList()) {
                deleteRecursively(child);
            }
        }
    }

    /**
     * 递归删除文件或目录
     */
    private static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            Files.delete(path);
        }
    }

    /**
     * 计算目录总大小（字节）
     */
    private static long dirSize(Path dir) {
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk.filter(Files::isRegularFile)
                    .mapToLong(f -> {
                        try {
                            return Files.size(f);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();
        } catch (IOException e) {
            return 0L;
        }
    }

    /**
     * 将字节数转为人类可读格式
     */
    private static String humanReadableSize(long bytes) {
        if (bytes >= 1024L * 1024 * 1024) {
            return String.format("%.1fG", bytes / (1024.0 * 1024 * 1024));
        } else if (bytes >= 1024L * 1024) {
            return String.format("%.1fM", bytes / (1024.0 * 1024));
        } else if (bytes >= 1024L) {
            return String.format("%.1fK", bytes / 1024.0);
        }
        return bytes + "B";
    }
}
