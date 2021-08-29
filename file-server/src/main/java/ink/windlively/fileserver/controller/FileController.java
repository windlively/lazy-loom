package ink.windlively.fileserver.controller;

import ink.windlively.fileserver.model.FileModel;
import ink.windlively.fileserver.model.FileServerConfig;
import ink.windlively.fileserver.model.HttpResult;
import ink.windlively.fileserver.service.FileService;
import ink.windlively.fileserver.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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

    private final static String REQ_PREFIX = "/file-server/files";

    public FileController(FileService fileService,
                          FileServerConfig fileServerConfig) {
        this.fileService = fileService;
        this.fileServerConfig = fileServerConfig;
    }

    private final FileServerConfig fileServerConfig;

    @PostMapping("/one-file")
    public HttpResult<String> fileUpload(@RequestParam(name = "file") MultipartFile multipartFile,
                                         @RequestParam String path,
                                         @RequestParam(required = false, defaultValue = "true") boolean saveAsOriginalName) {
        return fileService.saveOneFile(multipartFile, path, saveAsOriginalName);
    }

    @RequestMapping("/explorer/**")
    public Object index(HttpServletRequest request) {
        return fileExplorer("explorer", request);
    }

    @RequestMapping(value = "/files/**")
    public Object files(HttpServletRequest request) {
        return fileExplorer("files", request);
    }

    private Object fileExplorer(String repPathPrefix, HttpServletRequest request){
        String reqFilePath = request.getServletPath().substring(("/" + repPathPrefix).length());
        if (reqFilePath.equals(""))
            reqFilePath = "/";
        String filePath = Utils.makePath(fileServerConfig.getWorkspace(), reqFilePath);
        File file = new File(filePath);
        ModelAndView mv = new ModelAndView();
        if (!file.exists()) {
            mv.setViewName("error");
            mv.addObject("message", "file or dir: " + reqFilePath + " not exist!");
            return mv;
        }
        if (file.isDirectory()) {
            // 父目录
            String parentDir = reqFilePath.substring(0, reqFilePath.lastIndexOf('/'));
            List<FileModel> childs = new ArrayList<>();
            mv.setViewName("index");
            mv.addObject("childs", childs);
            mv.addObject("index", reqFilePath);
            childs.add(new FileModel(REQ_PREFIX + "/", "根目录", true, null, 0));
            childs.add(new FileModel(Utils.makePath(REQ_PREFIX, parentDir), "上一级", true, null, 0));
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return mv;
            }
            for (File f : files) {
                FileModel fm = new FileModel(Utils.makePath(REQ_PREFIX, reqFilePath, f.getName()),
                        f.getName(),
                        f.isDirectory(),
                        new Date(f.lastModified()),
                        f.length());
                childs.add(fm);
            }
        } else {
//            URLEncoder.encode("/download" + reqPath, StandardCharsets.UTF_8);
            mv.setViewName("redirect:/download" + new String(reqFilePath.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
//            try (
//                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
//            ){
//                HttpHeaders headers = new HttpHeaders();
//                String fileName = new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
////                headers.setContentDispositionFormData("attachment", fileName);
////                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//                headers.setContentType(MediaType.ALL);
//                return new ResponseEntity<>(inputStream.readAllBytes(),
//                        headers, HttpStatus.CREATED);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
        return mv;
    }

    @RequestMapping("/delete/**")
    public Object delete(HttpServletRequest request) throws IOException {
        ModelAndView mv = new ModelAndView();
        String reqPath = request.getServletPath().substring("/delete".length());
        if (reqPath.equals(""))
            reqPath = "/";
        if (reqPath.equals("/")) {
            mv.addObject("error", "root dir could not delete!");
            return mv;
        }
        String filePath = Utils.makePath(fileServerConfig.getWorkspace(), reqPath);
        File file = new File(filePath);
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
        if (file.delete()) {
            log.info("delete file: {}", filePath);
            mv.addObject("message", "delete file: " + reqPath + " success");
        } else {
            mv.addObject("error", "file: " + reqPath + " delete failed!");
        }
        String parentDir = reqPath.substring(0, reqPath.lastIndexOf('/'));
        mv.setViewName("redirect:" + new String(Utils.makePath("/files", parentDir).getBytes(StandardCharsets.UTF_8)
                , StandardCharsets.ISO_8859_1));
        return mv;
    }

    @RequestMapping("/upload-page")
    public ModelAndView upload() {
        return new ModelAndView("upload");
    }
}
