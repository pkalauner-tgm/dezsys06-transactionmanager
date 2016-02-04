package at.kalauner.dezsys06.station;

import at.kalauner.dezsys06.station.connection.ClientSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main-Class
 *
 * @author Paul Kalauner 5BHIT
 * @version 1.0
 */
public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final String DEFAULT_HOSTNAME = "127.0.0.1";
    private static final int DEFAULT_PORT = 12345;


    /**
     * Main-Method
     *
     * @param args CLI-arguments <br>
     *             Hostname and port (optional)
     */
    public static void main(String[] args) {
        LOGGER.info("Starting Station...");

        String hostname = DEFAULT_HOSTNAME;
        int port = DEFAULT_PORT;

        if (args.length >= 2) {
            hostname = args[0];
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid port");
            }
        }

        ClientSocket cs = new ClientSocket(hostname, port);
    }
}
