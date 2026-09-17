package com.example.lianaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class FileOperationToolTest {

    @Test
    public void testReadFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "text.txt";
        String result = fileOperationTool.readFile(fileName);
        Assertions.assertNotNull(result);
    }

    @Test
    public void testWriteFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "text.txt";
        String content = "Hello, World!";
        String result = fileOperationTool.writeFile(fileName, content);
        Assertions.assertNotNull(result);
    }
}