package ink.windlively.fileserver.service;

import ink.windlively.fileserver.model.FileServerConfig;
import ink.windlively.fileserver.model.FileType;
import ink.windlively.fileserver.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import ink.windlively.fileserver.model.HttpResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    /**
     * 分片上传临时文件存放的子目录名
     */
    private static final String UPLOAD_TEMP_DIR = ".upload_tmp";

    private final FileServerConfig fileServerConfig;

    public FileService(FileServerConfig fileServerConfig) {
        this.fileServerConfig = fileServerConfig;
    }

    public HttpResult<String> saveOneFile(MultipartFile multipartFile, String path, boolean saveAsOriginalName) {
        String originalName = Objects.requireNonNull(multipartFile, "上传文件为空")
                .getOriginalFilename();
        if(StringUtils.isEmpty(path)){
            path = "/default";
        }
        assert originalName != null;
        byte[] headBytes = null;
        if (!saveAsOriginalName && originalName.lastIndexOf(".") == -1) {
            try (InputStream in = multipartFile.getInputStream()) {
                headBytes = readHead(in, 28);
            } catch (IOException ignored) {
            }
        }
        String fileName = buildFileName(originalName, saveAsOriginalName, headBytes);
        // 保存的位置
        File file = new File(Utils.makePath(fileServerConfig.getWorkspace(), path, fileName));
        // 如果文件已经存在
        if (file.exists()) {
            return HttpResult.FAILED(String.format("file [%s] already exists!", Utils.makePath(path, fileName)));
        }
        // 检查父目录是否存在
        File dir = file.getParentFile();
        boolean bool = true;
        // 如果不存在则创建
        if (!dir.exists())
            bool = dir.mkdirs();
        if (bool) {
            try {
                multipartFile.transferTo(file);
                log.info("save file: [{}]", file.getAbsolutePath());
                return HttpResult.SUCCESS("上传成功", Utils.makePath("/file-server/files", path, fileName));
            } catch (IOException e) {
                log.error(e.toString(), e);
                e.printStackTrace();
            }
        }
        return HttpResult.FAILED("上传失败");
    }

    /**
     * 保存一个分片，写入到临时 .part 文件的指定偏移位置，返回已接收的总字节数
     */
    public long saveChunk(String fileId, long offset, MultipartFile chunk) throws IOException {
        validateFileId(fileId);
        Path tempDir = Paths.get(fileServerConfig.getWorkspace(), UPLOAD_TEMP_DIR);
        Files.createDirectories(tempDir);
        Path partFile = tempDir.resolve(fileId + ".part");
        try (RandomAccessFile raf = new RandomAccessFile(partFile.toFile(), "rw")) {
            raf.seek(offset);
            raf.write(chunk.getBytes());
            return raf.length();
        }
    }

    /**
     * 查询某个上传任务已接收的字节数（用于断点续传）
     */
    public long getUploadedSize(String fileId) {
        validateFileId(fileId);
        Path partFile = Paths.get(fileServerConfig.getWorkspace(), UPLOAD_TEMP_DIR, fileId + ".part");
        return Files.exists(partFile) ? partFile.toFile().length() : 0L;
    }

    /**
     * 完成上传：校验临时文件完整性后移动到最终位置
     */
    public HttpResult<String> completeUpload(String fileId, String fileName, String path,
                                             boolean saveAsOriginalName, long totalSize) {
        validateFileId(fileId);
        Path partFile = Paths.get(fileServerConfig.getWorkspace(), UPLOAD_TEMP_DIR, fileId + ".part");
        try {
            // 空文件特殊处理：没有分片上传过，直接创建空临时文件
            if (totalSize == 0 && !Files.exists(partFile)) {
                Files.createDirectories(partFile.getParent());
                Files.createFile(partFile);
            }
            if (!Files.exists(partFile) || partFile.toFile().length() != totalSize) {
                return HttpResult.FAILED("文件不完整，请重新上传");
            }
            if (StringUtils.isEmpty(path)) {
                path = "/default";
            }
            byte[] headBytes = null;
            if (!saveAsOriginalName && fileName.lastIndexOf(".") == -1) {
                try (InputStream in = Files.newInputStream(partFile)) {
                    headBytes = readHead(in, 28);
                } catch (IOException ignored) {
                }
            }
            String finalName = buildFileName(fileName, saveAsOriginalName, headBytes);
            File target = new File(Utils.makePath(fileServerConfig.getWorkspace(), path, finalName));
            if (target.exists()) {
                return HttpResult.FAILED(String.format("file [%s] already exists!", Utils.makePath(path, finalName)));
            }
            File dir = target.getParentFile();
            if (!dir.exists() && !dir.mkdirs()) {
                return HttpResult.FAILED("创建目录失败");
            }
            Files.move(partFile, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("save file: [{}]", target.getAbsolutePath());
            return HttpResult.SUCCESS("上传成功", Utils.makePath("/file-server/files", path, finalName));
        } catch (IOException e) {
            log.error(e.toString(), e);
            return HttpResult.FAILED("上传失败");
        }
    }

    /**
     * 取消上传，清理临时分片文件
     */
    public void abortUpload(String fileId) {
        validateFileId(fileId);
        Path partFile = Paths.get(fileServerConfig.getWorkspace(), UPLOAD_TEMP_DIR, fileId + ".part");
        try {
            Files.deleteIfExists(partFile);
            log.info("abort upload, clean temp file: [{}]", partFile);
        } catch (IOException e) {
            log.warn("clean temp file failed: {}", partFile);
        }
    }

    /**
     * 校验 fileId，防止路径穿越
     */
    private static void validateFileId(String fileId) {
        if (fileId == null || !fileId.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("非法的 fileId: " + fileId);
        }
    }

    /**
     * 根据原始文件名生成最终保存的文件名
     */
    private static String buildFileName(String originalName, boolean saveAsOriginalName, byte[] headBytes) {
        if (saveAsOriginalName) {
            return originalName;
        }
        String fileType;
        if (originalName.lastIndexOf(".") == -1) {
            fileType = ".undefined";
            if (headBytes != null && headBytes.length > 0) {
                String head = bytesToHexString(headBytes);
                for (FileType type : FileType.values()) {
                    if (head.startsWith(type.getValue())) {
                        fileType = type.name();
                        break;
                    }
                }
            }
        } else {
            fileType = originalName.substring(originalName.lastIndexOf("."));
        }
        return new SimpleDateFormat("yyyyMMddHHmmss_").format(new Date())
                + UUID.randomUUID().toString().replaceAll("-", "") + fileType;
    }

    /**
     * 从输入流读取最多 len 个字节，返回实际长度的数组
     */
    private static byte[] readHead(InputStream in, int len) throws IOException {
        byte[] buf = new byte[len];
        int total = 0;
        while (total < len) {
            int n = in.read(buf, total, len - total);
            if (n == -1) {
                break;
            }
            total += n;
        }
        if (total == len) {
            return buf;
        }
        byte[] out = new byte[total];
        System.arraycopy(buf, 0, out, 0, total);
        return out;
    }

    private static String bytesToHexString(byte[] src) {
        assert src != null;
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }
}
