package com.wordchain.api;

import com.wordchain.datahandler.GameDatas;
import com.wordchain.datahandler.PlayerDatas;
import com.wordchain.model.Game;
import com.wordchain.model.GameStatus;
import com.wordchain.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

        System.out.println("EVERYBODY: " + gameDatas.everyPlayerEntered(gameId));

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
        } else if (game.getStatus().equals(GameStatus.STARTING1)){
            jsonPlayersArrayBuilder = getPlayerArrayBuilder(gameId);
            gameDataBuilder.add("currentMessage", "First player will be: " + gameDatas.getFirstPlayerName(gameId) + ". You can follow players' order in left side.");
            gameDataBuilder.add("playerTable", jsonPlayersArrayBuilder);
            gameDataBuilder.add("activePlayer", gameDatas.getFirstPlayerId(gameId));
            gameDatas.setGameStatus(gameId, GameStatus.STARTING2);
        } else if (game.getStatus().equals(GameStatus.STARTING2)){
            gameDatas.setGameStatus(gameId, GameStatus.FIRST_STEP);
        } else if (game.getStatus().equals(GameStatus.FIRST_STEP)){
            jsonPlayersArrayBuilder = getPlayerArrayBuilder(gameId);
            gameDataBuilder.add("firstWord", gameDatas.giveFirstWord());
            gameDataBuilder.add("currentMessage", gameDatas.getFirstPlayerName(gameId) + " is thinking.");
            gameDataBuilder.add("playerTable", jsonPlayersArrayBuilder);
            gameDataBuilder.add("activePlayer", gameDatas.getFirstPlayerId(gameId));
            gameDatas.setGameStatus(gameId, GameStatus.GAMEINPROGRESS);
        } else if (game.getStatus().equals(GameStatus.GAMEINPROGRESS)){
            // KITENNI AZ ELŐZŐ BEÉRKEZETT SZÓT
            // KITENNI AZ IDŐKET A JÁTÉKOSOK TÁBLÁZATÁBA, PLUSZ JELÖLNI OTT, HOGY KI AZ AKTÍV
        } else if (game.getStatus().equals(GameStatus.CLOSED)){
            // AZ INPUT MEZŐ MINDENKINÉL INAKTÍV
            // ÜZENET: VÉGEREDMÉNY: RANGSOR AZ IDŐKKEL
        }

        JsonObject gameData = gameDataBuilder.build();

        return gameData.toString();
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
            jsonPlayerArrayBuilder.add(playerBuilder);
        }

        return jsonPlayerArrayBuilder;
    }


}
