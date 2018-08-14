package com.pulsepoint.games.minesweeper.dto;

import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.Set;

/**
 * Represents the status of the operation to open a {@link Box}.
 * Multiple boxes can be affected, and the game could either be won/lost
 * depending on whether it was the last box or the mine box.
 * 
 * @author avenkatraman
 */
public class OpenBoxOperationStatus {
    
    private Set<Box> boxesAffected;
    private GameStatus status;

    public OpenBoxOperationStatus(Set<Box> boxesAffected, GameStatus status) {
        this.boxesAffected = boxesAffected;
        this.status = status;
    }

    public Set<Box> getBoxesAffected() {
        return boxesAffected;
    }

    public GameStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenBoxOperationStatus that = (OpenBoxOperationStatus) o;
        return Objects.equals(boxesAffected, that.boxesAffected) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(boxesAffected, status);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("boxesAffected", boxesAffected)
                .add("status", status)
                .toString();
    }
}
