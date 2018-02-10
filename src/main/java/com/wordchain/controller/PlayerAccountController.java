package com.wordchain.controller;

import com.wordchain.controller.collectData.PlayerDataForModel;
import com.wordchain.datahandler.PlayerDatas;
import com.wordchain.model.Player;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class PlayerAccountController {

    @Autowired
    private PlayerDataForModel playerDataForModel;

    @Autowired
    private PlayerDatas playerDatas;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(PlayerAccountController.class);

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("player", new Player());
        model.addAttribute("errors", new ArrayList<>());
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute Player player,
                               @RequestParam("confirm") String confirm,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        model = playerDataForModel.collectPlayerRegistrationData(player, confirm, model);

        List<String> errorMessages = (List) model.asMap().get("errors");

        if (errorMessages.size() == 0 &&
                (boolean) model.asMap().get("savingtried")){
            if ((boolean) model.asMap().get("savingsucceeded")){
                redirectAttributes.addFlashAttribute("newplayersaved", true);
                return "redirect:/";
            } else {
                errorMessages.add("Database problem. Please, try later.");
                model.addAttribute("errors", errorMessages);
            }
        }

        return "registration";
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public String renderLogin(@RequestParam Map<String,String> allRequestParams,
                              Model model,
                              HttpServletRequest httpServletRequest) {
        model = playerDataForModel.collectLoginData(allRequestParams, model);

        List<String> errorMessages = (List) model.asMap().get("errors");
        Player player = (Player) model.asMap().get("validplayer");

        if (errorMessages.size() == 0 && player != null){
            httpServletRequest.getSession().setAttribute("player_id", player.getId());
            httpServletRequest.getSession().setAttribute("player_name", player.getUserName());
            Player.onlinePlayers.add(player);
            return "redirect:/";
        }

        return "login";

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String renderLogout(HttpServletRequest httpServletRequest) {
        long logoutPlayerId = (long) httpServletRequest.getSession().getAttribute("player_id");

        playerDatas.deletePlayerFromOnlineGames(logoutPlayerId);
        playerDatas.deletePlayerFromOnlinePlayersList(logoutPlayerId);

        httpServletRequest.getSession().invalidate();
        logger.info("ONLINE PLAYERS: " + Player.onlinePlayers.toString());
        return "redirect:/";
    }

}