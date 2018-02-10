package com.wordchain.controller;

import com.wordchain.datahandler.PlayerDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class OnlineEntitiesController {

    @Autowired
    private PlayerDatas playerDatas;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listingOnlineEntities(Model model,
                                        HttpServletRequest httpServletRequest) {

        Long playerId = (Long) httpServletRequest.getSession().getAttribute("player_id");
        String playerName = (String) httpServletRequest.getSession().getAttribute("player_name");

        model.addAttribute("loggedIn", playerId != null);
        model.addAttribute("playername", playerName);
        model.addAttribute("onlinePlayers", playerDatas.getOnlinePlayersSortedByUsername());

        if (playerId != null){
            // TODO: add my match and other's match data for model
        } else {
            // TODO: add every new match data for model
        }

        return "index";
    }

}