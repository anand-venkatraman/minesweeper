package com.pulsepoint.games.minesweeper.dto;

/**
 * Represents the status of a {@link Box}
 * 
 * @author avenkatraman
 */
public enum BoxStatus {

    /**
     * Box has been unopened
     */
    CLOSED,

    /**
     * Box has been marked as mine.
     */
    MARKED_MINE,

    /**
     * Box has been opened
     */
    OPENED,

    /**
     * This status means the game is lost.
     * Box has been opened and it had a mine.
     */
    OPENED_MINE,
    
}
