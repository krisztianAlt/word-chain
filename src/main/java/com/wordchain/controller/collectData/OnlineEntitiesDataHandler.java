package com.wordchain.controller.collectData;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class OnlineEntitiesDataHandler {

    public void collectOnlineEntitiesData(@RequestParam Map<String,String> allRequestParams,
                                  Model model,
                                  HttpServletRequest httpServletRequest){
        Long playerId = (Long) httpServletRequest.getSession().getAttribute("player_id");
        String playerName = (String) httpServletRequest.getSession().getAttribute("player_name");

        model.addAttribute("loggedIn", playerId != null);
        model.addAttribute("playername", playerName);

    }

}
