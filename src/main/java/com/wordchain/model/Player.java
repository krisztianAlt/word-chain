package com.wordchain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordchain.WordChain;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "PLAYER")
public class Player {

    @Transient
    public static List<Long> onlinePlayers = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String userName;

    @Column(unique = true, nullable = false)
    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private UserLegitimacy legitimacy;

    @ManyToMany
    private List<WordCard> myWordCards;

    // hints: https://vladmihalcea.com/a-beginners-guide-to-jpa-and-hibernate-cascade-types/
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "players", cascade = CascadeType.ALL)
    private List<Game> games;

    @OneToMany(mappedBy = "creator")
    private List<Game> createdGames;

    @OneToMany(mappedBy = "player")
    private List<WordChainItem> wordsInChains;

    public Player() {
    }

    public Player(String userName, String email, String password, UserLegitimacy legitimacy) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.legitimacy = legitimacy;
        // this.myWordCards = new ArrayList<>();
        this.createdGames = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserLegitimacy getLegitimacy() {
        return legitimacy;
    }

    public void setLegitimacy(UserLegitimacy legitimacy) {
        this.legitimacy = legitimacy;
    }

    /*public List<WordCard> getMyWordCards() {
        return myWordCards;
    }

    public void setMyWordCards(List<WordCard> myWordCards) {
        this.myWordCards = myWordCards;
    }*/

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public void addNewGameToCreatedGames(Game game){
        createdGames.add(game);
    }

    public void joinToNewGame(Game game){
        games.add(game);
    }

    public void leaveGame(Game game) {
        games.remove(game);
    }

    @Override
    public String toString() {
        return String.format(
                "Person: id = %d," +
                "username = '%s', " +
                "legitimacy = '%s', ",
                id,
                userName,
                legitimacy);
    }


}
