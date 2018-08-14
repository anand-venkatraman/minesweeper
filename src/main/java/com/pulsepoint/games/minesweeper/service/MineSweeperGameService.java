package com.pulsepoint.games.minesweeper.service;

import com.google.common.collect.ImmutableMap;
import com.pulsepoint.games.minesweeper.dto.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
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
        Map<Position, Box> boxes = IntStream.range(0, complexityConfig.getRows())
                .mapToObj(i -> IntStream.range(0, complexityConfig.getColumns())
                        .mapToObj(j -> new Box().setPosition(new Position(j, i)).setStatus(BoxStatus.CLOSED))
                        .collect(toList()))
                .flatMap(Collection::stream)
                .collect(toMap(Box::getPosition, v -> v));
        Board board = new Board().setBoxes(boxes).setBoxesOpened(0).setMinesMarked(0);
        Set<Position> minePositions = newHashSet();
        IntStream.of(complexityConfig.getMineCount()).forEach(i -> {
            while(minePositions.size() < i) {
                minePositions.add(new Position(nextInt(0, complexityConfig.getRows()), nextInt(0, complexityConfig.getColumns())));   
            }
        });
        Game game = new Game(gameId, board, complexityConfig);
        gameMines.put(gameId, minePositions);
        games.put(gameId, game);
        return game;
    }
    
    public OpenBoxOperationStatus openBox(String gameId, int x, int y) {
        Game game = games.get(gameId);
        Set<Position> mines = gameMines.get(gameId);
        Box box = game.getBoard().getBoxes().get(new Position(x, y));
        if (game.getStatus() == GameStatus.IN_PLAY && box.getStatus() != BoxStatus.OPENED) {
            if (mines.contains(new Position(x, y))) {
                box.setStatus(BoxStatus.OPENED_MINE);
                game.setStatus(GameStatus.LOST);
                return new OpenBoxOperationStatus(newHashSet(box), GameStatus.LOST);
            } else {
                game.getBoard().getBoxes().get(new Position(x, y)).setStatus(BoxStatus.OPENED);
                Set<Box> boxesAffected = openBox(box.getPosition(), game.getBoard().getBoxes(), gameMines.get(gameId), game.getComplexity());
                boolean gameDone = isGameDone(game, mines);
                return new OpenBoxOperationStatus(boxesAffected, gameDone ? GameStatus.WON : GameStatus.IN_PLAY);
            }
        }
        return null;
    }

    private boolean isGameDone(Game game, Set<Position> mines) {
        Set<Position> markedMines = newHashSet();
        Map<Position, Box> boxes = game.getBoard().getBoxes();
        int openedCount = 0;
        for(Position position : boxes.keySet()) {
            switch (boxes.get(position).getStatus()) {
                case MARKED_MINE:
                    markedMines.add(position);
                    break;
                case OPENED:
                    openedCount ++;
                    break;
                case CLOSED:
                    return false;
            }
        }
        if (markedMines.size() != mines.size()) {
            return false;
        }
        if (markedMines.removeAll(mines) && markedMines.size() > 0) {
            return false;
        }
        return mines.size() + openedCount == boxes.size();
    }

    private Set<Box> openBox(Position position, Map<Position, Box> boxes, Set<Position> mines, ComplexityConfig complexityConfig) {
        int adjacentMineCount = 0;
        Set<Box> boxesAffected = newHashSet(boxes.get(position));
        Set<Position> adjacentPositions = newHashSet();
        addToSet(adjacentPositions, position.getX() + 1, position.getY() + 1, complexityConfig);
        addToSet(adjacentPositions, position.getX() + 1, position.getY(), complexityConfig);
        addToSet(adjacentPositions, position.getX(), position.getY() + 1, complexityConfig);
        addToSet(adjacentPositions, position.getX() - 1, position.getY() -1, complexityConfig);
        addToSet(adjacentPositions, position.getX() - 1, position.getY(), complexityConfig);
        addToSet(adjacentPositions, position.getX(), position.getY() -1, complexityConfig);
        addToSet(adjacentPositions, position.getX() - 1, position.getY() + 1, complexityConfig);
        addToSet(adjacentPositions, position.getX() + 1, position.getY() - 1, complexityConfig);
        for(Position adjacent : adjacentPositions) {
            if (mines.contains(adjacent)) {
                adjacentMineCount ++;
            }
        }
        boxes.get(position).setStatus(BoxStatus.OPENED);
        boxes.get(position).setAdjacentMineCount(adjacentMineCount);
        if (adjacentMineCount == 0) {
            for(Position adjacent : adjacentPositions) {
                if (boxes.get(adjacent).getStatus() == BoxStatus.CLOSED && ! mines.contains(adjacent)) {
                    boxesAffected.addAll(openBox(adjacent, boxes, mines, complexityConfig));
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
            Box box = game.getBoard().getBoxes().get(new Position(x, y));
            if (box.getStatus() == BoxStatus.CLOSED) {
                box.setStatus(BoxStatus.MARKED_MINE);
                boolean gameDone = isGameDone(game, gameMines.get(gameId));
                return new OpenBoxOperationStatus(newHashSet(box), gameDone ? GameStatus.WON : GameStatus.IN_PLAY);
            }
            if (box.getStatus() == BoxStatus.MARKED_MINE) {
                box.setStatus(BoxStatus.CLOSED);
                return new OpenBoxOperationStatus(newHashSet(box), GameStatus.IN_PLAY);
            }
        }
        return null;
    }
}
