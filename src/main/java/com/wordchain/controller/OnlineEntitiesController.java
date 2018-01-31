package com.wordchain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class OnlineEntitiesController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listingOnlineEntities(Model model) {
        // model.addAttribute("players", new Player());
        // model.addAttribute("errors", new ArrayList<>());
        return "index";
    }



}
