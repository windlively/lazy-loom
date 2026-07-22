package ink.andromeda.leisure.scripts;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.PageReadListener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Collections.singletonList;

/**
 * @author junhan.bai
 * @date 2026/3/30
 * @note
 */
@Slf4j
public class SplitExcel {

    /**
     * 拆分 Excel 文件
     *
     * @param sourceFilePath 源文件路径
     * @param outputDirPath  输出目录路径 (例如: "output/")
     * @param fileCount      要拆分的子文件数量
     */
    public static void splitExcelFile(String sourceFilePath, String outputDirPath, int fileCount) {

        Map<Integer, String> heads = new HashMap<>();

        Path file = Paths.get(sourceFilePath).getFileName();
        String fileName = file.toString().substring(0, file.toString().lastIndexOf("."));
        Path outputDir = Paths.get(Paths.get(outputDirPath, fileName) + "/");
        if (Files.notExists(outputDir)) {
            try {
                Files.createDirectory(outputDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        List<Map<Integer, Object>> objects = EasyExcel.read(sourceFilePath).headRowNumber(1).sheet().registerReadListener(new PageReadListener<Map<String, Object>>(data -> {

        }){
            @Override
            public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                headMap.forEach((k, v) -> {
                    heads.put(k, v.getStringValue());
                });
            }
        }).doReadSync();

        int size = objects.size();
        System.out.println( size);
        if (size < fileCount) {
            fileCount = size;
        }

        int eachCount = (size % fileCount == 0) ? (size / fileCount) : (size / fileCount + 1);

        List<List<Map<Integer, Object>>> lists = new ArrayList<>();

        int i = 0;
        do {
            List<Map<Integer, Object>> list = objects.subList(i, Math.min((i + eachCount), size));
            lists.add(list);
            i += eachCount;
        } while (i < size);


        List<List<String>> head = heads.keySet().stream().sorted().map(e -> singletonList(heads.get(e))).toList();

        System.out.println(lists.size());
        for (int j = 0; j < lists.size(); j++) {
            System.out.println("file " + j + " size: " + lists.get(j).size());

            EasyExcel.write(outputDir + "/" + fileName + "_" + j + ".xlsx")
                    .sheet()
                     .head(head)
                    .doWrite(lists.get(j));

        }


    }

    public static void main(String[] args) {
        splitExcelFile("/Users/windlively/Documents/迁移数据修复/19年之前未迁移数据.xlsx",
                "/Users/windlively/Documents/迁移数据修复/拆分/", 20);
    }

}
