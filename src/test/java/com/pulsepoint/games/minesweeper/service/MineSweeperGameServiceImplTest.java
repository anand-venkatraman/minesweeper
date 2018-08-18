package com.pulsepoint.games.minesweeper.service;

import com.pulsepoint.games.minesweeper.dto.*;
import org.junit.Test;

import java.util.Set;
import java.util.stream.IntStream;

import static com.google.common.collect.Iterables.get;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link MineSweeperGameServiceImpl}
 *
 * @author avenkatraman
 */
public class MineSweeperGameServiceImplTest {

    private MineSweeperGameServiceImpl service = new MineSweeperGameServiceImpl();

    @Test
    public void testAllMinesMarked() {
        Game game = setupGame();
        // marking the first 7 mines
        for (int i = 0; i < 7; i++) {
            OpenBoxOperationStatus status = service.markMine(game.getId(), i, i);
            assertEquals(GameStatus.IN_PLAY, status.getStatus());
            assertEquals(1, status.getBoxesAffected().size());
            assertEquals(new Position(i, i), get(status.getBoxesAffected(), 0).getPosition());
            assertEquals(BoxStatus.MARKED_MINE, get(status.getBoxesAffected(), 0).getBox().getStatus());
        }
        // last mine marked
        OpenBoxOperationStatus status = service.markMine(game.getId(), 7, 7);
        assertEquals(GameStatus.WON, status.getStatus());
        assertEquals(1, status.getBoxesAffected().size());
        assertEquals(new Position(7, 7), get(status.getBoxesAffected(), 0).getPosition());
        assertEquals(BoxStatus.MARKED_MINE, get(status.getBoxesAffected(), 0).getBox().getStatus());
    }

    @Test
    public void testGameLostOpeningMine() {
        Game game = setupGame();
        // open a box not having mine
        OpenBoxOperationStatus status = service.openBox(game.getId(), 0, 7);
        assertEquals(GameStatus.IN_PLAY, status.getStatus());
        // open a box with mine
        status = service.openBox(game.getId(), 7, 7);
        assertEquals(GameStatus.LOST, status.getStatus());
    }

    @Test
    public void testAdjacentMineCount() {
        Game game = setupGame();
        // the diagonal cells from (0,0) to (7,7) have mines. this test verifies
        // that the boxes right next to it report the right adjacent counts.
        for (int i = 0; i < 7; i++) {
            OpenBoxOperationStatus status = service.openBox(game.getId(), i, i + 1);
            assertEquals(2, get(status.getBoxesAffected(), 0).getBox().getAdjacentMineCount());
            status = service.openBox(game.getId(), i + 1, i);
            assertEquals(2, get(status.getBoxesAffected(), 0).getBox().getAdjacentMineCount());
        }
    }

    @Test
    public void testEmptyBoxOpensUpAllBoxesAroundIt() {
        Game game = setupGame();
        // the diagonal cells from (0,0) to (7,7) have mines. this test opens (0,7)
        // and verifies if all other empty cells near it get opened.
        // when it reaches cells that have adjacent mines, it should stop.
        // visualization is here: src/test/resources/test-visual.jpg
        OpenBoxOperationStatus status = service.openBox(game.getId(), 0, 7);
        assertEquals(26, status.getBoxesAffected().size());
        Set<Position> positions = status.getBoxesAffected().stream().map(OpenBoxOperationStatus.BoxPosition::getPosition).collect(toSet());
        // verify the right boxes have been opened.
        for (int i = 0; i < 8; i++) {
            for (int j = i + 2; j < 8; j++) {
                assertTrue(positions.contains(new Position(i, j)));
            }
            if (i > 0 && i < 6) {
                assertTrue(positions.contains(new Position(i, i + 1)));
            }
            
        }
    }

    private Game setupGame() {
        Game game = game(8, 8, 8);
        Set<Position> mineLocations = IntStream.range(0, 8).mapToObj(i -> new Position(i, i)).collect(toSet());
        service.addGame(game, mineLocations);
        return game;
    }

    private Game game(int rows, int columns, int mineCount) {
        Board board = new Board().setTotalMines(mineCount).setBoxes(boxes(rows, columns));
        return new Game(randomAlphanumeric(10), board, mineCount);
    }

    private Box[][] boxes(int rows, int columns) {
        return IntStream.range(0, rows)
                .mapToObj(i -> IntStream.range(0, columns)
                        .mapToObj(j -> new Box().setStatus(BoxStatus.CLOSED))
                        .toArray(Box[]::new))
                .toArray(Box[][]::new);
    }

}