package com.MK.Code_Translator.Controller;

import com.MK.Code_Translator.Entity.User;
import com.MK.Code_Translator.Service.UserEntryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
public class UserEntry {
    @Autowired
    private UserEntryService userEntryService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody User user){
        userEntryService.saveuser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            log.error("Wrong password",e);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
