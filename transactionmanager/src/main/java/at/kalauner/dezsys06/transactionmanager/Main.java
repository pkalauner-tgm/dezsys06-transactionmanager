package at.kalauner.dezsys06.transactionmanager;

import at.kalauner.dezsys06.transactionmanager.connection.SocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.SystemClock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main-Class
 *
 * @author Paul Kalauner 5BHIT
 * @version 1.0
 */
public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static final int DEFAULT_PORT = 12345;
    public static final String EXIT_COMMAND = "exit";

    /**
     * Main-Method <br>
     *
     * Reads user inputs from the command line
     *
     * @param args CLI-arguments <br>
     *             Port (optional)
     */
    public static void main(String[] args) {
        LOGGER.info("Starting Transactionmanager...");

        int port = DEFAULT_PORT;

        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid port");
            }
        }

        SocketHandler sh = new SocketHandler(port);
        sh.start();


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter some text, or '" + EXIT_COMMAND + "' to quit");
        try {
            while (true) {

                System.out.print("> ");
                String input;
                input = br.readLine();

                if (input.equalsIgnoreCase(EXIT_COMMAND)) {
                    System.out.println("Exiting");
                    LOGGER.info("Exiting");
                    System.exit(0);
                }
                sh.broadcast(input);
            }
        } catch (IOException e) {
            LOGGER.error("Error while reading user inputs");
        }
    }
}
