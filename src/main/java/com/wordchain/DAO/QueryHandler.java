package com.wordchain.DAO;

import com.wordchain.model.Player;
import com.wordchain.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryHandler {

    @Autowired
    PlayerRepository playerRepository;

    public void saveNewPlayer(Player player) {
        playerRepository.save(player);
    }

    public Player getPlayerByEmail(String email){
        return playerRepository.getPlayerByEmail(email);
    }

    public Player getPlayerById(Long playerId){
        return playerRepository.getPlayerById(playerId);
    }

}
