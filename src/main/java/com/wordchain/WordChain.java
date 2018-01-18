package com.wordchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WordChain {

    public static void main(String[] args) {
        System.out.println("HI");
        SpringApplication.run(WordChain.class, args);
    }
}
