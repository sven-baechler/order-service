package ch.hslu.swda.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.messages.OrderCreatedMessage;
import ch.hslu.swda.micro.Routes;
import ch.hslu.swda.services.OrderCreateService;
import ch.hslu.swda.services.logging.LogService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderReceiverTest {
    @Mock
    private BusConnector bus;

    @Mock
    private LogService logService;

    @Mock
    private OrderCreateService orderCreateService;

    @InjectMocks
    private OrderReceiver orderReceiver;

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
        String message = "{\"order\": {\"branchOfficeId\": {\"$oid\": \"000000000000000000000000\"}, \"sellerId\": {\"$oid\": \"000000000000000000000000\"}, \"customer\": {\"id\": {\"$oid\": \"000000000000000000000000\"}, \"name\": \"\", \"preName\": \"\", \"street\": \"\", \"zip\": \"\", \"city\": \"\"}, \"datetime\": null, \"status\": null, \"entries\": [{\"articleId\": {\"$oid\": \"000000000000000000000000\"}, \"articleName\": \"\", \"amount\": 0, \"pricePerUnit\": 0}]}}";

        OrderCreatedMessage orderCreatedMessage = new OrderCreatedMessage("1234", List.of());
        doReturn(orderCreatedMessage).when(orderCreateService).createOrder(any());

        // Act
        this.orderReceiver.onMessageReceived(route, replyTo, corrId, message);

        // Assert
        String expectedMessage = new Gson().toJson(orderCreatedMessage);

        verify(this.logService).info("received message of type: {}", route);
        assertDoesNotThrow(() -> verify(this.bus).talkAsync(null, Routes.ORDER_CREATED, expectedMessage));
    }
}
