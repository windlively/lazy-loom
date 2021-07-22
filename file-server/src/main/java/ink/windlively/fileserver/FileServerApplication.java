package ink.windlively.fileserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Slf4j
public class FileServerApplication implements WebMvcConfigurer {

    // 文件的本地磁盘存储路径
    @Value("${file-server.base-dir}")
    private String baseDir;

    private ApplicationContext applicationContext;

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
        //映射本地文件时，开头必须是 file:/// 开头，表示协议
        log.info("base dir: {}", baseDir);
        registry.addResourceHandler("/download/**")
                .addResourceLocations("file:///" + (baseDir.endsWith("/") ? baseDir : baseDir + "/"));
        // 静态资源(thymeleaf)文件
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/");
    }
}