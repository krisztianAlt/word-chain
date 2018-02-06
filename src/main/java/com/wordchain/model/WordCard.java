package com.wordchain.model;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "WORDCARD")
public class WordCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String word;

    private String hungarianTranslation;

    @ManyToMany(mappedBy = "myWordCards")
    private List<Player> players;

    public WordCard(String word) {

        this.word = word;

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

    public String getHungarianTranslation() {
        return hungarianTranslation;
    }

    public void setHungarianTranslation(String hungarianTranslation) {
        this.hungarianTranslation = hungarianTranslation;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
