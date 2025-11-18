package com.MK.Code_Translator.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/upload")
@CrossOrigin("*")
public class FileUploading {

    @PostMapping
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Map.of("status", "error", "message", "File is empty");
            }

            String fileName = file.getOriginalFilename();
            String content = new String(file.getBytes());
            String extension="";
            int i=0;
            while(fileName.charAt(i)!='.')i++;
            i++;
            while(i<fileName.length()){
                extension+=fileName.charAt(i);
                i++;
            }
            return Map.of(
                    "status", "success",
                    "fileName", fileName,
                    "content", content,
                    "extension",extension
            );

        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
