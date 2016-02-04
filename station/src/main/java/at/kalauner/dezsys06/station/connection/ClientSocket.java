package at.kalauner.dezsys06.station.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Connection to Server
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160204
 */
public class ClientSocket {
    private static final Logger LOGGER = LogManager.getLogger(ClientSocket.class);

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Initializes the ClientSocket
     *
     * @param hostname hostname of the server
     * @param port     port of the server
     */
    public ClientSocket(String hostname, int port) {
        try {
            this.socket = new Socket(hostname, port);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            LOGGER.error("Error while connecting to server", e);
        }
    }

    /**
     * Sends a message to the server
     *
     * @param message the message which should be sent
     */
    public void sendMessage(String message) {
        this.out.println(message);
    }

    /**
     * Handles the received command
     *
     * @param command the received command
     */
    private void handleCommand(String command) {
        LOGGER.info("Received command: " + command);

    }

    /**
     * Starts to listen from the server in a loop
     */
    public void startListening() {
        String fromServer;
        try {
            while ((fromServer = in.readLine()) != null) {
                this.handleCommand(fromServer);
            }
            this.socket.close();
        } catch (IOException e) {
            LOGGER.error("Exception while listening to server", e);
        }
    }
}
