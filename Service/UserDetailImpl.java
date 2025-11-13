package com.MK.Code_Translator.Service;

import com.MK.Code_Translator.Entity.User;
import com.MK.Code_Translator.Repo.UserEntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailImpl implements UserDetailsService {
    @Autowired
    private UserEntryRepo userEntryRepo;
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user=userEntryRepo.findByUserName(userName);
        if(user==null) {
            throw new UsernameNotFoundException(userName);
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole().toArray(new String[0]))
                .build();
    }
}
