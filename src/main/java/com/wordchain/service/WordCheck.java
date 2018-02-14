package com.wordchain.service;

import com.wordchain.model.Game;
import org.springframework.stereotype.Service;

@Service
public class WordCheck {


    public String wordIsValid(Long gameId, String word) {
        String status;

        if (word == ""){
            status = "You did not type word.";
        } else if (Game.wordChains.get(gameId).contains(word)){
            status = "This word is already in the chain. Type another one!";
        } else if (!firstAndLastLetterMatch(gameId, word)){
            status = "First letter must be the same like previous word's last letter.";
        } else {
            status = "Accepted.";
        }

        return status;
    }

    private boolean firstAndLastLetterMatch(Long gameId, String word) {
        int lastWordIndex = Game.wordChains.get(gameId).size() - 1;
        String previousWord = Game.wordChains.get(gameId).get(lastWordIndex);
        return previousWord.substring(previousWord.length()-1, previousWord.length()).equals(word.substring(0, 1));

    }
}
