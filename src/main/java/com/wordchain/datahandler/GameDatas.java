package com.wordchain.datahandler;

import com.wordchain.model.Game;
import com.wordchain.model.GameStatus;
import com.wordchain.model.Player;
import com.wordchain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class GameDatas {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerDatas playerDatas;

    public Game createNewMatch(Long playerId) {
        Player player = playerDatas.getPlayerById(playerId);
        Date actualDate = new Date();
        Game newGame = new Game(player, actualDate);
        gameRepository.save(newGame);
        Game.onlineGames.add(newGame.getId());
        Map<Long, Boolean> enteredPlayer = new HashMap<>();
        enteredPlayer.put(playerId, false);
        Game.playerEnteredIntoGameWindow.put(newGame.getId(), enteredPlayer);
        return newGame;
    }

    public Game getGameById(Long gameId){
        return gameRepository.getGameById(gameId);
    }

    public String joinPlayerIntoGame(Long playerId, Long gameId) {
        Player player = playerDatas.getPlayerById(playerId);
        Game game = getGameById(gameId);
        String status = game.addNewPlayerToGame(player);
        if (status.equals("OK")){
            player.joinToNewGame(game);
            // game.refreshGameInOnlineGames(game);
            gameRepository.addPlayerToGame(gameId, playerId);
            Game.playerEnteredIntoGameWindow.get(game.getId()).put(playerId, false);
            status = "Joined.";
        }

        return status;

        /*List<Player> newPlayerList = game.getPlayers();
        newPlayerList.add(player);
        gameRepository.addPlayerToGame(newPlayerList, gameId);
        return "Joined.";*/
    }

    public String leaveGame(Long playerId, Long gameId) {
        Player player = playerDatas.getPlayerById(playerId);
        Game game = getGameById(gameId);
        game.removePlayerFromGame(player);
        player.leaveGame(game);
        // game.refreshGameInOnlineGames(game);
        gameRepository.deletePlayerFromGame(gameId, playerId);
        Game.playerEnteredIntoGameWindow.get(gameId).remove(playerId);
        return "Game is left.";
    }

    public String deleteGame(Long deleteGameId) {

        // delete game from online games static list
        /*Iterator<Game> onlineGamesIterator = Game.onlineGames.iterator();

        while (onlineGamesIterator.hasNext()){
            Game game = onlineGamesIterator.next();

            if(game.getId() == deleteGameId){
                onlineGamesIterator.remove();
            }
        }*/
        Game.onlineGames.remove(deleteGameId);

        // delete game from entered players map:
        Game.playerEnteredIntoGameWindow.remove(deleteGameId);

        // delete game from database (from game and game_players tables)
        gameRepository.deleteGameById(deleteGameId);

        return "Deleted.";
    }

    public void playerEnteredIntoGameWindow(Long playerId, Long gameId){
        /*Game game = getGameById(gameId);
        game.addPlayerToPlayerEnteredIntoGameWindowMap(playerId, true);*/
        Game.playerEnteredIntoGameWindow.get(gameId).put(playerId, true);
    }

    public Long getCreatorIdByGameId(Long gameId) {
        Game game = getGameById(gameId);
        return game.getCreator().getId();
    }

    public void setGameStatusToPreparation(Long gameId){
        Game game = getGameById(gameId);
        game.setStatus(GameStatus.PREPARATION);
        gameRepository.changeGameStatus(game.getStatus().toString(), gameId);
        // game.refreshGameInOnlineGames(game);


    }

}
