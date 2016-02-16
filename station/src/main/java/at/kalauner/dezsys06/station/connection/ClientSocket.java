package at.kalauner.dezsys06.station.connection;

import at.kalauner.dezsys06.station.dbhandling.DBMSConnection;
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

    private String hostname;
    private int port;
    private BufferedReader in;
    private PrintWriter out;
    private DBMSConnection dbcon;

    /**
     * Initializes the ClientSocket
     *
     * @param dbcon    DBConnection
     * @param hostname hostname of the server
     * @param port     port of the server
     */
    public ClientSocket(DBMSConnection dbcon, String hostname, int port) {
        this.dbcon = dbcon;
        this.hostname = hostname;
        this.port = port;
        this.connect();
    }

    private void connect() {
        try {
            this.socket = new Socket(hostname, port);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            LOGGER.info("Connected to transaction manger on " + hostname + ":" + port);
            this.startListening();
        } catch (IOException e) {
            LOGGER.error("Error while connecting to transaction manager");
        }
    }

    /**
     * Sends a message to the server
     *
     * @param message the message which should be sent
     */
    public void sendMessage(String message) {
        LOGGER.debug("Sending message to transcation manager: " + message);
        this.out.println(message);
    }

    /**
     * Handles the received command
     *
     * @param command the received command
     */
    private void handleCommand(String command) {
        LOGGER.debug("Received command from transaction manager: " + command);
        String[] arr = command.split(" ", 2);
        String cmd = arr[0];
        String param = null;
        if (arr.length >= 2)
            param = arr[1];

        switch (cmd) {
            case "PREPARE":
                dbcon.doPrepare();
                sendMessage("GOT_PREPARE");
                break;
            case "QUERY":
                dbcon.executeQuery(param);
                break;
            case "PREPARE_FINISHED":
                sendMessage(dbcon.finishPrepare().toString());
                break;
            case "COMMIT":
                sendMessage(dbcon.doCommit().toString());
                break;
            case "ROLLBACK":
                sendMessage(dbcon.doRollback().toString());
                break;

        }
    }

    /**
     * Starts to listen from the server in a loop
     */
    public void startListening() {
        String fromServer;
        try {
            while (true) {
                while ((fromServer = in.readLine()) != null) {
                    this.handleCommand(fromServer);
                }
                LOGGER.error("Connection to Transactionmanager lost. Trying to connect again...");
                // Try to reconnect every 20 seconds
                Thread.sleep(20000);
                this.connect();
            }
        } catch (IOException e) {
            LOGGER.error("Exception while listening to server");
        } catch (InterruptedException ie) {
            LOGGER.error("Exception while Thread.sleep()");
        }
    }
}
