package com.MK.Code_Translator.Service;

import com.MK.Code_Translator.Entity.User;
import com.MK.Code_Translator.Repo.UserEntryRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class UserEntryService {
    @Autowired
    private UserEntryRepo userEntryRepo;
    private static final PasswordEncoder passwordEncode=new BCryptPasswordEncoder();
    public void saveuser(User user){
        try{
            user.setPassword(passwordEncode.encode(user.getPassword()));
            user.setRole(Arrays.asList("USER"));
            userEntryRepo.save(user);
        }catch (Exception e){
            log.error("same user name",e);
        }
    }
}
