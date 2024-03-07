package ch.hslu.swda.logging;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.messages.LogMessage;
import ch.hslu.swda.micro.Routes;

import java.io.IOException;
import java.time.Instant;

public class Log {
    private final LogMessage logMessage;
    private final String exchangeName;
    private final BusConnector bus;

    public Log(LogMessage logMessage, String exchangeName, BusConnector bus) {
        this.logMessage = logMessage;
        this.exchangeName = exchangeName;
        this.bus = bus;
    }

    public Log withCorrelationId(String correlationId) {
        logMessage.setCorrelationId(correlationId);

        return this;
    }

    public Log withCustomerId(String customerId) {
        logMessage.setCustomerId(customerId);

        return this;
    }

    public Log withUserId(String userId) {
        logMessage.setUserId(userId);

        return this;
    }

    public void send() {
        this.logMessage.setTimestamp(Instant.now().getEpochSecond());

        try {
            bus.talkAsync(exchangeName, Routes.LOG_OCCURRED, this.logMessage.toJson());
        } catch (IOException ignored) {}
    }
}
