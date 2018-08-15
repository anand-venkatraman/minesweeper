package com.pulsepoint.games.minesweeper.dto;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * DTO to hold the position and the Box information.
 * Used when returning back the boxes affected during open operation.
 * 
 * @author avenkatraman
 */
public class BoxPosition {
    
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
