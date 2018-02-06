package com.wordchain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordchain.WordChain;

import javax.persistence.*;
import java.util.*;

@NamedQueries({
        @NamedQuery(
                name = "Player.getAllPlayer",
                query = "SELECT p FROM Player p"
        ),
        @NamedQuery(
                name="Player.getPlayerByEmail",
                query = "SELECT p FROM Player p WHERE p.email = :email"
        ),
        @NamedQuery(
                name = "Player.getPlayerById",
                query = "SELECT p FROM Player p WHERE p.id = :id"
        )
})
@Entity
@Table(name = "PLAYER")
public class Player {

    @Transient
    public static List<Player> onlinePlayers = new ArrayList<>();

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
    // @JsonIgnore
    private UserLegitimacy legitimacy;

    @ManyToMany
    // @JsonIgnore
    private List<WordCard> myWordCards;

    @ManyToMany
    // @JsonIgnore
    private List<Game> games;

    @OneToMany(mappedBy = "creator")
    // @JsonIgnore
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
