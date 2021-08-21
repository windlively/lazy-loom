package ink.windlively.fileserver.service;

import ink.windlively.fileserver.model.FileType;
import ink.windlively.fileserver.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import ink.windlively.fileserver.model.HttpResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    @Value("${file-server.base-dir}")
    public String baseDir;

    public HttpResult<String> saveOneFile(MultipartFile multipartFile, String path, boolean saveAsOriginalName) {
        String originalName = multipartFile.getOriginalFilename();
        assert originalName != null;
        String fileName;
        if (saveAsOriginalName) {
            fileName = originalName;
        } else {
            String fileType = "";
            if (originalName.lastIndexOf(".") == -1) {
                fileType = ".undefined";
                try {
                    byte[] bytes = multipartFile.getBytes();
                    bytes = Arrays.copyOf(bytes, 28);
                    String head = bytesToHexString(bytes);
                    for (FileType type : FileType.values()) {
                        if (head.startsWith(type.getValue())) {
                            fileType = type.name();
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                fileType = originalName.substring(originalName.lastIndexOf("."));

            fileName = new SimpleDateFormat("yyyyMMdd_HH:mm:ss_")
                    .format(new Date()) + UUID.randomUUID().toString().replaceAll("-", "") + fileType;
        }
        // 保存的位置
        File file = new File(Utils.makePath(baseDir, path, fileName));
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
