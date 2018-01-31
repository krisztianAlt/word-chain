package com.wordchain.controller;

import com.wordchain.model.Player;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class PlayerAccountController {

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
        model.addAttribute("player","SOMETHING");
        model.addAttribute("errors", new ArrayList<>());
        return "registration";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String checkWord(@RequestParam("word") String word,
                            Model model){
        return "dictionary-test";
    }
}