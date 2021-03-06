package com.wordchain.datahandler;

import com.wordchain.model.Game;
import com.wordchain.model.GameStatus;
import com.wordchain.model.Player;
import com.wordchain.repository.GameRepository;
import com.wordchain.repository.PlayerRepository;
import com.wordchain.service.WordCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameDatas {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerDatas playerDatas;

    @Autowired
    WordCheck wordCheck;

    public Game createNewMatch(Long playerId, Integer round) {
        Player player = playerDatas.getPlayerById(playerId);
        Date actualDate = new Date();
        Game newGame = new Game(player, actualDate, round);
        gameRepository.save(newGame);
        Game.onlineGames.add(newGame.getId());
        Map<Long, Boolean> enteredPlayer = new HashMap<>();
        enteredPlayer.put(playerId, false);
        Game.playerEnteredIntoGameWindow.put(newGame.getId(), enteredPlayer);
        return newGame;
    }

    public Game getGameById(Long gameId){

        return gameRepository.findById(gameId);

    }

    public String joinPlayerIntoGame(Long playerId, Long gameId) {
        Player player = playerDatas.getPlayerById(playerId);
        Game game = getGameById(gameId);
        String status = game.addNewPlayerToGame(player);
        if (status.equals("OK")){
            game.addNewPlayerToGame(player);
            gameRepository.save(game);
            Game.playerEnteredIntoGameWindow.get(game.getId()).put(playerId, false);
            status = "Joined.";
        }

        return status;
    }

    public String leaveGame(Long playerId, Long gameId) {
        Player player = playerDatas.getPlayerById(playerId);
        Game game = getGameById(gameId);
        game.removePlayerFromGame(player);
        gameRepository.save(game);
        Game.playerEnteredIntoGameWindow.get(gameId).remove(playerId);
        return "Game is left.";
    }

    public String deleteGame(Long gameId) {
        Game.onlineGames.remove(gameId);

        // delete game from entered players map:
        Game.playerEnteredIntoGameWindow.remove(gameId);

        // delete game from database (from game and game_players tables)
        Game game = gameRepository.findById(gameId);
        gameRepository.delete(game);
        return "Deleted.";
    }

    public void playerEnteredIntoGameWindow(Long playerId, Long gameId){
        Game.playerEnteredIntoGameWindow.get(gameId).put(playerId, true);
    }

    public Long getCreatorIdByGameId(Long gameId) {
        Game game = getGameById(gameId);
        return game.getCreator().getId();
    }

    public void setGameStatus(Long gameId, GameStatus gameStatus){
        Game game = getGameById(gameId);
        if (!game.getStatus().equals(GameStatus.CLOSED)){
            game.setStatus(gameStatus);
            gameRepository.save(game);
        }
    }

    public boolean everyPlayerEntered(Long gameId) {
        Game game = getGameById(gameId);
        boolean everybodyIsInGameWindow = true;

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
        Game game = getGameById(gameId);
        Map<String, Integer> playerRounds = new HashMap<>();
        playerRounds.put("maxRound", game.getMaxRound());
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

    public void initiateLettersMap(Long gameId) {
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
                Game.letters.put(gameId, playerRounds);
            } else {
                Game.letters.get(gameId).put(playerOrderById.get(index), 0);
            }
        }
    }

    public void initiateWordChain(Long gameId) {
        Game.wordChains.put(gameId, new ArrayList<>());
    }

    public int getTimeResult(Long gameId, Long playerId) {
        return Game.timeResults.get(gameId).get(playerId);
    }

    public int getLetters(Long gameId, Long playerId) {

        return Game.letters.get(gameId).get(playerId);

    }

    public String giveFirstWord(Long gameId) {
        List<String> words = new ArrayList<>();
        words.add("cat");
        words.add("table");
        words.add("dog");
        words.add("red");
        words.add("final");
        words.add("car");
        words.add("book");
        words.add("bean");
        words.add("frog");
        words.add("two");
        words.add("door");
        words.add("guitar");
        words.add("bread");
        words.add("reptile");
        words.add("ant");
        words.add("riot");
        words.add("cloud");
        words.add("dark");
        words.add("revolution");
        Collections.shuffle(words);
        String selectedWord = words.get(0);
        Game.wordChains.get(gameId).add(selectedWord);
        saveWordChain(gameId);
        return selectedWord;
    }

    public String checkWord(Long gameId, String word, Long playerId) {
        String status = wordCheck.wordIsValid(gameId, word);

        if (status.equals("Accepted.")){
            Game.wordChains.get(gameId).add(word);
            saveWordChain(gameId);
            setNextPlayer(gameId);

            if (isGameOver(gameId)){
                setGameStatus(gameId, GameStatus.CLOSED);
                saveWordChain(gameId);
            } else {
                setGameStatus(gameId, GameStatus.GAMEINPROGRESS_NEXT_PLAYER);
            }
        }

        return status;
    }

    private void saveWordChain(Long gameId) {
        List<String> words = Game.wordChains.get(gameId);
        String chain = "";

        for (String word : words){
            chain += word + ", ";
        }

        chain = chain.substring(0, chain.length()-2);
        Game game = gameRepository.findById(gameId);
        game.setWordChain(chain);
        gameRepository.save(game);
    }

    private boolean isGameOver(Long gameId) {
        return Game.rounds.get(gameId).get("maxRound") < Game.rounds.get(gameId).get("actualRound");
    }

    private void setNextPlayer(Long gameId) {
        Integer actualPlayerId = Game.rounds.get(gameId).get("actualPlayer");
        int sizeOfPlayerOderList = Game.playerOrder.get(gameId).size();
        int lastPlayerId = Game.playerOrder.get(gameId).get(sizeOfPlayerOderList-1).intValue();
        if (actualPlayerId == lastPlayerId){
            int actualRound = Game.rounds.get(gameId).get("actualRound");
            Game.rounds.get(gameId).put("actualRound", actualRound + 1);
            Game.rounds.get(gameId).put("actualPlayer", Game.playerOrder.get(gameId).get(0).intValue());
        } else {
            int index = Game.playerOrder.get(gameId).indexOf(actualPlayerId.longValue()) + 1;
            Game.rounds.get(gameId).put("actualPlayer", Game.playerOrder.get(gameId).get(index).intValue());
        }
    }

    public String giveLastWord(Long gameId) {
        int sizeOfWordList = Game.wordChains.get(gameId).size();
        return Game.wordChains.get(gameId).get(sizeOfWordList-1);
    }

    public int getActivePlayerId(Long gameId) {
        return Game.rounds.get(gameId).get("actualPlayer");
    }

    public String getActivePlayerName(Long gameId) {
        Long playerId = Game.rounds.get(gameId).get("actualPlayer").longValue();
        Player player = playerDatas.getPlayerById(playerId);
        return player.getUserName();
    }

    public void addSeconds(Long gameId, Long playerId, Integer secondData) {
        Integer previousSecondData = Game.timeResults.get(gameId).get(playerId);
        Game.timeResults.get(gameId).put(playerId, previousSecondData + secondData);
    }

    public int getActualRound(Long gameId) {
        return Game.rounds.get(gameId).get("actualRound");
    }

    public void addLetters(Long gameId, Long playerId, int newWordLength) {
        Integer previousLetterNumber = Game.letters.get(gameId).get(playerId);
        Game.letters.get(gameId).put(playerId, previousLetterNumber + newWordLength);
    }
}
