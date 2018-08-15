package com.pulsepoint.games.minesweeper.service;

import com.pulsepoint.games.minesweeper.dto.Game;
import com.pulsepoint.games.minesweeper.dto.Level;
import com.pulsepoint.games.minesweeper.dto.OpenBoxOperationStatus;

/**
 * A no-op version of {@link MineSweeperGameService}.
 * Candidate is supposed to implement the interface methods, and 
 * get the tests to pass.
 * 
 * @author avenkatraman
 */
public class MineSweeperGameServiceNoImpl implements MineSweeperGameService {
    
    @Override
    public Game createGame(Level level) {
        return null;
    }

    @Override
    public OpenBoxOperationStatus openBox(String gameId, int x, int y) {
        return null;
    }

    @Override
    public Game getGame(String gameId) {
        return null;
    }

    @Override
    public OpenBoxOperationStatus markMine(String gameId, int x, int y) {
        return null;
    }
}
