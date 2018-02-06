package com.wordchain.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table
public class Game {

    @Transient
    public static List<Game> onlineGames = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private GameStatus status;

    @ManyToOne
    private Player creator;

    @ManyToMany(mappedBy = "games")
    private List<Player> players;

    @Temporal(TemporalType.DATE)
    private Date dateAndTime;

    @OneToMany(mappedBy = "game")
    private List<WordChainItem> wordChain;

    public Game(Player creator, Date dateAndTime) {
        this.creator = creator;
        this.dateAndTime = dateAndTime;
        this.status = GameStatus.NEW;
        this.wordChain = new ArrayList<>();
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
}
