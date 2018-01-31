package com.wordchain.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Player creator;

    @ManyToMany(mappedBy = "games")
    private List<Player> players;

    @Temporal(TemporalType.DATE)
    private Date dateAndTime;

    public Game(Player creator, Date dateAndTime) {
        this.creator = creator;
        this.dateAndTime = dateAndTime;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

}
