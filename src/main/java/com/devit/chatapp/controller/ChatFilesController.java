package com.devit.chatapp.controller;

import com.devit.chatapp.service.FileService;
import com.devit.chatapp.util.ResponseAPI;
import com.devit.chatapp.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class ChatFilesController {

    private final FileService fileService;

    @PostMapping
    public ResponseAPI<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String filePath = fileService.uploadFile(file);
        return ResponseBuilder.success("File uploaded successfully", filePath);
    }
}
