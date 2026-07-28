package ink.windlively.fileserver.controller;

import ink.windlively.fileserver.model.FileModel;
import ink.windlively.fileserver.model.FileServerConfig;
import ink.windlively.fileserver.model.HttpResult;
import ink.windlively.fileserver.service.FileService;
import ink.windlively.fileserver.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class FileController {

    private final FileService fileService;

    private final FileServerConfig fileServerConfig;

    public FileController(FileService fileService,
                          FileServerConfig fileServerConfig) {
        this.fileService = fileService;
        this.fileServerConfig = fileServerConfig;
    }

    // ==================== 文件上传 API ====================

    @PostMapping("/one-file")
    public HttpResult<String> fileUpload(@RequestParam(name = "file") MultipartFile multipartFile,
                                         @RequestParam(name = "path") String path,
                                         @RequestParam(name = "saveAsOriginalName", required = false, defaultValue = "true") boolean saveAsOriginalName) {
        return fileService.saveOneFile(multipartFile, path, saveAsOriginalName);
    }

    /**
     * 分片上传：接收某个文件的单个分片并写入指定偏移位置，返回已接收的总字节数
     */
    @PostMapping("/upload/chunk")
    public HttpResult<Long> uploadChunk(@RequestParam(name = "fileId") String fileId,
                                        @RequestParam(name = "offset") long offset,
                                        @RequestParam(name = "chunk") MultipartFile chunk) {
        try {
            long received = fileService.saveChunk(fileId, offset, chunk);
            return HttpResult.SUCCESS(received);
        } catch (Exception e) {
            log.error("save chunk error, fileId: {}", fileId, e);
            return HttpResult.FAILED(e.getMessage());
        }
    }

    /**
     * 查询某个上传任务已接收的字节数（用于断点续传）
     */
    @GetMapping("/upload/progress")
    public HttpResult<Long> uploadProgress(@RequestParam(name = "fileId") String fileId) {
        try {
            return HttpResult.SUCCESS(fileService.getUploadedSize(fileId));
        } catch (Exception e) {
            return HttpResult.FAILED(e.getMessage());
        }
    }

    /**
     * 完成上传：所有分片上传完毕后调用，合并为最终文件
     */
    @PostMapping("/upload/complete")
    public HttpResult<String> uploadComplete(@RequestParam(name = "fileId") String fileId,
                                             @RequestParam(name = "fileName") String fileName,
                                             @RequestParam(name = "path", required = false) String path,
                                             @RequestParam(name = "saveAsOriginalName", required = false, defaultValue = "true") boolean saveAsOriginalName,
                                             @RequestParam(name = "totalSize") long totalSize) {
        try {
            return fileService.completeUpload(fileId, fileName, path, saveAsOriginalName, totalSize);
        } catch (Exception e) {
            log.error("complete upload error, fileId: {}", fileId, e);
            return HttpResult.FAILED(e.getMessage());
        }
    }

    /**
     * 取消上传，清理已上传的临时分片
     */
    @PostMapping("/upload/abort")
    public HttpResult<String> uploadAbort(@RequestParam(name = "fileId") String fileId) {
        try {
            fileService.abortUpload(fileId);
            return HttpResult.SUCCESS("已取消");
        } catch (Exception e) {
            return HttpResult.FAILED(e.getMessage());
        }
    }

    // ==================== 文件浏览 API ====================

    /**
     * 列出指定目录下的文件和子目录
     */
    @GetMapping("/api/list")
    public HttpResult<List<FileModel>> listFiles(@RequestParam(name = "path", required = false, defaultValue = "/") String path) {
        String filePath = Utils.makePath(fileServerConfig.getWorkspace(), path);
        File file = new File(filePath);
        if (!file.exists()) {
            return HttpResult.FAILED("目录不存在: " + path);
        }
        if (!file.isDirectory()) {
            return HttpResult.FAILED("路径不是目录: " + path);
        }
        List<FileModel> childs = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                FileModel fm = new FileModel(
                        Utils.makePath(path, f.getName()),
                        f.getName(),
                        f.isDirectory(),
                        new Date(f.lastModified()),
                        f.length());
                childs.add(fm);
            }
        }
        return HttpResult.SUCCESS(childs);
    }

    /**
     * 删除文件或目录
     */
    @DeleteMapping("/api/delete")
    public HttpResult<String> delete(@RequestParam(name = "path") String path) {
        if (path == null || path.equals("/") || path.isEmpty()) {
            return HttpResult.FAILED("根目录不可删除");
        }
        String filePath = Utils.makePath(fileServerConfig.getWorkspace(), path);
        File file = new File(filePath);
        if (!file.exists()) {
            return HttpResult.FAILED("文件不存在: " + path);
        }
        try {
            if (file.isDirectory()) {
                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path f, BasicFileAttributes attributes) throws IOException {
                        Files.delete(f);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                Files.delete(file.toPath());
            }
            log.info("delete file: {}", filePath);
            return HttpResult.SUCCESS("删除成功: " + path);
        } catch (IOException e) {
            log.error("delete failed: {}", filePath, e);
            return HttpResult.FAILED("删除失败: " + e.getMessage());
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("/api/download")
    public ResponseEntity<Resource> download(@RequestParam(name = "path") String path) {
        String filePath = Utils.makePath(fileServerConfig.getWorkspace(), path);
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String encodedName = UriUtils.encode(file.getName(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }
}
