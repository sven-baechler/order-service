package ch.hslu.swda.logging;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.micro.Routes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class LogServiceTest {
    private LogService logService;

    private Logger logger;

    private BusConnector bus;

    @BeforeEach
    void setUp() {
        logger = mock(Logger.class);
        bus = mock(BusConnector.class);
        logService = new LogService("exchangeName", bus, logger);
    }

    @Test
    void testInfo() {
        String format = "This is an info message with a value provided %s";
        String argument = "input to insert";
        Log returnedLog = logService.info(format, argument);

        verify(logger).info(String.format(format, argument));
        verifyNoInteractions(bus);
        assertNotNull(returnedLog);

        returnedLog.send();

        assertDoesNotThrow(() -> verify(bus).talkAsync(
                eq("exchangeName"),
                eq(Routes.LOG_OCCURRED),
                argThat((String s) -> s.contains("\"level\":\"INFO\""))
        ));

        assertDoesNotThrow(() -> verify(bus).talkAsync(
                eq("exchangeName"),
                eq(Routes.LOG_OCCURRED),
                argThat((String s) -> s.contains("\"message\":\"This is an info message with a value provided input to insert\""))
        ));
    }

    @Test
    void testError() {
        String format = "Error message %s";
        String argument = "error input";
        Log returnedLog = logService.error(format, argument);

        verify(logger).error(String.format(format, argument));
        verifyNoInteractions(bus);
        assertNotNull(returnedLog);

        returnedLog.send();

        assertDoesNotThrow(() -> verify(bus).talkAsync(
                eq("exchangeName"),
                eq(Routes.LOG_OCCURRED),
                argThat((String s) -> s.contains("\"level\":\"ERROR\""))
        ));

        assertDoesNotThrow(() -> verify(bus).talkAsync(
                eq("exchangeName"),
                eq(Routes.LOG_OCCURRED),
                argThat((String s) -> s.contains("\"message\":\"Error message error input\""))
        ));
    }


}