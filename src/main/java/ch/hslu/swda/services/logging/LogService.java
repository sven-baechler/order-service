package ch.hslu.swda.services.logging;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.messages.LogMessage;
import ch.hslu.swda.micro.Routes;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.io.IOException;
import java.time.Instant;

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

    public void info(String format, Object... arguments)
    {
        if (!logger.isInfoEnabled()) {
            return;
        }
        LogMessage infoLogMessage = createLogMessage(Level.INFO, format, arguments);
        logger.info(infoLogMessage.getMessage());
        log(infoLogMessage);
    }
    public void error( String format, Object... arguments)
    {
        if (!logger.isErrorEnabled()) {
            return;
        }
        LogMessage errorLogMessage = createLogMessage(Level.ERROR, format, arguments);
        logger.error(errorLogMessage.getMessage());
        log(errorLogMessage);
    }

    public void log(LogMessage logMessage)
    {
        logMessage.setTimestamp(Instant.now().getEpochSecond());
        try {
            bus.talkAsync(exchangeName, Routes.LOG_OCCURRED, logMessage.toJson());
        } catch (IOException ignored) {}
    }

    private LogMessage createLogMessage(Level level, String format, Object... arguments) {
        LogMessage logMessage = new LogMessage();
        logMessage.setLevel(level);
        logMessage.setMessage(String.format(format, arguments));
        logMessage.setSource(SOURCE_NAME);

        if (this.correlationId != null) {
            logMessage.setCorrelationId(this.correlationId);
        }

        return logMessage;
    }

}
