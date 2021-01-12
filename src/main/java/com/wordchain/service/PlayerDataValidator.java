package com.wordchain.service;

import com.wordchain.datahandler.PlayerDatas;
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
    private PlayerDatas playerDatas;

    @Autowired
    private Password password;

    public List<String> validateRegistrationDatas(Player player, String confirm) {
        List<String> errorMessages = new ArrayList<String>();

        if (userNameLenghtIsBad(player.getUserName())){
            errorMessages.add("Username must be a minimum of 5 characters and a maximum 32 characters.");
        }

        if (userNameContainsInvalidCharacters(player.getUserName())){
            errorMessages.add("Only letters, numbers, '-', '.', and '_' may be used in the user name.");
        }

        if (userNameExists(player.getUserName())) {
        	errorMessages.add("This user name is already exists in our database. Please, give another one.");
        }
        
        /*if (emailFormatIsBad(player.getEmail())){
            errorMessages.add("Type email in this format: john.doe@fantasymail.com");
        }

        if (emailExists(player.getEmail())){
            errorMessages.add("This email is already exists in our database. Give another one.");
        }*/

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

    private boolean userNameExists(String userName) {
        boolean userNameExists = false;
        Player player = playerDatas.getPlayerByUserName(userName);
        if (player != null){
        	userNameExists = true;
        }
        return userNameExists;
    }
    
    /*private boolean emailFormatIsBad(String email) {
        Pattern compiledPattern = Pattern.compile(
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = compiledPattern.matcher(email);
        return !matcher.matches();
    }

    private boolean emailExists(String email) {
        boolean emailExists = false;
        Player player = playerDatas.getPlayerByEmail(email);
        if (player != null){
            emailExists = true;
        }
        return emailExists;
    }*/

    private boolean passwordLenghtIsBad(String password) {
        if (password.length() < 5 || password.length() > 55){
            return true;
        }
        return false;
    }

    public Map<String, Object> validateLoginDatas(Map<String, String> playerDataPackage) {
        String username = playerDataPackage.get("username");
        String passwordStr = playerDataPackage.get("password");

        List<String> errorMessages = new ArrayList<String>();

        Player playerFromDB = playerDatas.getPlayerByUserName(username);

        if (playerFromDB == null) {
            errorMessages.add("Invalid user name or password.");
        } else {
            if (!password.checkPassword(passwordStr,playerFromDB.getPassword())) {
                errorMessages.add("Invalid user name or password.");
            } else {
            	if (playerIsAlreadyLoggedIn(playerFromDB)) {
            		errorMessages.add("You have already logged in. Please, push 'Main Page' in the menu.");
            	}
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("errors", errorMessages);
        result.put("player", playerFromDB);
        return result;
    }
    
    private boolean playerIsAlreadyLoggedIn(Player playerFromDB) {
        if (Player.onlinePlayers.contains(playerFromDB.getId())){
            return true;
        }
        return false;
    }
    
}