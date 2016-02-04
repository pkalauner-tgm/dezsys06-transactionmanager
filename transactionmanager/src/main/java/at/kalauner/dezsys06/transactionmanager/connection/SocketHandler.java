package at.kalauner.dezsys06.transactionmanager.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles new incoming connections
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160204
 */
public class SocketHandler extends Thread{
    private static final Logger LOGGER = LogManager.getLogger(SocketHandler.class);
    private ServerSocket serverSocket;
    private Set<ClientThread> clients;

    /**
     * Initializes the ServerSocket
     *
     * @param port port
     */
    public SocketHandler(int port) {
        LOGGER.info("Creating ServerSocket");
        this.clients = new HashSet<>();
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOGGER.error("Error while creating ServerSocket", e);
        }
    }

    /**
     * Waits for new connections
     */
    @Override
    public void run() {
        try {
            while (true) {
                ClientThread ct = new ClientThread(serverSocket.accept());
                clients.add(ct);
                ct.start();
            }
        } catch (IOException e) {
            LOGGER.error("Exception while creating new ClientThread", e);
        }
    }

    /**
     * Sends a message to all clients
     *
     * @param message the message which should be sent
     */
    public void broadcast(String message) {
        for (ClientThread cur : clients)
            cur.sendCommand(message);
    }
}
