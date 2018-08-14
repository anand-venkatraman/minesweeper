package com.pulsepoint.games.minesweeper.web;

import com.pulsepoint.games.minesweeper.dto.Game;
import com.pulsepoint.games.minesweeper.service.MineSweeperGameService;
import com.pulsepoint.games.minesweeper.dto.Level;
import com.pulsepoint.games.minesweeper.dto.OpenBoxOperationStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.Set;

/**
 * Webservices for the browser minesweeper game to connect to
 * for {@link Game} related operations.
 * 
 * @author avenkatraman
 */
@ApplicationPath("/")
@Path("/")
public class MinesweeperWebService extends Application {
    
    private MineSweeperGameService service = new MineSweeperGameService();
    
    @GET
    @Path("/game/new")
    @Produces("application/json")
    public Game createGame(@QueryParam("level") Level level) {
        return service.createGame(level);
    }
    
    @GET
    @Path("/game/{id}")
    public Game getGame(@PathParam("id") String gameId) {
        return service.getGame(gameId);
    }
    
    @GET
    @Path("/game/{id}/box")
    public OpenBoxOperationStatus openBox(@PathParam("id") String gameId, @QueryParam("x") int x, @QueryParam("y") int y) {
        return service.openBox(gameId, x, y);
    }
    
    @GET
    @Path("/game/{id}/box/mine")
    public OpenBoxOperationStatus markMine(@PathParam("id") String gameId, @QueryParam("x") int x, @QueryParam("y") int y) {
        return service.markMine(gameId, x, y);
    }

    @Override
    public Set<Object> getSingletons() {
        return Collections.singleton(this);
    }
    
}
