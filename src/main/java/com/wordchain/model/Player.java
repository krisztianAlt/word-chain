package com.wordchain.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String userName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserLegitimacy legitimacy;

    @ManyToMany
    private List<Word> myWords;

    @ManyToMany
    private List<Game> games;

    @OneToMany(mappedBy = "creator")
    private List<Game> createdGames;

    public Player() {
    }

    public Player(String userName, String email, String password, UserLegitimacy legitimacy) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.legitimacy = legitimacy;
        this.myWords = new ArrayList<>();
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

    public List<Word> getMyWords() {
        return myWords;
    }

    public void setMyWords(List<Word> myWords) {
        this.myWords = myWords;
    }

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
