package com.wordchain.model;

import javax.persistence.*;
import java.util.*;

@NamedQueries({
        @NamedQuery(
                name="Game.getGameById",
                query = "SELECT g FROM Game g WHERE g.id = :gameId"
        )
})
@Entity
@Table
public class Game {

    @Transient
    public static List<Long> onlineGames = new ArrayList<>();

    @Transient
    public static Map<Long, Map<Long, Boolean>> playerEnteredIntoGameWindow = new HashMap<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @ManyToOne
    private Player creator;

    @ManyToMany
    private List<Player> players;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAndTime;

    @OneToMany(mappedBy = "game")
    private List<WordChainItem> wordChain;

    public Game() {}

    public Game(Player creator, Date dateAndTime) {
        this.creator = creator;
        this.dateAndTime = dateAndTime;
        this.status = GameStatus.NEW;
        this.gameType = GameType.Timelimit;
        this.wordChain = new ArrayList<>();
        this.players = new ArrayList<>();
        this.players.add(creator);
        creator.addNewGameToCreatedGames(this);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public List<WordChainItem> getWordChain() {
        return wordChain;
    }

    public void setWordChain(List<WordChainItem> wordChain) {
        this.wordChain = wordChain;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public String addNewPlayerToGame(Player newPlayer){
        String status = "OK";
        boolean playerIsNotInGame = true;

        for (Player player : players){
            if (player.getId() == newPlayer.getId()){
                playerIsNotInGame = false;
                status = "Player is in this game already.";
            }
        }

        if (playerIsNotInGame){
            this.players.add(newPlayer);
        }

        return status;
    }

    public void removePlayerFromGame(Player player){
        players.remove(player);
    }

}
