// I use old style JSON solution without Spring's ResponseEntity.
// Sample code: https://docs.oracle.com/javaee/7/api/javax/json/JsonObjectBuilder.html

package com.wordchain.api;

import com.wordchain.model.Game;
import com.wordchain.model.GameStatus;
import com.wordchain.model.Player;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OnlineEntitiesREST {

    private JsonBuilderFactory factory = Json.createBuilderFactory(null);

    @GetMapping("/api/get-online-entities")
    public String getOnlineEntities(HttpServletRequest httpServletRequest) {
        Long playerId = (Long) httpServletRequest.getSession().getAttribute("player_id");

        JsonArrayBuilder jsonPlayersArrayBuilder = getPlayerArrayBuilder();
        Map<String, JsonArrayBuilder> jsonGamesArrayBuilder = getNewOnlineGamesBuilder(playerId);

        JsonObjectBuilder onlineEntitiesBuilder = factory.createObjectBuilder();

        onlineEntitiesBuilder.add("onlinePlayers", jsonPlayersArrayBuilder);

        if (playerId != null){
            onlineEntitiesBuilder.add("myNewGames", jsonGamesArrayBuilder.get("myOnlineNewGames"));
            onlineEntitiesBuilder.add("othersNewGames", jsonGamesArrayBuilder.get("othersOnlineNewGames"));
        } else {
            onlineEntitiesBuilder.add("everyNewGame", jsonGamesArrayBuilder.get("everyOnlineNewGame"));
        }

        JsonObject onlineEntities = onlineEntitiesBuilder.build();

        return onlineEntities.toString();

    }

    private JsonArrayBuilder getPlayerArrayBuilder() {
        JsonArrayBuilder jsonPlayerArrayBuilder = factory.createArrayBuilder();

        for (Player player : Player.onlinePlayers){
            JsonObjectBuilder onlinePlayerBuilder = factory.createObjectBuilder();
            onlinePlayerBuilder.add("playerId", player.getId());
            onlinePlayerBuilder.add("userName", player.getUserName());
            jsonPlayerArrayBuilder.add(onlinePlayerBuilder);
        }

        return jsonPlayerArrayBuilder;
    }

    private Map<String, JsonArrayBuilder> getNewOnlineGamesBuilder(Long playerId) {

        Map<String, JsonArrayBuilder> gameArrayBuilders = new HashMap<>();

        JsonArrayBuilder jsonMyGamesArrayBuilder = factory.createArrayBuilder();
        JsonArrayBuilder jsonOthersGamesArrayBuilder = factory.createArrayBuilder();
        JsonArrayBuilder jsonEveryNewGameArrayBuilder = factory.createArrayBuilder();

        for (Game game : Game.onlineGames){
            if (game.getStatus().equals(GameStatus.NEW)){

                JsonObjectBuilder onlineGamesBuilder = factory.createObjectBuilder();
                onlineGamesBuilder.add("gameId", game.getId());
                onlineGamesBuilder.add("creatorName", game.getCreator().getUserName());

                JsonArrayBuilder jsonPlayersInGameArrayBuilder = factory.createArrayBuilder();

                for (Player player : game.getPlayers()){
                    JsonObjectBuilder playerInGameBuilder = factory.createObjectBuilder();
                    playerInGameBuilder.add("playerId", player.getId());
                    playerInGameBuilder.add("userName", player.getUserName());
                    jsonPlayersInGameArrayBuilder.add(playerInGameBuilder);
                }

                onlineGamesBuilder.add("players", jsonPlayersInGameArrayBuilder);

                if (playerId != null && game.getCreator().getId() == playerId){
                    jsonMyGamesArrayBuilder.add(onlineGamesBuilder);
                } else if (playerId != null && game.getCreator().getId() != playerId){
                    jsonOthersGamesArrayBuilder.add(onlineGamesBuilder);
                } else if (playerId == null){
                    jsonEveryNewGameArrayBuilder.add(onlineGamesBuilder);
                }
            }
        }

        if (playerId != null){
            gameArrayBuilders.put("myOnlineNewGames", jsonMyGamesArrayBuilder);
            gameArrayBuilders.put("othersOnlineNewGames", jsonOthersGamesArrayBuilder);
        } else {
            gameArrayBuilders.put("everyOnlineNewGame", jsonEveryNewGameArrayBuilder);
        }

        return gameArrayBuilders;
    }

    /*@GetMapping("/api/get-online-entities")
    public ResponseEntity<Map<String, List<Player>>> getOnlineEntities() {
        Map<String, List> onlineEntities = new HashMap();
        onlineEntities.put("onlinePlayers", Player.onlinePlayers);

        return new ResponseEntity(onlineEntities, HttpStatus.OK);
    }*/

}
