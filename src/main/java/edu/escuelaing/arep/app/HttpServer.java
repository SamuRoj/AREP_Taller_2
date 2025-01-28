package edu.escuelaing.arep.app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class HttpServer {

    private static HashMap<String, String> activities = new HashMap<>();

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = new ServerSocket(23727);
        boolean isRunning = true;
        while(isRunning){
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine = "";
            boolean isFirstLine = true;
            String outputLine = "";
            String file = "";
            String method = "";

            while((inputLine = in.readLine()) != null){
                if(isFirstLine){
                    method = inputLine.split(" ")[0];
                    file = inputLine.split(" ")[1];
                    isFirstLine = false;
                }
                if (inputLine.isEmpty()) break;
            }

            URI resourceURI = new URI(file);
            if(method.equals("GET")){
                outputLine = obtainFileRest(resourceURI.getPath(), clientSocket.getOutputStream());
            }
            else if(method.equals("POST")){
                String time = resourceURI.getQuery().split("&")[0].split("=")[1];
                String activity = resourceURI.getQuery().split("&")[1].split("=")[1];
                activities.put(time, activity);
                outputLine = "HTTP/1.1 201 Accepted\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n";
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String obtainFileRest(String path, OutputStream out) throws IOException {
        String file = path.equals("/") ? "index.html" : path.split("/")[1];
        String extension = file.split("\\.")[1];
        String header = obtainContentType(extension);
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + header + "\r\n" +
                "\r\n";
        String notFound = "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: text/plain\r\n"
                + "\r\n";
        String inputLine;
        StringBuilder fileContent = new StringBuilder();
        if(extension.equals("html") || extension.equals("css") || extension.equals("js")){
            try {
                BufferedReader in = new BufferedReader(new FileReader(("src/main/resources/static/" + file)));
                while((inputLine = in.readLine()) != null){
                    fileContent.append(inputLine).append("\n");
                    if (!in.ready()) break;
                }
                in.close();
            } catch (FileNotFoundException e) {
                return notFound;
            }
        }
        else{
            out.write(response.getBytes());
            File imageFile = new File("src/main/resources/static/" + file);
            if(imageFile.exists()){
                FileInputStream fis = new FileInputStream(imageFile);
                byte[] imageBytes = new byte[(int) imageFile.length()];
                fis.read(imageBytes);
                out.write(imageBytes);
                return response;
            }
            return notFound;
        }
        return  response + fileContent;
    }

    private static String obtainContentType(String extension){
        if(extension.equals("html") || extension.equals("css")) return "text/" + extension;
        else if(extension.equals("js")) return "text/javascript";
        else if(extension.equals("jpg") || extension.equals("jpeg")) return "image/jpeg";
        else if(extension.equals("png")) return "image/png";
        return "text/plain";
    }
}
