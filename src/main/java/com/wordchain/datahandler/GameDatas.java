package com.wordchain.datahandler;

import com.wordchain.model.Game;
import com.wordchain.model.GameStatus;
import com.wordchain.model.Player;
import com.wordchain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public void setGameStatus(Long gameId, GameStatus gameStatus){
        Game game = getGameById(gameId);
        game.setStatus(gameStatus);
        gameRepository.changeGameStatus(game.getStatus().toString(), gameId);
        // game.refreshGameInOnlineGames(game);

    }

    public boolean everyPlayerEntered(Long gameId) {
        Game game = getGameById(gameId);
        boolean everybodyIsInGameWindow = true;
        // System.out.println(Game.playerEnteredIntoGameWindow.values());

        for (Player player : game.getPlayers()){
            if (!Game.playerEnteredIntoGameWindow.get(gameId).get(player.getId())){
                everybodyIsInGameWindow = false;
                break;
            }
        }

        return everybodyIsInGameWindow;
    }

    public boolean playerJoinedToGame(Long playerId, Long gameId) {
        Game game = getGameById(gameId);
        for (Player player : game.getPlayers()){
            if (player.getId() == playerId){
                return true;
            }
        }
        return false;
    }

    public String getMissingPlayersName(Long gameId) {
        String missingPlayers = "";
        Game game = getGameById(gameId);

        for (Player player : game.getPlayers()){
            if (!Game.playerEnteredIntoGameWindow.get(gameId).get(player.getId())){
                missingPlayers += player.getUserName() + ", ";
            }
        }


        return "We are waiting for: " + missingPlayers.substring(0, missingPlayers.length()-2);
    }

    public void initiatePlayerOrder(Long gameId) {
        Game game = getGameById(gameId);

        List<Long> playerOrder = new ArrayList<>();

        for (Player player : game.getPlayers()){
            playerOrder.add(player.getId());
        }

        Collections.shuffle(playerOrder);

        Game.playerOrder.put(gameId, playerOrder);
    }

    public void initiateRoundMap(Long gameId) {
        Map<String, Integer> playerRounds = new HashMap<>();
        playerRounds.put("maxRound", Game.MAXROUND);
        playerRounds.put("actualRound", 1);
        playerRounds.put("actualPlayer", getFirstPlayerId(gameId).intValue());
        Game.rounds.put(gameId, playerRounds);
    }

    public String getFirstPlayerName(Long gameId) {
        Player firstPlayer = playerDatas.getPlayerById(Game.playerOrder.get(gameId).get(0));
        return firstPlayer.getUserName();
    }

    public Long getFirstPlayerId(Long gameId) {
        return Game.playerOrder.get(gameId).get(0);
    }


    public void initiateTimeResultsMap(Long gameId) {
        Game game = getGameById(gameId);
        List<Long> playerOrderById = new ArrayList<>();

        for (Player player : game.getPlayers()){
            playerOrderById.add(player.getId());
        }

        int lengthOfPlayerOrderList = playerOrderById.size();

        for (int index = 0; index < lengthOfPlayerOrderList; index++){
            if (index == 0){
                Map<Long, Integer> playerRounds = new HashMap<>();
                playerRounds.put(playerOrderById.get(index), 0);
                Game.timeResults.put(gameId, playerRounds);
            } else {
                Game.timeResults.get(gameId).put(playerOrderById.get(index), 0);
            }
        }
    }

    public int getTimeResult(Long gameId, Long playerId) {
        return Game.timeResults.get(gameId).get(playerId);
    }

    public String giveFirstWord() {
        List<String> words = new ArrayList<>();
        words.add("cat");
        words.add("table");
        words.add("dog");
        words.add("red");
        words.add("final");
        words.add("car");
        Collections.shuffle(words);
        return words.get(0);
    }
}
