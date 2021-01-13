package com.wordchain.datahandler;

import com.wordchain.model.Game;
import com.wordchain.model.Player;
import com.wordchain.model.UserLegitimacy;
import com.wordchain.repository.PlayerRepository;
import com.wordchain.service.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerDatas {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    Password password;

    public void saveNewPlayer(Player player) {
        playerRepository.save(player);
    }

    public Player getPlayerByUserName(String userName){

        return playerRepository.findByUserName(userName);

    }
    
    /*public Player getPlayerByEmail(String email){

        return playerRepository.findByEmail(email);

    }*/

    public Player getPlayerById(Long playerId){

        return playerRepository.findById(playerId);

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
        List<Player> onlinePlayers = new ArrayList<>();

        for (Long playerId : Player.onlinePlayers){
            onlinePlayers.add(getPlayerById(playerId));
        }

        Collections.sort(onlinePlayers, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p1.getUserName().compareTo(p2.getUserName()); // Ascending
            }
        });

        return onlinePlayers;
    }

    public void deletePlayerFromOnlinePlayersList(long logoutPlayerId) {
        Player.onlinePlayers.remove(logoutPlayerId);
    }
    
    public boolean checkUserIsAdmin(long playerId) {

        Player player = getPlayerById(playerId);

        if (player.getLegitimacy()== UserLegitimacy.ADMIN) {
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