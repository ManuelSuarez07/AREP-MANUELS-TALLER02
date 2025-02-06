package com.eci.webservert1;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

/**
 *
 * @author Manuel S
 */
public class WebServerT1 {

    private static final List<CurrencyConverter> conversions = new ArrayList<>();

    public static void main(String[] args) throws IOException, URISyntaxException {
        try (ServerSocket serverSocket = new ServerSocket(35000)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept(); BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String inputLine = in.readLine();
                    if (inputLine == null) {
                        continue;
                    }

                    String[] requestParts = inputLine.split(" ");
                    String method = requestParts[0];
                    String file = requestParts[1];
                    URI resourceURI = new URI(file);

                    String response = switch (method) {
                        case "GET" ->
                            resourceURI.getPath().startsWith("/convertir")
                            ? handleCurrencyConversion(resourceURI)
                            : obtainFile(resourceURI.getPath(), clientSocket.getOutputStream());
                        default ->
                            """
                               HTTP/1.1 405 Method Not Allowed\r
                               Content-Type: text/plain\r
                               \r
                               """;
                    };

                    out.println(response);
                } catch (Exception e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        }
    }

    private static String handleCurrencyConversion(URI resourceURI) {
        String query = resourceURI.getQuery();
        String[] params = query.split("&");
        double amount = Double.parseDouble(params[0].split("=")[1]);
        String fromCurrency = params[1].split("=")[1];
        String toCurrency = params[2].split("=")[1];

        double result = CurrencyConverter.convert(amount, fromCurrency, toCurrency);

        String response = """
                          HTTP/1.1 200 OK\r
                          Content-Type: application/json\r
                          \r
                          {"amount":""" + amount + ", \"from\":\"" + fromCurrency + "\", \"to\":\"" + toCurrency + "\", \"converted\":" + result + "}";
        return response;
    }

    public static String obtainFile(String path, OutputStream out) throws IOException {
        String file = path.equals("/") ? "index.html" : path.split("/")[1];
        String extension = file.contains(".") ? file.substring(file.lastIndexOf('.') + 1) : "";
        String filePath = "src/main/resources/static/" + file;
        String responseHeader = "HTTP/1.1 200 OK\r\nContent-Type: " + obtainContentType(extension) + "\r\n\r\n";
        String notFoundResponse = "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\n\r\n";

        try {
            if (extension.matches("html|css|js")) {
                return responseHeader + new String(Files.readAllBytes(Paths.get(filePath)));
            } else if (extension.matches("jpg|jpeg|png")) {
                File imageFile = new File(filePath);
                if (imageFile.exists()) {
                    out.write(responseHeader.getBytes());
                    Files.copy(imageFile.toPath(), out);
                    return responseHeader;
                }
            }
        } catch (IOException e) {
            return "HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/plain\r\n\r\n" + e.getMessage();
        }

        return notFoundResponse;
    }

    public static String obtainContentType(String extension) {
        switch (extension) {
            case "html", "css" -> {
                return "text/" + extension;
            }
            case "js" -> {
                return "text/javascript";
            }
            case "jpg", "jpeg" -> {
                return "image/jpeg";
            }
            case "png" -> {
                return "image/png";
            }
            default -> {
            }
        }
        return "text/plain";
    }
}
