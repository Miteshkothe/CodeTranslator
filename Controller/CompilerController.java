package com.MK.Code_Translator.Controller;

import com.MK.Code_Translator.Entity.Code;
import com.MK.Code_Translator.Service.DockerCompiler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compile/execute")
@CrossOrigin(origins = "*") // Allow frontend access
public class CompilerController {

    private final DockerCompiler dockerCompiler = new DockerCompiler();

    @PostMapping
    public String executeCode(@RequestBody Code request) {
        try {
            return dockerCompiler.executeCode(request.getCode(), request.getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }
}
