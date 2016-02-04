package at.kalauner.dezsys06.station.dbhandling;

import java.sql.Connection;

/**
 * Created by Paul on 04.02.16.
 */
public class DBMSConnection {
    private Connection connection;

    public DBMSConnection(Connection connection) {
        this.connection = connection;
    }
}
