package com.wordchain.controller;

import com.wordchain.datahandler.GameDatas;
import com.wordchain.datahandler.PlayerDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class OnlineEntitiesController {

    @Autowired
    GameDatas gameDatas;

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

        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String createNewMatch(Model model,
                               HttpServletRequest httpServletRequest,
                               @RequestParam Map<String,String> allRequestParams){

        Long playerId = (Long) httpServletRequest.getSession().getAttribute("player_id");
        String playerName = (String) httpServletRequest.getSession().getAttribute("player_name");

        Integer maxRound = Integer.parseInt(allRequestParams.get("round"));
        gameDatas.createNewMatch(playerId, maxRound);

        model.addAttribute("loggedIn", playerId != null);
        model.addAttribute("playername", playerName);
        model.addAttribute("onlinePlayers", playerDatas.getOnlinePlayersSortedByUsername());

        return "index";
    }

}