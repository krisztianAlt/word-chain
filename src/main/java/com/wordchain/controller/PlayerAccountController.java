package com.wordchain.controller;

import com.wordchain.controller.collectData.PlayerDataHandler;
import com.wordchain.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PlayerAccountController {

    @Autowired
    PlayerDataHandler playerDataHandler;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("player", new Player());
        model.addAttribute("errors", new ArrayList<>());
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute Player player,
                               @RequestParam("confirm") String confirm,
                               Model model) {

        model = playerDataHandler.collectPlayerRegistrationData(player, confirm, model);

        List<String> errorMessages = (List) model.asMap().get("errors");

        if (errorMessages.size() == 0 &&
                (boolean) model.asMap().get("savingtried")){
            if ((boolean) model.asMap().get("savingsucceeded")){
                return "redirect:/registration-succeeded";
            } else {
                errorMessages.add("Database problem. Please, try later.");
                model.addAttribute("errors", errorMessages);
            }
        }

        return "registration";
    }

    @RequestMapping(value = "/registration-succeeded", method = RequestMethod.GET)
    public String registrationSucceeded() {
        return "registration-succeeded";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model){
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam("word") String word,
                            Model model){
        return "login";
    }

}