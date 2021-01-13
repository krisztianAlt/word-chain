package com.wordchain.api;

import com.wordchain.datahandler.GameDatas;
import com.wordchain.datahandler.PlayerDatas;
import com.wordchain.model.Game;
import com.wordchain.model.GameStatus;
import com.wordchain.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.json.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class GameREST {

    @Autowired
    GameDatas gameDatas;

    @Autowired
    PlayerDatas playerDatas;

    private JsonBuilderFactory factory = Json.createBuilderFactory(null);

    @GetMapping("/api/get-game-data")
    public String collectGameData(HttpServletRequest httpServletRequest){
        Long playerId = (Long) httpServletRequest.getSession().getAttribute("player_id");
        Long gameId = (Long) httpServletRequest.getSession().getAttribute("game_id");

        Game game = gameDatas.getGameById(gameId);

        JsonArrayBuilder jsonPlayersArrayBuilder;
        JsonObjectBuilder gameDataBuilder = factory.createObjectBuilder();

        gameDataBuilder.add("ownId", playerId);
        gameDataBuilder.add("gameId", gameId);
        gameDataBuilder.add("gameStatus", game.getStatus().toString());

        if (game.getStatus().equals(GameStatus.PREPARATION) && !gameDatas.everyPlayerEntered(gameId)){
            gameDataBuilder.add("currentMessage", gameDatas.getMissingPlayersName(gameId));
        } else if (game.getStatus().equals(GameStatus.PREPARATION) && gameDatas.everyPlayerEntered(gameId)){
            gameDataBuilder.add("currentMessage", "Everybody is here. The game begins...");
            gameDatas.setGameStatus(gameId, GameStatus.STARTING1);
            gameDatas.initiatePlayerOrder(gameId);
            gameDatas.initiateRoundMap(gameId);
            gameDatas.initiateTimeResultsMap(gameId);
            gameDatas.initiateLettersMap(gameId);
            gameDatas.initiateWordChain(gameId);
        } else if (game.getStatus().equals(GameStatus.STARTING1)){
            jsonPlayersArrayBuilder = getPlayerArrayBuilder(gameId);
            gameDataBuilder.add("currentMessage", "First player will be: " + gameDatas.getFirstPlayerName(gameId) + ". You can follow players' order in left side.");
            gameDataBuilder.add("playerTable", jsonPlayersArrayBuilder);
            gameDataBuilder.add("actualRound", gameDatas.getActualRound(gameId));
            gameDataBuilder.add("activePlayer", gameDatas.getFirstPlayerId(gameId));
            gameDatas.setGameStatus(gameId, GameStatus.STARTING2);
        } else if (game.getStatus().equals(GameStatus.STARTING2)){
            gameDatas.setGameStatus(gameId, GameStatus.FIRST_STEP);
        } else if (game.getStatus().equals(GameStatus.FIRST_STEP)){
            jsonPlayersArrayBuilder = getPlayerArrayBuilder(gameId);
            gameDataBuilder.add("lastWord", gameDatas.giveFirstWord(gameId));
            gameDataBuilder.add("currentMessage", gameDatas.getFirstPlayerName(gameId) + " is thinking.");
            gameDataBuilder.add("playerTable", jsonPlayersArrayBuilder);
            gameDataBuilder.add("actualRound", gameDatas.getActualRound(gameId));
            gameDataBuilder.add("activePlayer", gameDatas.getFirstPlayerId(gameId));
            gameDatas.setGameStatus(gameId, GameStatus.GAMEINPROGRESS_WAITING_FOR_GOOD_WORD);
        } else if(game.getStatus().equals(GameStatus.GAMEINPROGRESS_WAITING_FOR_GOOD_WORD)){
            jsonPlayersArrayBuilder = getPlayerArrayBuilder(gameId);
            gameDataBuilder.add("lastWord", gameDatas.giveLastWord(gameId));
            gameDataBuilder.add("currentMessage", gameDatas.getActivePlayerName(gameId) + " is thinking.");
            gameDataBuilder.add("playerTable", jsonPlayersArrayBuilder);
            gameDataBuilder.add("actualRound", gameDatas.getActualRound(gameId));
            gameDataBuilder.add("activePlayer", gameDatas.getActivePlayerId(gameId));
        } else if (game.getStatus().equals(GameStatus.GAMEINPROGRESS_NEXT_PLAYER)){
            jsonPlayersArrayBuilder = getPlayerArrayBuilder(gameId);
            gameDataBuilder.add("lastWord", gameDatas.giveLastWord(gameId));
            gameDataBuilder.add("currentMessage", gameDatas.getActivePlayerName(gameId) + " is thinking.");
            gameDataBuilder.add("playerTable", jsonPlayersArrayBuilder);
            gameDataBuilder.add("actualRound", gameDatas.getActualRound(gameId));
            gameDataBuilder.add("activePlayer", gameDatas.getActivePlayerId(gameId));
            gameDatas.setGameStatus(gameId, GameStatus.GAMEINPROGRESS_WAITING_FOR_GOOD_WORD);
        } else if (game.getStatus().equals(GameStatus.CLOSED)){
            jsonPlayersArrayBuilder = getPlayerArrayBuilder(gameId);
            gameDataBuilder.add("lastWord", gameDatas.giveLastWord(gameId));
            gameDataBuilder.add("currentMessage", "GAME OVER. Please, push 'Main Page' in the menu.");
            gameDataBuilder.add("playerTable", jsonPlayersArrayBuilder);
            gameDataBuilder.add("activePlayer", -1);
            httpServletRequest.getSession().removeAttribute("game_id");
        }

        JsonObject gameData = gameDataBuilder.build();

        return gameData.toString();
    }

    @PostMapping("/api/add-word")
    public String getNewWord(HttpServletRequest httpServletRequest,
                             @RequestParam Map<String,String> allRequestParams){
        Long playerId = (Long) httpServletRequest.getSession().getAttribute("player_id");
        Long gameId = Long.parseLong(allRequestParams.get("gameId"));
        String word = allRequestParams.get("newWord");
        Integer secondData = Integer.parseInt(allRequestParams.get("secondData"));

        gameDatas.addSeconds(gameId, playerId, secondData);
        String status = gameDatas.checkWord(gameId, word, playerId);

        if (status.equals("Accepted.")){
            gameDatas.addLetters(gameId, playerId, word.length());
        }

        JsonObject answer = factory.createObjectBuilder().add("answer", status).build();
        return answer.toString();
    }

    private JsonArrayBuilder getPlayerArrayBuilder(Long gameId) {
        // TODO: player name, time
        List<Long> playersById = Game.playerOrder.get(gameId);

        JsonArrayBuilder jsonPlayerArrayBuilder = factory.createArrayBuilder();

        for (Long playerId : playersById){
            Player player = playerDatas.getPlayerById(playerId);
            JsonObjectBuilder playerBuilder = factory.createObjectBuilder();
            playerBuilder.add("playerId", player.getId());
            playerBuilder.add("userName", player.getUserName());
            playerBuilder.add("timeResult", gameDatas.getTimeResult(gameId, playerId));
            playerBuilder.add("letters", gameDatas.getLetters(gameId, playerId));
            jsonPlayerArrayBuilder.add(playerBuilder);
        }

        return jsonPlayerArrayBuilder;
    }

}
