package com.wordchain.service;

import com.wordchain.DAO.QueryHandler;
import com.wordchain.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PlayerDataValidator {

    @Autowired
    private QueryHandler queryHandler;

    @Autowired
    private Password password;

    public List<String> validateRegistrationDatas(Player player, String confirm) {
        List<String> errorMessages = new ArrayList();

        if (userNameLenghtIsBad(player.getUserName())){
            errorMessages.add("Username must be a minimum of 5 characters and a maximum 32 characters.");
        }

        if (userNameContainsInvalidCharacters(player.getUserName())){
            errorMessages.add("Only letters, numbers, '-', '.', and '_' may be used in the username.");
        }

        if (emailFormatIsBad(player.getEmail())){
            errorMessages.add("Type email in this format: john.doe@fantasymail.com");
        }

        if (emailExists(player.getEmail())){
            errorMessages.add("This email is already exists in our database. Give another one.");
        }

        if (passwordLenghtIsBad(player.getPassword())){
            errorMessages.add("Password must be a minimum of 5 characters and a maximum 55 characters.");
        }

        if (!player.getPassword().equals(confirm)){
            errorMessages.add("Password confirmation failed. Type the same password in Password and Confirm fields.");
        }

        return errorMessages;
    }


    private boolean userNameLenghtIsBad(String userName) {
        if (userName.length() < 5 || userName.length() > 32){
            return true;
        }
        return false;
    }

    private boolean userNameContainsInvalidCharacters(String userName) {
        String numbers = "1234567890";
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String validSigns = "-._";
        for (int index = 0; index < userName.length(); index++){
            String character = Character.toString(userName.charAt(index));
            if (!numbers.contains(character) &&
                    !letters.contains(character) &&
                    !validSigns.contains(character)){
                return true;
            }
        }
        return false;
    }

    private boolean emailFormatIsBad(String email) {
        Pattern compiledPattern = Pattern.compile(
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = compiledPattern.matcher(email);
        return !matcher.matches();
    }

    private boolean emailExists(String email) {
        boolean emailExists = false;
        Player player = queryHandler.getPlayerByEmail(email);
        if (player != null){
            emailExists = true;
        }
        return emailExists;
    }

    private boolean passwordLenghtIsBad(String password) {
        if (password.length() < 5 || password.length() > 55){
            return true;
        }
        return false;
    }

    public Map<String, Object> validateLoginDatas(Map<String, String> playerDatas) {
        String email = playerDatas.get("email");
        String passwordStr = playerDatas.get("password");

        List<String> errorMessages = new ArrayList();

        Player playerFromDB = queryHandler.getPlayerByEmail(email);

        if (playerFromDB == null) {
            errorMessages.add("Invalid email or password.");
        } else {
            if (!password.checkPassword(passwordStr,playerFromDB.getPassword())) {
                errorMessages.add("Invalid email or password.");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("errors", errorMessages);
        result.put("player", playerFromDB);
        return result;
    }

}
