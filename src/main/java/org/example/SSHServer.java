package org.example;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class SSHServer {
    private static final int SERVER_PORT = 5000;
    private static final String AUTHORIZED_KEYS_PATH = System.getProperty("user.home") + "/ssh/authorized_keys";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Servidor SSH esperando conexiones en el puerto " + SERVER_PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     BufferedWriter writer = new BufferedWriter(new FileWriter(AUTHORIZED_KEYS_PATH, true))) {

                    String clavePublica = in.readLine();
                    if (clavePublica != null && !clavePublica.isEmpty()) {
                        writer.write(clavePublica + "\n");
                        writer.flush();
                        System.out.println("Clave pública recibida y almacenada.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
