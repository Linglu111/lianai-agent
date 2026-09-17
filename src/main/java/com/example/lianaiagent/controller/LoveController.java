package com.example.lianaiagent.controller;

import com.example.lianaiagent.app.LoveApp;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/love")
public class LoveController {

    private final LoveApp loveApp;

    public LoveController(LoveApp loveApp) {
        this.loveApp = loveApp;
    }

    @PostMapping(value = "/chat/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> chatWithImage(@RequestParam String message,
                                                @RequestParam String chatId,
                                                @RequestParam MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "图片不能为空");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(BAD_REQUEST, "仅支持图片文件");
        }

        return ResponseEntity.ok(
                loveApp.doChatWithImage(message, chatId, image.getBytes(), contentType)
        );
    }
}
