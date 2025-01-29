package edu.escuelaing.arep.app;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HttpServer {

    private static List<Activity> activities = new ArrayList<>();

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
                if (!in.ready()) break;
            }

            URI resourceURI = new URI(file);
            if(method.equals("GET")){
                if(resourceURI.getPath().startsWith("/api/activity")) outputLine = obtainActivities();
                else outputLine = obtainFile(resourceURI.getPath(), clientSocket.getOutputStream());
            }
            else if(method.equals("POST")){
                String time = resourceURI.getQuery().split("&")[0].split("=")[1];
                String activity = resourceURI.getQuery().split("&")[1].split("=")[1];
                activities.add(new Activity(time, activity));
                outputLine = "HTTP/1.1 201 Accepted\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n";
            }
            else if(method.equals("DELETE")){
                String time = resourceURI.getQuery().split("=")[1];
                Predicate<Activity> condition = activity -> activity.getTime().equals(time);
                activities.removeIf(condition);
                outputLine = "HTTP/1.1 201 Accepted\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n";
            }
            else{
                outputLine = "HTTP/1.1 405 Method Now Allowed\r\n"
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

    private static String obtainFile(String path, OutputStream out) throws IOException {
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
                return  response + fileContent;
            } catch (FileNotFoundException e) {
                return notFound;
            }
        }
        else if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png")){
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
        else return notFound;
    }

    private static String obtainActivities(){
        StringBuilder json = new StringBuilder();
        json.append("[");

        boolean first = true;
        for (Activity a : activities) {
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append("{")
                    .append("\"time\": \"").append(a.getTime()).append("\", ")
                    .append("\"activity\": \"").append(a.getName()).append("\"")
                    .append("}");
        }

        json.append("]");
        return"HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                json;
    }

    private static String obtainContentType(String extension){
        if(extension.equals("html") || extension.equals("css")) return "text/" + extension;
        else if(extension.equals("js")) return "text/javascript";
        else if(extension.equals("jpg") || extension.equals("jpeg")) return "image/jpeg";
        else if(extension.equals("png")) return "image/png";
        return "text/plain";
    }
}
