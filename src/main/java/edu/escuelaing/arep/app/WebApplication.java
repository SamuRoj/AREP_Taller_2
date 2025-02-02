package edu.escuelaing.arep.app;

import java.io.IOException;
import java.net.URISyntaxException;

import static edu.escuelaing.arep.app.HttpServer.get;
import static edu.escuelaing.arep.app.HttpServer.staticFiles;

public class WebApplication {
    public static void main(String[] args) throws IOException, URISyntaxException {
        staticFiles("/static");

        get("/hello", (req, res) -> "hello world!");

        get("/greeting", (req, res) -> {
            return "Hello " + req.getValues("name");
        });

        get("/pi", (req, res) -> {
            return String.valueOf(Math.PI);
        });

        get("/e", (req, res) -> {
            return String.valueOf(Math.E);});

        HttpServer.start(args);
    }
}
