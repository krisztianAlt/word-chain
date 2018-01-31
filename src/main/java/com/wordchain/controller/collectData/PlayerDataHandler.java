package com.wordchain.controller.collectData;

import com.wordchain.DAO.QueryHandler;
import com.wordchain.model.Player;
import com.wordchain.model.UserLegitimacy;
import com.wordchain.service.Password;
import com.wordchain.service.PlayerDataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class PlayerDataHandler {

    @Autowired
    private QueryHandler queryHandler;

    @Autowired
    private PlayerDataValidator playerDataValidator;

    @Autowired
    private Password password;

    public Model collectPlayerRegistrationData(Player player, String confirm, Model model) {

        List<String> errorMessages;
        boolean savingSucceeded = false;
        boolean savingTried = false;

        errorMessages = playerDataValidator.validateRegistrationDatas(player, confirm);

        if (errorMessages.size() == 0){
            savingTried = true;
            savingSucceeded = savePlayerDatas(player);
        }

        model.addAttribute("player", player);
        model.addAttribute("errors", errorMessages);
        model.addAttribute("savingsucceeded", savingSucceeded);
        model.addAttribute("savingtried", savingTried);

        return model;
    }

    private boolean savePlayerDatas(Player player) {
        boolean savingSucceeded = false;

        String passwordStr = player.getPassword();
        String hashPassword = password.hashPassword(passwordStr);
        player.setPassword(hashPassword);
        player.setLegitimacy(UserLegitimacy.USER);

        try {
            queryHandler.saveNewPlayer(player);
            savingSucceeded = true;
        } catch (Exception e){
            System.out.println("SAVING FAILED: " + e.getMessage());
        }

        return savingSucceeded;
    }

}