package ink.windlively.fileserver.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URL;

@Data
@ConfigurationProperties(prefix = "file-server")
public class FileServerConfig {

    /**
     * 工作空间，文件服务器保存文件的目录
     */
    private String workspace = "/data/file-server";

    /**
     * 主机访问地址
     */
    private URL host;

}
