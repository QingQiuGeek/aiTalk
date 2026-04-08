package com.qingqiu.openchat.tools;


import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.stereotype.Component;

/**
 * 终端操作工具
 */
@Component
public class TerminalOperationTool implements ITool {

    @Override
    public String getDescription() {
        return "Execute terminal commands on the server";
    }

    @Override
    public String getName() {
        return "TerminalOperationTool";
    }

    @Override
    public ToolType getType() {
        return ToolType.OPTIONAL;
    }

    @Tool(name = "TerminalOperationTool",value = "Execute a command in the terminal")
    public String executeTerminalCommand(@V(value = "Command to execute in the terminal") String command) {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
//            Process process = Runtime.getRuntime().exec(command);
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}
