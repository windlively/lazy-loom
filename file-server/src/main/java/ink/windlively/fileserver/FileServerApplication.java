package ink.windlively.fileserver;

import ink.windlively.fileserver.model.FileServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@Slf4j
public class FileServerApplication implements WebMvcConfigurer {

    private final FileServerConfig fileServerConfig;

    private ApplicationContext applicationContext;

    public FileServerApplication(FileServerConfig fileServerConfig) {
        this.fileServerConfig = fileServerConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

//    @Bean("multipartResolver")
//    CommonsMultipartResolver multipartResolver(){
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setDefaultEncoding("UTF-8");
//        return multipartResolver;
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String workspace = fileServerConfig.getWorkspace();
        //映射本地文件时，开头必须是 file:/// 开头，表示协议
        log.info("workspace is: {}", workspace);
        registry.addResourceHandler("/download/**")
                .addResourceLocations("file:///" + (workspace.endsWith("/") ? workspace : workspace + "/"));
        // 静态资源(thymeleaf)文件
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/");
    }
}
