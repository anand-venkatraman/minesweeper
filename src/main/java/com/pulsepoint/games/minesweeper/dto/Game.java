package com.pulsepoint.games.minesweeper.dto;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * Represents the Game settings.
 * 
 * @author avenkatraman
 */
public class Game {
    
    private String id;
    private Board board;
    private GameStatus status;
    private long startTime;
    private long endTime;
    private GameConfig config;

    public Game(String id, Board board, GameConfig config) {
        this.id = id;
        this.startTime = System.currentTimeMillis();
        this.status = GameStatus.IN_PLAY;
        this.board = board;
        this.config = config;
    }

    public String getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Game setStatus(GameStatus status) {
        this.status = status;
        if (status == GameStatus.LOST || status == GameStatus.WON) {
            this.endTime = System.currentTimeMillis();
        }
        return this;
    }

    public GameConfig getConfig() {
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return startTime == game.startTime &&
                endTime == game.endTime &&
                Objects.equals(id, game.id) &&
                Objects.equals(board, game.board) &&
                status == game.status &&
                Objects.equals(config, game.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, status, startTime, endTime, config);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("board", board)
                .add("status", status)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("config", config)
                .toString();
    }
}
