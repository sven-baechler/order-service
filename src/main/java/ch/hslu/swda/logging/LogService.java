package ch.hslu.swda.logging;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.messages.LogMessage;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class LogService {
    private static final String SOURCE_NAME = "g03-order-service";
    private final String exchangeName;
    private final BusConnector bus;
    private final Logger logger;

    private String correlationId;

    public LogService(String exchangeName, BusConnector bus, Logger logger) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logger = logger;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Log info(String format, Object... arguments) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info(String.format(format, arguments));
        }

        return this.log(Level.INFO, format, arguments);
    }

    public Log error(String format, Object... arguments) {
        if (this.logger.isErrorEnabled()) {
            this.logger.error(String.format(format, arguments));
        }

        return this.log(Level.ERROR, format, arguments);
    }

    private Log log(Level level, String format, Object... arguments) {
        LogMessage logMessage = new LogMessage();
        logMessage.setLevel(level);
        logMessage.setMessage(String.format(format, arguments));
        logMessage.setSource(SOURCE_NAME);

        if (this.correlationId != null) {
            logMessage.setCorrelationId(this.correlationId);
        }

        return new Log(logMessage, exchangeName, bus);
    }
}
