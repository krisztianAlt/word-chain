package com.wordchain.controller.collectData;

import com.wordchain.model.Player;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
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
        model.addAttribute("onlinePlayers", getOnlinePlayersSortedByUsername());

    }

    private List<Player> getOnlinePlayersSortedByUsername(){
        Collections.sort(Player.onlinePlayers, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p1.getUserName().compareTo(p2.getUserName()); // Ascending
            }
        });
        return Player.onlinePlayers;
    }

}
