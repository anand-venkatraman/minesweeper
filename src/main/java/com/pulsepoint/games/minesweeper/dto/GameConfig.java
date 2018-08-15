package com.pulsepoint.games.minesweeper.dto;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * Complexity settings for the game.
 * 
 * @author avenkatraman
 */
public class GameConfig {
    
    private int columns;
    private int rows;
    private int mineCount;

    public GameConfig(int columns, int rows, int mineCount) {
        this.columns = columns;
        this.rows = rows;
        this.mineCount = mineCount;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getMineCount() {
        return mineCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameConfig that = (GameConfig) o;
        return columns == that.columns &&
                rows == that.rows &&
                mineCount == that.mineCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columns, rows, mineCount);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("columns", columns)
                .add("rows", rows)
                .add("mineCount", mineCount)
                .toString();
    }
}
