package com.wordchain.controller.collectData;

import com.wordchain.DAO.QueryHandler;
import com.wordchain.model.Game;
import com.wordchain.model.GameStatus;
import com.wordchain.model.Player;
import com.wordchain.model.UserLegitimacy;
import com.wordchain.service.Password;
import com.wordchain.service.PlayerDataValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Service
public class PlayerDataHandler {

    @Autowired
    private QueryHandler queryHandler;

    @Autowired
    private PlayerDataValidator playerDataValidator;

    @Autowired
    private Password password;

    public Model collectPlayerRegistrationData(Player player, String confirm, Model model) {

        List<String> errorMessages;
        boolean savingSucceeded = false;
        boolean savingTried = false;

        errorMessages = playerDataValidator.validateRegistrationDatas(player, confirm);

        if (errorMessages.size() == 0){
            savingTried = true;
            savingSucceeded = savePlayerDatas(player);
        }

        model.addAttribute("player", player);
        model.addAttribute("errors", errorMessages);
        model.addAttribute("savingsucceeded", savingSucceeded);
        model.addAttribute("savingtried", savingTried);

        return model;
    }

    private boolean savePlayerDatas(Player player) {
        boolean savingSucceeded = false;

        String passwordStr = player.getPassword();
        String hashPassword = password.hashPassword(passwordStr);
        player.setPassword(hashPassword);
        player.setLegitimacy(UserLegitimacy.USER);

        try {
            queryHandler.saveNewPlayer(player);
            savingSucceeded = true;
        } catch (Exception e){
            System.out.println("SAVING FAILED: " + e.getMessage());
        }

        return savingSucceeded;
    }

    public Model collectLoginData(@RequestParam Map<String,String> allRequestParams,
                                  Model model) {
        List<String> errorMessages = new ArrayList();
        Map<String, String> playerDatas = new HashMap<>();
        Player player = null;
        if (allRequestParams.size() > 0){
            playerDatas.put("email", allRequestParams.get("email"));
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

    public boolean checkUserIsAdmin(long playerId) {

        Player player = queryHandler.getPlayerById(playerId);

        if (player.getLegitimacy()== UserLegitimacy.ADMIN.ADMIN) {
            return true;

        }
        return false;
    }

    public boolean checkUserIsLoggedUserOrAdmin(long playerId) {

        Player player = queryHandler.getPlayerById(playerId);

        if (player.getLegitimacy() == UserLegitimacy.ADMIN ||
                player.getLegitimacy()== UserLegitimacy.USER ) {
            return true;

        }
        return false;
    }

    public void deletePlayerFromOnlinePlayersList(long logoutPlayerId) {
        Iterator<Player> onlinePlayers = Player.onlinePlayers.iterator();

        while (onlinePlayers.hasNext()) {
            Player player = onlinePlayers.next();

            if (player.getId() == logoutPlayerId)
                onlinePlayers.remove();
        }
    }

    public void deletePlayerFromOnlineGames(long logoutPlayerId) {
        Iterator<Game> onlineGames = Game.onlineGames.iterator();

        while (onlineGames.hasNext()){
            Game game = onlineGames.next();

            if (game.getCreator().getId() == logoutPlayerId &&
                    game.getStatus().equals(GameStatus.NEW)){
                onlineGames.remove();
            } else {
                Iterator<Player> playersIter = game.getPlayers().iterator();

                while (playersIter.hasNext()){
                    Player player = playersIter.next();
                    if (player.getId() == logoutPlayerId){
                        playersIter.remove();
                    }
                }
            }
        }
    }
}