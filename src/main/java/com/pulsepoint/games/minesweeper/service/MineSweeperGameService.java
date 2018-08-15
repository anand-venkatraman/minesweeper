package com.pulsepoint.games.minesweeper.service;

import com.pulsepoint.games.minesweeper.dto.Game;
import com.pulsepoint.games.minesweeper.dto.Level;
import com.pulsepoint.games.minesweeper.dto.OpenBoxOperationStatus;

/**
 * Service operations for the Minesweeper Game.
 * 
 * @author avenkatraman
 */
public interface MineSweeperGameService {

    /**
     * Creates a Game, given the level of complexity.
     * 
     * @param level Complexity {@link Level}
     * @return {@link Game} instance.
     */
    Game createGame(Level level);

    /**
     * Open a box in a given game, given the x and y coordinates with reference to
     * the top of the board. x and y are indexed from 0. Opening a box might result
     * in additional boxes getting opened, should be box opened be sorrounded by no
     * mines. Opening a box with a mine will result in the Game getting over.
     * 
     * @param gameId Identifier for the game.
     * @param x x coordinate for the Box with reference to the left of Board
     * @param y y coordinate for the Box with reference to the top of Board
     * @return A status object holding the result of the operation. This will hold
     * the Boxes affected, and if the overall Game status has changed.
     */
    OpenBoxOperationStatus openBox(String gameId, int x, int y);

    /**
     * Gets the game given the identifier.
     * 
     * @param gameId Identifier of the Game
     * @return Game instance.
     */
    Game getGame(String gameId);

    /**
     * Marks a box as having mine. 
     * If the box in question has already been marked as having mine, this operation
     * should unmark the box. This operation could result in the game getting over if
     * the mine marked is the last of the mines to be found.
     * Boxes with no mines under, can also be marked. However, game will not be considered
     * as Won.
     * 
     * @param gameId Identifier of the Game
     * @param x coordinate for the Box with reference to the left of Board
     * @param y y coordinate for the Box with reference to the top of Board 
     * @return A status object holding the result of the operation. This will hold
     * the Box affected, and if the overall Game status has changed.
     */
    OpenBoxOperationStatus markMine(String gameId, int x, int y);
}
