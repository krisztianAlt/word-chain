package com.wordchain.controller.collectData;

import com.wordchain.datahandler.PlayerDatas;
import com.wordchain.model.Player;
import com.wordchain.service.PlayerDataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Service
public class PlayerDataForModel {

    @Autowired
    private PlayerDatas playerDatas;

    @Autowired
    private PlayerDataValidator playerDataValidator;

    public Model collectPlayerRegistrationData(Player player, String confirm, Model model) {

        List<String> errorMessages;
        boolean savingSucceeded = false;
        boolean savingTried = false;

        errorMessages = playerDataValidator.validateRegistrationDatas(player, confirm);

        if (errorMessages.size() == 0){
            savingTried = true;
            savingSucceeded = playerDatas.savePlayerDatas(player);
        }

        model.addAttribute("player", player);
        model.addAttribute("errors", errorMessages);
        model.addAttribute("savingsucceeded", savingSucceeded);
        model.addAttribute("savingtried", savingTried);

        return model;
    }

    public Model collectLoginData(@RequestParam Map<String,String> allRequestParams,
                                  Model model) {

        List<String> errorMessages = new ArrayList();
        Map<String, String> playerDatas = new HashMap<>();
        Player player = null;
        if (allRequestParams.size() > 0){
            // playerDatas.put("email", allRequestParams.get("email"));
        	playerDatas.put("username", allRequestParams.get("username"));
            playerDatas.put("password", allRequestParams.get("password"));

            Map<String, Object> errorMessagesAndPlayer = playerDataValidator.validateLoginDatas(playerDatas);
            errorMessages = (List<String>) errorMessagesAndPlayer.get("errors");
            player = (Player) errorMessagesAndPlayer.get("player");

        }

        model.addAttribute("player", playerDatas);
        model.addAttribute("errors", errorMessages);
        model.addAttribute("validplayer", player);

        return model;
    }

}