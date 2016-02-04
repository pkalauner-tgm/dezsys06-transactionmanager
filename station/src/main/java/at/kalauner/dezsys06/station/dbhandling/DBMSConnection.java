package at.kalauner.dezsys06.station.dbhandling;

import at.kalauner.dezsys06.station.enums.CommitStatus;
import at.kalauner.dezsys06.station.enums.PrepareStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Responsible for DBMS actions
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160204
 */
public class DBMSConnection {
    private static final Logger LOGGER = LogManager.getLogger(DBMSConnection.class);
    private PrepareStatus ps;

    private Connection con;

    public DBMSConnection(Connection connection) {
        this.con = connection;
    }


    public boolean canCommit() {
        try {
            this.con.setAutoCommit(false);
            this.con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            this.con.commit();
            this.con.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    public void doPrepare() {
        try {
            LOGGER.info("Starting preparation phase");
            this.con.setAutoCommit(false);
            this.con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            this.ps =  PrepareStatus.READY;
        } catch (SQLException e) {
            LOGGER.error("Starting preparation phase failed");
            this.ps = PrepareStatus.ABORT;
        }
    }

    public void executeQuery(String query) {
        try {
            LOGGER.info("Executing query: " + query);
            this.con.prepareStatement(query).execute();
            this.ps =  PrepareStatus.READY;
        } catch (SQLException e) {
            LOGGER.error("Executing query failed: " + e.getMessage());
            this.ps = PrepareStatus.ABORT;
        }
    }

    public PrepareStatus finishPrepare() {
        LOGGER.info("Preparation status: " + this.ps);
        return this.ps;
    }

    public CommitStatus doCommit() {
        try {
            this.con.commit();
            this.con.setAutoCommit(true);
            LOGGER.info("Transaction committed");
            return CommitStatus.ACK;
        } catch (SQLException e) {
            LOGGER.error("Transaction not comitted");
            return CommitStatus.NCK;
        }
    }

    public CommitStatus doRollback() {
        try {
            this.con.rollback();
            this.con.setAutoCommit(true);
            LOGGER.info("Rollback successful");
            return CommitStatus.ACK;
        } catch (SQLException e) {
            LOGGER.error("Rollback failed");
            return CommitStatus.NCK;
        }
    }
}
