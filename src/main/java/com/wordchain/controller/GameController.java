package com.wordchain.controller;

import com.wordchain.datahandler.GameDatas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GameController {

    @Autowired
    GameDatas gameDatas;

    @RequestMapping(value = "/game", method = RequestMethod.POST)
    public String startGame(@RequestParam Map<String,String> allRequestParams,
                            Model model,
                            HttpServletRequest httpServletRequest){

        Long playerId = (Long) httpServletRequest.getSession().getAttribute("player_id");
        String playerName = (String) httpServletRequest.getSession().getAttribute("player_name");
        Long gameId = Long.parseLong(allRequestParams.get("gameid"));

        Long creatorID = gameDatas.getCreatorIdByGameId(gameId);
        if (creatorID == playerId){
            gameDatas.setGameStatusToPreparation(gameId);
        }
        gameDatas.playerEnteredIntoGameWindow(playerId, gameId);

        model.addAttribute("loggedIn", playerId != null);
        model.addAttribute("playername", playerName);

        return "game";
    }

}