package edu.escuelaing.arep.app;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void shouldObtainFile() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            String response = HttpServer.obtainFile("/wallpaper.jpeg", outputStream);
            assertEquals("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: image/jpeg\r\n" +
                    "\r\n", response);
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void shouldNotObtainFile(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            HttpServer.obtainFile("/page.html", outputStream);

        } catch (IOException e) {
            assertTrue(true);
        }
    }

    @Test
    public void shouldObtainHtml(){
        String extension = HttpServer.obtainContentType("html");
        assertEquals("text/html", extension);
    }

    @Test
    public void shouldObtainCss(){
        String extension = HttpServer.obtainContentType("css");
        assertEquals("text/css", extension);
    }

    @Test
    public void shouldObtainHJs(){
        String extension = HttpServer.obtainContentType("js");
        assertEquals("text/javascript", extension);
    }

    @Test
    public void shouldObtainImage(){
        String extension = HttpServer.obtainContentType("jpg");
        assertEquals("image/jpeg", extension);
    }
}
