package org.example;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.*;

public class SSHClient {
    private static final String KEY_PATH = System.getProperty("user.home") + "/Desktop/Tareas DAM/ssh/id_rsa";
    private static final int SERVER_PORT = 5000;
    private static final String SERVER_IP = "127.0.0.1";

    public static void main(String[] args) {
        if (!keyExists() || isKeyExpired()) {
            System.out.println("Generando nueva clave SSH...");
            generateSSHKey();
            sendPublicKeyToServer();
        }
        connectToSSH();
    }

    private static boolean keyExists() {
        return Files.exists(Paths.get(KEY_PATH)) && Files.exists(Paths.get(KEY_PATH + ".pub"));
    }

    private static boolean isKeyExpired() {
        try {
            File keyFile = new File(KEY_PATH);
            Instant lastModified = Instant.ofEpochMilli(keyFile.lastModified());
            return Duration.between(lastModified, Instant.now()).toDays() > 30;
        } catch (Exception e) {
            return true;
        }
    }

    private static void generateSSHKey() {
        try {
            JSch jsch = new JSch();
            KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 2048);
            kpair.writePrivateKey(KEY_PATH);
            kpair.writePublicKey(KEY_PATH + ".pub", "Generated Key");
            kpair.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendPublicKeyToServer() {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String publicKey = Files.readString(Paths.get(KEY_PATH + ".pub"));
            out.println(publicKey);
            System.out.println("Clave pública enviada al servidor.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void connectToSSH() {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(KEY_PATH);
            Session session = jsch.getSession("usuario", "localhost", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            System.out.println("Conectado a SSH correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
