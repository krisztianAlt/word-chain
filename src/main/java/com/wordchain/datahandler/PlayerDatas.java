package com.wordchain.datahandler;

import com.wordchain.model.Game;
import com.wordchain.model.GameStatus;
import com.wordchain.model.Player;
import com.wordchain.model.UserLegitimacy;
import com.wordchain.repository.PlayerRepository;
import com.wordchain.service.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Service
public class PlayerDatas {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    Password password;

    public void saveNewPlayer(Player player) {
        playerRepository.save(player);
    }

    public Player getPlayerByEmail(String email){
        return playerRepository.getPlayerByEmail(email);
    }

    public Player getPlayerById(Long playerId){
        return playerRepository.getPlayerById(playerId);
    }

    public boolean savePlayerDatas(Player player) {
        boolean savingSucceeded = false;

        String passwordStr = player.getPassword();
        String hashPassword = password.hashPassword(passwordStr);
        player.setPassword(hashPassword);
        player.setLegitimacy(UserLegitimacy.USER);

        try {
            saveNewPlayer(player);
            savingSucceeded = true;
        } catch (Exception e){
            System.out.println("SAVING FAILED: " + e.getMessage());
        }

        return savingSucceeded;
    }

    public List<Player> getOnlinePlayersSortedByUsername(){
        Collections.sort(Player.onlinePlayers, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p1.getUserName().compareTo(p2.getUserName()); // Ascending
            }
        });
        return Player.onlinePlayers;
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

    public boolean checkUserIsAdmin(long playerId) {

        Player player = getPlayerById(playerId);

        if (player.getLegitimacy()== UserLegitimacy.ADMIN.ADMIN) {
            return true;

        }
        return false;
    }

    public boolean checkUserIsLoggedUserOrAdmin(long playerId) {

        Player player = getPlayerById(playerId);

        if (player.getLegitimacy() == UserLegitimacy.ADMIN ||
                player.getLegitimacy()== UserLegitimacy.USER ) {
            return true;

        }
        return false;
    }

}