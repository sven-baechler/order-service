package ch.hslu.swda.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.services.OrderEntryService;
import ch.hslu.swda.services.logging.LogService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

class OrderEntryUpdatedReceiverTest {
    @Mock
    private BusConnector bus;

    @Mock
    private LogService logService;

    @Mock
    private OrderEntryService orderEntryService;

    @InjectMocks
    private OrderEntryUpdatedReceiver orderEntryUpdatedReceiver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOnMessageReceived() {
        // Arrange
        String route = "testRoute";
        String replyTo = "testReplyTo";
        String corrId = "testCorrId";
        String message = "{\"orderId\": \"1234\", \"articleId\": \"1234\", \"status\": \"READY_TO_DELIVER\"}";

        // Act
        this.orderEntryUpdatedReceiver.onMessageReceived(route, replyTo, corrId, message);

        // Assert
        OrderEntryUpdatedMessage orderEntryUpdatedMessage = new Gson().fromJson(message, OrderEntryUpdatedMessage.class);
        verify(orderEntryService, times(1)).handleOrderEntryUpdated(orderEntryUpdatedMessage);
    }
}
