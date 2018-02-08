package com.wordchain.DAO;

import com.wordchain.model.Game;
import com.wordchain.model.GameStatus;
import com.wordchain.model.Player;
import com.wordchain.repository.GameRepository;
import com.wordchain.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class QueryHandler {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GameRepository gameRepository;

    public void saveNewPlayer(Player player) {
        playerRepository.save(player);
    }

    public Player getPlayerByEmail(String email){
        return playerRepository.getPlayerByEmail(email);
    }

    public Player getPlayerById(Long playerId){
        return playerRepository.getPlayerById(playerId);
    }

    public Game createNewMatch(Long playerId) {
        Player player = getPlayerById(playerId);
        Date actualDate = new Date();
        Game newGame = new Game(player, actualDate);
        gameRepository.save(newGame);
        return newGame;
    }

    public Game getGameById(Long gameId){
        return gameRepository.getGameById(gameId);
    }

    public String joinPlayerIntoGame(Long playerId, Long gameId) {
        Player player = getPlayerById(playerId);
        Game game = getGameById(gameId);
        String status = game.addNewPlayerToGame(player);
        if (status.equals("OK")){
            player.joinToNewGame(game);
            game.refreshGameInOnlineGames(game);
            gameRepository.addPlayerToGame(gameId, playerId);
            status = "Joined.";
        }

        return status;

        /*List<Player> newPlayerList = game.getPlayers();
        newPlayerList.add(player);
        gameRepository.addPlayerToGame(newPlayerList, gameId);
        return "Joined.";*/
    }

    public String leaveGame(Long playerId, Long gameId) {
        Player player = getPlayerById(playerId);
        Game game = getGameById(gameId);
        game.removePlayerFromGame(player);
        player.leaveGame(game);
        game.refreshGameInOnlineGames(game);
        gameRepository.deletePlayerFromGame(gameId, playerId);
        return "Game is left.";
    }

    public String deleteGame(Long deleteGameId) {

        // delete game from online games static list
        Iterator<Game> onlineGamesIterator = Game.onlineGames.iterator();

        while (onlineGamesIterator.hasNext()){
            Game game = onlineGamesIterator.next();

            if(game.getId() == deleteGameId){
                onlineGamesIterator.remove();
            }
        }

        // delete game from database (from game and game_players tables)
        gameRepository.deleteGameById(deleteGameId);

        return "Deleted.";
    }
}
