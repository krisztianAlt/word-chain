package com.wordchain.model;

import javax.persistence.*;

@Entity
public class WordChainItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String word;

    /*@ManyToOne
    private Game game;*/

    @ManyToOne
    private Player player;

    public WordChainItem(String word, Game game, Player player) {
        this.word = word;
        // this.game = game;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    /*public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }*/

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
