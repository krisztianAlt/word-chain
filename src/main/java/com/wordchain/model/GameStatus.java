package com.wordchain.model;

public enum GameStatus {
    NEW,
    PREPARATION, // waiting for all players and do the word tables
    STARTING1, // countdown message on client side
    STARTING2, // just give time for players to read their order in game
    FIRST_STEP, // computer gives the first word
    GAMEINPROGRESS,
    CLOSED // last round is over, ranking on client side
}
