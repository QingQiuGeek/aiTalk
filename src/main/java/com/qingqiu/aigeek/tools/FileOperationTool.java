package com.qingqiu.aigeek.tools;

import static com.qingqiu.aigeek.constant.Common.FILE_SAVE_DIR;

import cn.hutool.core.io.FileUtil;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.context.annotation.Configuration;

/**
 * 文件操作工具类（提供文件读写功能）
 */
@Configuration
public class FileOperationTool {

    private final String FILE_DIR = FILE_SAVE_DIR + "/file";

    @Tool(name = "ReadFileTool" , value = "Read content from a file")
    public String readFile(@P(value = "Name of a file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool(name = "WriteFileTool",value = "Write content to a file")
    public String writeFile(@P(value = "Name of the file to write") String fileName,
                            @P(value = "Content to write to the file") String content
    ) {
        String filePath = FILE_DIR + "/" + fileName;

        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }
}
