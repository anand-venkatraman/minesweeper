package com.pulsepoint.games.minesweeper.dto;

/**
 * Represents the status of a {@link Game}
 * 
 * @author avenkatraman
 */
public enum GameStatus {

    /**
     * Game is in progress
     */
    IN_PLAY,
    
    /**
     * Game has been lost
     */
    LOST,
    
    /**
     * Game has been won
     */
    WON
    
}
