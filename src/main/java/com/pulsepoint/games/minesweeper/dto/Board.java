package com.pulsepoint.games.minesweeper.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the board used for the {@link Game}
 * 
 * @author avenkatraman
 */
public class Board {
    
    @JsonIgnore
    private Map<Position, Box> boxes;
    private int totalMines;
    private int minesMarked;
    private int boxesOpened;

    public Map<Position, Box> getBoxes() {
        return boxes;
    }

    public int getTotalMines() {
        return totalMines;
    }

    public int getMinesMarked() {
        return minesMarked;
    }

    public int getBoxesOpened() {
        return boxesOpened;
    }

    public Board setBoxes(Map<Position, Box> boxes) {
        this.boxes = boxes;
        return this;
    }

    public Board setTotalMines(int totalMines) {
        this.totalMines = totalMines;
        return this;
    }

    public Board setMinesMarked(int minesMarked) {
        this.minesMarked = minesMarked;
        return this;
    }

    public Board setBoxesOpened(int boxesOpened) {
        this.boxesOpened = boxesOpened;
        return this;
    }
    
    @JsonProperty("boxes")
    public Collection<Box> getBoxValues() {
        return boxes.values();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return totalMines == board.totalMines &&
                minesMarked == board.minesMarked &&
                boxesOpened == board.boxesOpened &&
                Objects.equals(boxes, board.boxes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boxes, totalMines, minesMarked, boxesOpened);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("boxes", boxes)
                .add("totalMines", totalMines)
                .add("minesMarked", minesMarked)
                .add("boxesOpened", boxesOpened)
                .toString();
    }
}