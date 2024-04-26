package ch.hslu.swda.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.services.GetOrderListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class OrderListRequestedReceiverTest {
    @Mock
    private BusConnector bus;

    @Mock
    private GetOrderListService getOrderListService;

    @InjectMocks
    private OrderListRequestedReceiver orderListRequestedReceiver;

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
        String message = "";

        String fakeJsonResponse = "{ this is just some random string }";
        when(getOrderListService.getJsonOrderList()).thenReturn(fakeJsonResponse);

        // Act
        this.orderListRequestedReceiver.onMessageReceived(route, replyTo, corrId, message);

        // Assert
        verify(getOrderListService, times(1)).getJsonOrderList();
        assertDoesNotThrow(() -> verify(bus).reply(null, replyTo, corrId, fakeJsonResponse));
    }
}
