package com.example.lianaiagent.tools;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class ResourceDownloadToolTest {

    @Test
    public void testDownloadResource() throws Exception {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String url = "https://i1.hdslb.com/bfs/archive/87a5d03581e326f4f818cab3212ce471d1f6a064.png";
        String fileName = "logo.png";
        String result = tool.downloadResource(url, fileName);
        assertNotNull(result);
    }
}
