package com.pulsepoint.games.minesweeper.runner;

import com.pulsepoint.games.minesweeper.web.MinesweeperWebService;
import org.eclipse.jetty.server.Server;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;

/**
 * Runner for the app. Spins up a Jetty server, that handles the 
 * web service calls for the game and also static content from the 
 * `resources` folder like html, js, css files for the game to run
 * on the browser.
 * 
 * @author avenkatraman
 */
public class MinesweeperAppRunner {

    public static void main(String[] args) throws Exception {
        // Initialize Jetty server
        Server jettyServer = new Server(9000);
        ServletContextHandler contextHandler = new ServletContextHandler(jettyServer, "/*");
        // Jersey servlet for service requests
        MinesweeperWebService service = new MinesweeperWebService();
        ServletContainer jerseyServlet = new ServletContainer(ResourceConfig.forApplication(service).register(JacksonFeature.class));
        contextHandler.addServlet(new ServletHolder(jerseyServlet), "/service/*");
        // Default servlet for static files (from /src/main/resources dir)
        ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
        defaultServlet.setInitParameter("resourceBase", new File(System.getProperty("user.dir"), "/src/main/resources/").getPath());
        defaultServlet.setInitParameter("maxCacheSize", "0");
        defaultServlet.setInitParameter("cacheControl", "no-cache");
        defaultServlet.setInitParameter("etags", "false");
        contextHandler.addServlet(defaultServlet, "/");
        // Start the jetty server
        jettyServer.start();
        jettyServer.join();
    }

}
