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

    private SocketHandler sh;

    private volatile int prepareAnswerCounter;
    private volatile int readyCounter;
    private volatile int abortCounter;
    private volatile int timeoutCounter;
    private volatile int ackCounter;
    private volatile int nckCounter;

    public TwoPhaseCommitHandler(SocketHandler sh) {
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

    public void startPrepare() {
        this.sh.broadcast("PREPARE");
    }

    public void sendQuery(String query) {
        while (prepareAnswerCounter < sh.getNumberOfClients());
        this.sh.broadcast("QUERY " + query);
    }

    public void endPrepare() {
        this.sh.broadcast("PREPARE_FINISHED");
        while ((readyCounter + abortCounter + timeoutCounter) < sh.getNumberOfClients());
        LOGGER.info(readyCounter + "xREADY " + abortCounter + "xABORT " + timeoutCounter + "xTIMEOUT");
        timeoutCounter = 0;
        commitPhase();
    }

    private void commitPhase() {
        if (readyCounter == sh.getNumberOfClients()) {
            LOGGER.info("doCommit");
            this.sh.broadcast("COMMIT");
        } else {
            LOGGER.info("doRollback");
            this.sh.broadcast("ROLLBACK");
        }

        while ((ackCounter + nckCounter + timeoutCounter) < sh.getNumberOfClients());
        LOGGER.info(ackCounter + "xACK " + nckCounter + "xNCK " + timeoutCounter + "xTIMEOUT");
        resetCounters();
    }


    public void resetCounters() {
        this.prepareAnswerCounter = 0;
        this.readyCounter = 0;
        this.abortCounter = 0;
        this.timeoutCounter = 0;
        this.ackCounter = 0;
        this.nckCounter = 0;
    }
}
