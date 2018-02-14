package com.wordchain.service;

import com.wordchain.controller.PlayerAccountController;
import com.wordchain.model.Game;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Service
public class WordCheck {

    private Set<String> wordSet;

    private Set<String> properNouns;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(PlayerAccountController.class);

    public WordCheck() {
        Set<String> wordSet = new HashSet<>();
        Set<String> properNouns = new HashSet<>();
        String fileName = "src/main/resources/public/data/words";

        try{
            FileInputStream fis = new FileInputStream(fileName);

            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = br.readLine()) != null) {
                if (Character.isLowerCase(line.charAt(0)) &&
                        !line.contains("'") &&
                        line.length() > 1){
                    wordSet.add(line);
                } else if (Character.isUpperCase(line.charAt(0)) &&
                        !line.contains("'") &&
                        line.length() > 1){
                    properNouns.add(line);
                }
            }

            br.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e){
            logger.error(e.getMessage());
        }

        this.wordSet = wordSet;
        this.properNouns = properNouns;
    }

    public String wordIsValid(Long gameId, String word) {
        String status;

        if (word == ""){
            status = "You did not type word.";
        } else if (Game.wordChains.get(gameId).contains(word)){
            status = "This word is already in the chain. Type another one!";
        } else if (!firstAndLastLetterMatch(gameId, word)){
            status = "First letter must be the same like previous word's last letter.";
        } else if(properNouns.contains(word)){
            status = "It's a proper noun, it's not okay.";
        } else if (!wordSet.contains(word)){
            status = "This word does not exist in English.";
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