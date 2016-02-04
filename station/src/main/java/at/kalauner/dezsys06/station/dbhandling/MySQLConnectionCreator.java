package at.kalauner.dezsys06.station.dbhandling;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Diese Klasse erweitert den DBConnectionCreator und implementiert die Fabrikmethode zum Erstellen einer Connection.
 * <p>
 * Hinweis: Diese Klasse wurde von einer frueheren SEW-Aufgabe vom 4. Jahrgang wiederverwendet.
 * </p>
 *
 * @author Paul Kalauner, Ritter Mathias 4AHIT
 * @version 20141226.1
 */
public class MySQLConnectionCreator extends DBConnectionCreator {

    private static final Logger LOGGER = LogManager.getLogger(MySQLConnectionCreator.class);

    /**
     * Im Konstruktor wird der Treiber fuer JDB geladen
     */
    public MySQLConnectionCreator() {
        try {
            //Laden des MySQL-Treibers
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            //Falls nicht gefunden, Fehlermeldung ausgeben und Programm verlassen
            LOGGER.error("MySQL-Treiber fuer JDBC nicht gefunden.");
            LOGGER.error(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * @see DBConnectionCreator#createConnection()
     */
    public Connection createConnection() {
        Connection out = null;

        //Connection-String speziell fuer MySQL
        String connectionString = "jdbc:mysql://" + super.getHost() + "/" + super.getDatabase();
        LOGGER.info("Creating MySQLConnection: " + connectionString);
        try {
            //Neue Connection mittels Driver-Manager initialisieren
            out = DriverManager.getConnection(connectionString, super.getUser(), super.getPassword());
        } catch (SQLException e) {
            //Bei nicht erfolgreichem Verbindungsaufbau Fehler ausgeben und Programm verlasen
            LOGGER.error("Verbindung zu DB fehlgeschlagen. Angegebene Daten:");
            LOGGER.error("hostname: " + super.getHost());
            LOGGER.error("database: " + super.getDatabase());
            LOGGER.error("username: " + super.getUser());
            LOGGER.error("passwort: " + super.getPassword());
            System.exit(-1);
        }

        return out;
    }
}
