package com.pulsepoint.games.minesweeper.dto;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents a single box in a {@link Board}
 * 
 * @author avenkatraman
 */
public class Box {
    
    private BoxStatus status;
    private int adjacentMineCount;

    public BoxStatus getStatus() {
        return status;
    }

    public Box setStatus(BoxStatus status) {
        this.status = status;
        return this;
    }

    public int getAdjacentMineCount() {
        return adjacentMineCount;
    }

    public Box setAdjacentMineCount(int adjacentMineCount) {
        this.adjacentMineCount = adjacentMineCount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return adjacentMineCount == box.adjacentMineCount &&
                status == box.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, adjacentMineCount);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Box.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("adjacentMineCount=" + adjacentMineCount)
                .toString();
    }
}
