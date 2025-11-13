package com.MK.Code_Translator.Controller;
import com.MK.Code_Translator.Entity.Code;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/translate")
//@CrossOrigin(origins = "*")
public class Translate {
    @PostMapping
    public Map<String,String> google_api(@RequestBody Code req){
            Client client = new Client();
            String code=req.getCode();
            String language= req.getLanguage();
            String transcode=req.getTranscode();
            String prompt=String.format("""
                    convert the given code %s of %s into %s nothing else no comment no info just code
                    """,code,language,transcode);
            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.5-flash",
                            prompt,
                            null);
            return Map.of("translate",response.text());
    }

}
