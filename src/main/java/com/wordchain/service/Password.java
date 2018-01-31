package com.wordchain.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class Password {

    private  int saltInt = 10;

    public String hashPassword(String password) {
        String salt = BCrypt.gensalt(saltInt);
        String hashed_password = BCrypt.hashpw(password, salt);

        return hashed_password;
    }

    public boolean checkPassword(String password, String stored_hash) {
        boolean password_verified;

        if(null == stored_hash || !stored_hash.startsWith("$2a$")){
            return false;

        }

        password_verified = BCrypt.checkpw(password, stored_hash);


        return (password_verified);
    }

}
