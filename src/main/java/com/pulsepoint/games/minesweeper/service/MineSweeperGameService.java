package com.pulsepoint.games.minesweeper.service;

import com.google.common.collect.ImmutableMap;
import com.pulsepoint.games.minesweeper.dto.*;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextInt;

/**
 * Main logic for the Minesweeper game.
 * 
 * @author avenkatraman
 */
public class MineSweeperGameService {
    
    private final Map<Level, ComplexityConfig> levelConfig;
    private Map<String, Game> games = newHashMap();
    private Map<String, Set<Position>> gameMines = newHashMap();

    public MineSweeperGameService() {
        levelConfig = ImmutableMap.of(
                Level.EASY, new ComplexityConfig(8, 8, 10),
                Level.MEDIUM, new ComplexityConfig(12, 12, 20),
                Level.HARD, new ComplexityConfig(16, 16, 30)
        );
    }

    public Game createGame(Level level) {
        String gameId = randomAlphanumeric(10);
        ComplexityConfig complexityConfig = levelConfig.get(level);
        /*
         * Setup the game with boxes based on level of complexity.
         */
        Box[][] boxes = new Box[complexityConfig.getRows()][complexityConfig.getColumns()];
        for(int i=0; i < complexityConfig.getRows(); i ++) {
            for(int j=0; j < complexityConfig.getColumns(); j ++) {
                boxes[i][j] = new Box().setStatus(BoxStatus.CLOSED); 
            }
        }
        Board board = new Board().setBoxes(boxes).setBoxesOpened(0).setMinesMarked(0);
        Set<Position> minePositions = newHashSet();
        while(minePositions.size() < complexityConfig.getMineCount()) {
            minePositions.add(new Position(nextInt(0, complexityConfig.getRows()), nextInt(0, complexityConfig.getColumns())));   
        }
        Game game = new Game(gameId, board, complexityConfig);
        gameMines.put(gameId, minePositions);
        games.put(gameId, game);
        return game;
    }
    
    public OpenBoxOperationStatus openBox(String gameId, int x, int y) {
        Game game = games.get(gameId);
        Set<Position> mines = gameMines.get(gameId);
        Box box = game.getBoard().getBox(x, y);
        if (game.getStatus() == GameStatus.IN_PLAY && box.getStatus() != BoxStatus.OPENED) {
            if (mines.contains(new Position(x, y))) {
                box.setStatus(BoxStatus.OPENED_MINE);
                game.setStatus(GameStatus.LOST);
                return new OpenBoxOperationStatus(newHashSet(new BoxPosition(box, new Position(x, y))), GameStatus.LOST);
            } else {
                game.getBoard().getBox(x, y).setStatus(BoxStatus.OPENED);
                Set<BoxPosition> boxesAffected = openBox(x, y, game.getBoard().getBoxes(), gameMines.get(gameId), game.getComplexity());
                boolean gameDone = isGameDone(game, mines);
                return new OpenBoxOperationStatus(boxesAffected, gameDone ? GameStatus.WON : GameStatus.IN_PLAY);
            }
        }
        return null;
    }

    private boolean isGameDone(Game game, Set<Position> mines) {
        Set<Position> markedMines = newHashSet();
        Box[][] boxes = game.getBoard().getBoxes();
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes[0].length; j++) {
                if (boxes[i][j].getStatus() == BoxStatus.MARKED_MINE) {
                    markedMines.add(new Position(i, j));
                }
            }
        }
        if (markedMines.size() != mines.size()) {
            return false;
        }
        return !markedMines.removeAll(mines) || markedMines.size() <= 0;
    }

    private Set<BoxPosition> openBox(int x, int y, Box[][] boxes, Set<Position> mines, ComplexityConfig complexityConfig) {
        int adjacentMineCount = 0;
        Box box = boxes[x][y];
        Set<BoxPosition> boxesAffected = newHashSet(new BoxPosition(box, new Position(x, y)));
        Set<Position> adjacentPositions = newHashSet();
        addToSet(adjacentPositions, x + 1, y + 1, complexityConfig);
        addToSet(adjacentPositions, x + 1, y, complexityConfig);
        addToSet(adjacentPositions, x, y + 1, complexityConfig);
        addToSet(adjacentPositions, x - 1, y -1, complexityConfig);
        addToSet(adjacentPositions, x - 1, y, complexityConfig);
        addToSet(adjacentPositions, x, y -1, complexityConfig);
        addToSet(adjacentPositions, x - 1, y + 1, complexityConfig);
        addToSet(adjacentPositions, x + 1, y - 1, complexityConfig);
        for(Position adjacent : adjacentPositions) {
            if (mines.contains(adjacent)) {
                adjacentMineCount ++;
            }
        }
        box.setStatus(BoxStatus.OPENED);
        box.setAdjacentMineCount(adjacentMineCount);
        if (adjacentMineCount == 0) {
            for(Position adjacent : adjacentPositions) {
                if (boxes[adjacent.getX()][adjacent.getY()].getStatus() == BoxStatus.CLOSED && ! mines.contains(adjacent)) {
                    boxesAffected.addAll(openBox(adjacent.getX(), adjacent.getY(), boxes, mines, complexityConfig));
                }
            }
        }
        return boxesAffected;
    }

    private void addToSet(Set<Position> adjacentBoxes, int x, int y, ComplexityConfig complexityConfig) {
        if (x >= 0 && x < complexityConfig.getRows() && y >= 0 && y < complexityConfig.getColumns()) {
            adjacentBoxes.add(new Position(x, y));
        }
    }

    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    public OpenBoxOperationStatus markMine(String gameId, int x, int y) {
        Game game = games.get(gameId);
        if(game.getStatus() == GameStatus.IN_PLAY) {
            Box box = game.getBoard().getBox(x, y);
            if (box.getStatus() == BoxStatus.CLOSED) {
                box.setStatus(BoxStatus.MARKED_MINE);
                boolean gameDone = isGameDone(game, gameMines.get(gameId));
                return new OpenBoxOperationStatus(newHashSet(new BoxPosition(box, new Position(x, y))), gameDone ? GameStatus.WON : GameStatus.IN_PLAY);
            }
            if (box.getStatus() == BoxStatus.MARKED_MINE) {
                box.setStatus(BoxStatus.CLOSED);
                return new OpenBoxOperationStatus(newHashSet(new BoxPosition(box, new Position(x, y))), GameStatus.IN_PLAY);
            }
        }
        return null;
    }
}
