package at.kalauner.dezsys06.station;

import at.kalauner.dezsys06.station.connection.ClientSocket;
import at.kalauner.dezsys06.station.dbhandling.DBConnectionCreator;
import at.kalauner.dezsys06.station.dbhandling.DBMSConnection;
import at.kalauner.dezsys06.station.dbhandling.MySQLConnectionCreator;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Main-Class
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160204
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
        if (!parseArgs(args))
            System.exit(0);
    }

    /**
     * Parses the CLI arguments and creates DB-Connection and ClientSocket
     *
     * @param args arguments
     * @return true if arguments are valid
     */
    public static boolean parseArgs(String[] args) {

        Options options = getOptions();
        HelpFormatter hf = new HelpFormatter();
        CommandLineParser parser = new BasicParser();

        try {

            CommandLine cmd = parser.parse(options, args);

            DBConnectionCreator connection = new MySQLConnectionCreator();

            connection.setHost(cmd.getOptionValue("host-dbms"))
                    .setDatabase(cmd.getOptionValue("d"))
                    .setUser(cmd.getOptionValue("u"))
                    .setPassword(cmd.getOptionValue("p"));


            int port = DEFAULT_PORT;
            if (cmd.hasOption("port-tm")) {
                port = ((Number) cmd.getParsedOptionValue("port-tm")).intValue();
            }

            DBMSConnection con = new DBMSConnection(connection.createConnection());
            ClientSocket cs = new ClientSocket(con, cmd.getOptionValue("host-tm", DEFAULT_HOSTNAME), port);
            return true;

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            hf.printHelp("java -jar station.jar", options);
            return false;
        }

    }

    /**
     * Options for CLI args
     *
     * @return Options
     */
    @SuppressWarnings("AccessStaticViaInstance")
    private static Options getOptions() {

        Options options = new Options();

        options.addOption(OptionBuilder
                .hasArg(true)
                .withDescription("Hostname of transaction manager. Default: localhost")
                .withLongOpt("host-tm")
                .create());

        options.addOption(OptionBuilder
                .hasArg(true)
                .withDescription("Port of transaction manager. Default: 12345")
                .withLongOpt("port-tm")
                .withType(Number.class)
                .create());


        options.addOption(OptionBuilder
                .hasArg(true)
                .withDescription("Hostname of the DBMS. Default: localhost")
                .withLongOpt("host-dbms")
                .create());


        options.addOption(OptionBuilder
                .hasArg(true)
                .withDescription("Username of the DBMS. Default: root")
                .withLongOpt("user")
                .create("u"));

        options.addOption(OptionBuilder
                .hasArg(true)
                .withDescription("Password of the DBMS. Default: none")
                .withLongOpt("password")
                .create("p"));

        options.addOption(OptionBuilder
                .hasArg(true)
                .isRequired()
                .withDescription("Name of the DB.")
                .withLongOpt("dbname")
                .create("d"));

        return options;

    }
}
