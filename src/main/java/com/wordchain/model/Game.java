package com.wordchain.model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name="Game.updatePlayerListOfGame",
                query= "UPDATE Game g SET g.players = :players WHERE g.id = :gameId"),
        @NamedQuery(
                name="Game.getGameById",
                query = "SELECT g FROM Game g WHERE g.id = :gameId"
        )
})
@Entity
@Table
public class Game {

    @Transient
    public static List<Game> onlineGames = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @ManyToOne
    private Player creator;

    //@ManyToMany(mappedBy = "games")
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

    public void addNewPlayerToGame(Player newPlayer){
        this.players.add(newPlayer);
    }

    public void removePlayerFromGame(Player player){
        players.remove(player);
    }

    public void deleteGame(){
        onlineGames.remove(this);
    }

    public void refreshGameInOnlineGames(Game game) {
        int index = 0;
        for (Game onlineGame : onlineGames){
            if (onlineGame.getId() == game.getId()){
                break;
            }
            index++;
        }
        onlineGames.set(index, game);
    }
}
