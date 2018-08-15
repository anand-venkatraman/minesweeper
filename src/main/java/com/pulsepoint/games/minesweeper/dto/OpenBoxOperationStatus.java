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

    private Set<BoxPosition> boxesAffected;
    private GameStatus status;

    public OpenBoxOperationStatus(Set<BoxPosition> boxesAffected, GameStatus status) {
        this.boxesAffected = boxesAffected;
        this.status = status;
    }

    public Set<BoxPosition> getBoxesAffected() {
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

    /**
     * DTO to hold the Box and the Position of the box relative
     * to the top left of the Board.
     */
    public static class BoxPosition {

        private Box box;
        private Position position;

        public BoxPosition(Box box, Position position) {
            this.box = box;
            this.position = position;
        }

        public Box getBox() {
            return box;
        }

        public Position getPosition() {
            return position;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BoxPosition that = (BoxPosition) o;
            return Objects.equals(box, that.box) &&
                    Objects.equals(position, that.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(box, position);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("box", box)
                    .add("position", position)
                    .toString();
        }
    }
}
