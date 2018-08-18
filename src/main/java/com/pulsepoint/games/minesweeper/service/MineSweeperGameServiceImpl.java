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
public class MineSweeperGameServiceImpl implements MineSweeperGameService {
    
    private final Map<Level, Double> mineDensity;
    private final Map<Level, Integer> boardDimension;
    private Map<String, Game> games = newHashMap();
    private Map<String, Set<Position>> gameMines = newHashMap();

    public MineSweeperGameServiceImpl() {
        mineDensity = ImmutableMap.of(Level.EASY, 0.12, Level.MEDIUM, 0.15, Level.HARD, 0.2);
        boardDimension = ImmutableMap.of(Level.EASY, 8, Level.MEDIUM, 12, Level.HARD, 16);
    }

    @Override
    public Game createGame(Level level) {
        String gameId = randomAlphanumeric(10);
        Integer dimension = boardDimension.get(level);
        int mineCount = (int) Math.ceil(mineDensity.get(level) * dimension * dimension);
        /*
         * Setup the game with boxes based on level of complexity.
         */
        Box[][] boxes = new Box[dimension][dimension];
        for(int i=0; i < dimension; i ++) {
            for(int j=0; j < dimension; j ++) {
                boxes[i][j] = new Box().setStatus(BoxStatus.CLOSED); 
            }
        }
        Board board = new Board().setBoxes(boxes).setBoxesOpened(0).setMinesMarked(0);
        Set<Position> minePositions = newHashSet();
        while(minePositions.size() < mineCount) {
            minePositions.add(new Position(nextInt(0, dimension), nextInt(0, dimension)));   
        }
        Game game = new Game(gameId, board, mineCount);
        gameMines.put(gameId, minePositions);
        games.put(gameId, game);
        return game;
    }
    
    @Override
    public OpenBoxOperationStatus openBox(String gameId, int row, int col) {
        Game game = games.get(gameId);
        Set<Position> mines = gameMines.get(gameId);
        Box box = game.getBoard().getBox(row, col);
        if (game.getStatus() == GameStatus.IN_PLAY && box.getStatus() != BoxStatus.OPENED) {
            if (mines.contains(new Position(row, col))) {
                box.setStatus(BoxStatus.OPENED_MINE);
                game.setStatus(GameStatus.LOST);
                return new OpenBoxOperationStatus(newHashSet(new OpenBoxOperationStatus.BoxPosition(box, new Position(row, col))), GameStatus.LOST);
            } else {
                game.getBoard().getBox(row, col).setStatus(BoxStatus.OPENED);
                Set<OpenBoxOperationStatus.BoxPosition> boxesAffected = openBox(row, col, game.getBoard(), gameMines.get(gameId));
                boolean gameDone = isGameDone(game, mines);
                return new OpenBoxOperationStatus(boxesAffected, gameDone ? GameStatus.WON : GameStatus.IN_PLAY);
            }
        }
        return null;
    }

    private boolean isGameDone(Game game, Set<Position> mines) {
        Set<Position> markedMines = newHashSet();
        for (int rowId = 0; rowId < game.getBoard().getRows(); rowId++) {
            for (int colId = 0; colId < game.getBoard().getColumns(); colId++) {
                if (game.getBoard().getBox(rowId, colId).getStatus() == BoxStatus.MARKED_MINE) {
                    markedMines.add(new Position(rowId, colId));
                }
            }
        }
        if (markedMines.size() != mines.size()) {
            return false;
        }
        return !markedMines.removeAll(mines) || markedMines.size() <= 0;
    }

    private Set<OpenBoxOperationStatus.BoxPosition> openBox(int row, int col, Board board, Set<Position> mines) {
        int adjacentMineCount = 0;
        Box box = board.getBox(row, col);
        Set<OpenBoxOperationStatus.BoxPosition> boxesAffected = newHashSet(new OpenBoxOperationStatus.BoxPosition(box, new Position(row, col)));
        Set<Position> adjacentPositions = newHashSet();
        addToSet(adjacentPositions, row + 1, col + 1, board);
        addToSet(adjacentPositions, row + 1, col, board);
        addToSet(adjacentPositions, row, col + 1, board);
        addToSet(adjacentPositions, row - 1, col -1, board);
        addToSet(adjacentPositions, row - 1, col, board);
        addToSet(adjacentPositions, row, col -1, board);
        addToSet(adjacentPositions, row - 1, col + 1, board);
        addToSet(adjacentPositions, row + 1, col - 1, board);
        for(Position adjacent : adjacentPositions) {
            if (mines.contains(adjacent)) {
                adjacentMineCount ++;
            }
        }
        box.setStatus(BoxStatus.OPENED);
        box.setAdjacentMineCount(adjacentMineCount);
        if (adjacentMineCount == 0) {
            for(Position adjacent : adjacentPositions) {
                if (board.getBox(adjacent.getRow(), adjacent.getCol()).getStatus() == BoxStatus.CLOSED && ! mines.contains(adjacent)) {
                    boxesAffected.addAll(openBox(adjacent.getRow(), adjacent.getCol(), board, mines));
                }
            }
        }
        return boxesAffected;
    }

    private void addToSet(Set<Position> adjacentBoxes, int row, int col, Board board) {
        if (row >= 0 && row < board.getRows() && col >= 0 && col < board.getColumns()) {
            adjacentBoxes.add(new Position(row, col));
        }
    }

    @Override
    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    @Override
    public OpenBoxOperationStatus markMine(String gameId, int row, int col) {
        Game game = games.get(gameId);
        if(game.getStatus() == GameStatus.IN_PLAY) {
            Box box = game.getBoard().getBox(row, col);
            if (box.getStatus() == BoxStatus.CLOSED) {
                box.setStatus(BoxStatus.MARKED_MINE);
                boolean gameDone = isGameDone(game, gameMines.get(gameId));
                return new OpenBoxOperationStatus(newHashSet(new OpenBoxOperationStatus.BoxPosition(box, new Position(row, col))), gameDone ? GameStatus.WON : GameStatus.IN_PLAY);
            }
            if (box.getStatus() == BoxStatus.MARKED_MINE) {
                box.setStatus(BoxStatus.CLOSED);
                return new OpenBoxOperationStatus(newHashSet(new OpenBoxOperationStatus.BoxPosition(box, new Position(row, col))), GameStatus.IN_PLAY);
            }
        }
        return null;
    }
    
    void addGame(Game game, Set<Position> mineLocations) {
        this.gameMines.put(game.getId(), mineLocations);
        this.games.put(game.getId(), game);
    }
}
