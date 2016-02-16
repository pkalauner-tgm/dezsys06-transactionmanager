package at.kalauner.dezsys06.transactionmanager;

import at.kalauner.dezsys06.transactionmanager.connection.SocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles the TwoPhaseCommit
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160204
 */
public class TwoPhaseCommitHandler {
    private static final Logger LOGGER = LogManager.getLogger(TwoPhaseCommitHandler.class);

    private int waited;
    private SocketHandler sh;

    private static final int TIMEOUT_TIME = 60000;

    private volatile int prepareAnswerCounter;
    private volatile int readyCounter;
    private volatile int abortCounter;
    private volatile int ackCounter;
    private volatile int nckCounter;

    /**
     * Initializes the TwoPhaseCommitHandler
     *
     * @param sh SocketHandler to send commands to clients
     */
    public TwoPhaseCommitHandler(SocketHandler sh) {
        this.waited = 0;
        this.sh = sh;
        this.resetCounters();
    }

    /**
     * Handles a received command
     *
     * @param cmd the received command
     */
    public synchronized void handleCommand(String cmd) {
        LOGGER.debug("Received command from client: " + cmd);
        switch (cmd) {
            case "GOT_PREPARE":
                prepareAnswerCounter++;
                break;
            case "READY":
                readyCounter++;
                break;
            case "ABORT":
                abortCounter++;
                break;
            case "ACK":
                ackCounter++;
                break;
            case "NCK":
                nckCounter++;
                break;
        }
    }

    /**
     * Starts the prepare phase
     */
    public void startPrepare() {
        this.sh.broadcast("PREPARE");
    }

    /**
     * Sends a query during the prepare phase
     *
     * @param query query
     */
    public void sendQuery(String query) {
        // Wait for all responses
        while (prepareAnswerCounter < sh.getNumberOfClients() && this.waited < TIMEOUT_TIME) {
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                LOGGER.info("doRollback");
                this.sh.broadcast("ROLLBACK");
            }
        }

        if (this.waited >= TIMEOUT_TIME) {
            LOGGER.info("No answer from all clients after 5 seconds. DoRollback");
            this.sh.broadcast("ROLLBACK");
        } else
            this.sh.broadcast("QUERY " + query);

        this.waited = 0;
    }

    /**
     * Ends the prepare phase
     */
    public void endPrepare() {
        this.sh.broadcast("PREPARE_FINISHED");
        // Wait for all responses
        waitForAllReady();

        if (this.waited >= TIMEOUT_TIME) {
            LOGGER.info("No answer from all clients after 5 seconds. DoRollback");
            this.sh.broadcast("ROLLBACK");
        } else {
            LOGGER.info(readyCounter + "xREADY " + abortCounter + "xABORT ");
            commitPhase();
        }

        this.waited = 0;
    }

    /**
     * Starts the commit Phase
     */
    private void commitPhase() {
        if (readyCounter == sh.getNumberOfClients()) {
            LOGGER.info("doCommit");
            this.sh.broadcast("COMMIT");
        } else {
            LOGGER.info("doRollback");
            this.sh.broadcast("ROLLBACK");
        }

        //Wait for all responses
        waitForAllAck();

        if (this.waited >= TIMEOUT_TIME) {
            LOGGER.info("No answer from all clients after 5 seconds. DoRollback");
            this.sh.broadcast("ROLLBACK");
        } else {
            LOGGER.info(ackCounter + "xACK " + nckCounter + "xNCK ");
            this.resetCounters();
        }
        this.waited = 0;
    }

    private void wait(int timeout) throws InterruptedException {
        Thread.sleep(timeout);
        this.waited += timeout;
    }

    private void waitForAllReady() {
        while ((readyCounter + abortCounter) < sh.getNumberOfClients() && this.waited < TIMEOUT_TIME) {
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                LOGGER.info("doRollback");
                this.sh.broadcast("ROLLBACK");
            }
        }
    }

    private void waitForAllAck() {
        while ((ackCounter + nckCounter) < sh.getNumberOfClients() && this.waited < TIMEOUT_TIME) {
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                LOGGER.info("doRollback");
                this.sh.broadcast("ROLLBACK");
            }
        }
    }

    /**
     * Resets the counters
     */
    private void resetCounters() {
        this.prepareAnswerCounter = 0;
        this.readyCounter = 0;
        this.abortCounter = 0;
        this.ackCounter = 0;
        this.nckCounter = 0;
    }
}
