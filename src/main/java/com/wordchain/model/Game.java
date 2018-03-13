package com.wordchain.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table
public class Game {

    @Transient
    public static List<Long> onlineGames = new ArrayList<>();

    @Transient
    public static Map<Long, Map<Long, Boolean>> playerEnteredIntoGameWindow = new HashMap<>();

    @Transient
    public static Map<Long, Map<String, Integer>> rounds = new HashMap<>();

    @Transient
    public static Map<Long, List<Long>> playerOrder = new HashMap<>();

    @Transient
    public static Map<Long, Map<Long, Integer>> timeResults = new HashMap<>();

    @Transient
    public static Map<Long, Map<Long, Integer>> letters = new HashMap<>();

    @Transient
    public static Map<Long, List<String>> wordChains = new HashMap<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @ManyToOne
    private Player creator;

    /*@ManyToMany
    private List<Player> players;*/

    /*@ManyToMany(mappedBy = "games")
    private List<Player> players;*/

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = {@JoinColumn(name = "game_id")},
            inverseJoinColumns = {@JoinColumn(name = "player_id")}
    )
    private List<Player> players;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAndTime;

    /*@OneToMany(mappedBy = "game")
    private List<WordChainItem> wordChain;*/

    private String wordChain;

    private Integer maxRound;

    public Game() {}

    public Game(Player creator, Date dateAndTime, Integer maxRound) {
        this.creator = creator;
        this.dateAndTime = dateAndTime;
        this.status = GameStatus.NEW;
        this.gameType = GameType.Timing;
        this.wordChain = "";
        this.maxRound = maxRound;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public String getWordChain() {
        return wordChain;
    }

    public void setWordChain(String wordChain) {
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

    public Integer getMaxRound() {
        return maxRound;
    }

    public void setMaxRound(Integer maxRound) {
        this.maxRound = maxRound;
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
