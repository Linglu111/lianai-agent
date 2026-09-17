package com.example.lianaiagent.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 终端操作工具
 */
public class TerminalOperationTool {

    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        StringBuilder output = new StringBuilder();
        try {
            String powershellCommand =
                    "[Console]::OutputEncoding = [System.Text.Encoding]::UTF8; "
                    + "$OutputEncoding = [System.Text.Encoding]::UTF8; "
                    + command;

            ProcessBuilder builder = new ProcessBuilder(
                    "powershell.exe",
                    "-NoLogo",
                    "-NoProfile",
                    "-NonInteractive",
                    "-Command",
                    powershellCommand
            );
            builder.redirectErrorStream(true);

            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            output.append("Command execution interrupted: ").append(e.getMessage());
        } catch (IOException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}
