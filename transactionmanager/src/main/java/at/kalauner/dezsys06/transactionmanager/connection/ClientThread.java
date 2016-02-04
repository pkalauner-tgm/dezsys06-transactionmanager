package at.kalauner.dezsys06.transactionmanager.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Represents one Client (Station)
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160204
 */
public class ClientThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(ClientThread.class);
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Initializes the ClientThread with the given socket
     * @param socket socket
     */
    public ClientThread(Socket socket) {
        LOGGER.info("New Client connected");
        this.socket = socket;
        this.initIO();
    }

    /**
     * Sends a command to the client
     * @param cmd the command which should be sent
     */
    public void sendCommand(String cmd) {
        this.out.println(cmd);
    }

    /**
     * Initializes Writer and Reader
     */
    private void initIO() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a received command
     *
     * @param message the received command
     */
    private void handleCommand(String message) {

    }

    /**
     * Waits for commands
     */
    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase("disconnect"))
                    break;
                this.handleCommand(inputLine);
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
