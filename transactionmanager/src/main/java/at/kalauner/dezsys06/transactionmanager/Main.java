package at.kalauner.dezsys06.transactionmanager;

import at.kalauner.dezsys06.transactionmanager.connection.SocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main-Class
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160204
 */
public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static final int DEFAULT_PORT = 12345;
    public static final String EXIT_COMMAND = "exit";

    /**
     * Main-Method <br>
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
        TwoPhaseCommitHandler tpch = new TwoPhaseCommitHandler(sh);
        sh.setTpch(tpch);
        sh.start();


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter a query, or '" + EXIT_COMMAND + "' to quit");
        try {
            // Read user inputs
            while (true) {

                System.out.print("> ");
                String input;
                input = br.readLine();

                if (input.equalsIgnoreCase(EXIT_COMMAND)) {
                    System.out.println("Exiting");
                    LOGGER.info("Exiting");
                    System.exit(0);
                }
                // Check if at least one station is connected
                if (sh.getNumberOfClients() > 0) {
                    tpch.startPrepare();
                    // Thread.sleep to see what's going on
                    Thread.sleep(1000);
                    // we send only one query, but a transaction could also contain multiple queries
                    tpch.sendQuery(input);
                    Thread.sleep(1000);
                    tpch.endPrepare();
                } else {
                    LOGGER.error("No clients available!");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while reading user inputs");
        } catch (InterruptedException ie) {
            LOGGER.error("Exception while Thread.sleep()", ie);
        }
    }
}
