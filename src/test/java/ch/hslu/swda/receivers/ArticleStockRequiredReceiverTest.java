package ch.hslu.swda.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.dto.OrdersAssortmentUpdateMessageOrderEntry;
import ch.hslu.swda.messages.ArticleStockUpdateRequiredMessage;
import ch.hslu.swda.messages.AssortmentUpdatedMessage;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.services.ArticleStockRequiredService;
import ch.hslu.swda.services.OrderEntryService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class ArticleStockRequiredReceiverTest {

    @Mock
    private BusConnector bus;

    @Mock
    private OrderEntryService orderEntryService;

    @Mock
    private ArticleStockRequiredService articleStockRequiredService;

    @InjectMocks
    private ArticleStockRequiredReceiver articleStockRequiredReceiver;


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
        String messageJson = "{\"articleId\":\"12345\",\"amount\":10}";

        ArticleStockUpdateRequiredMessage expectedMessage = new Gson().fromJson(messageJson, ArticleStockUpdateRequiredMessage.class);
        OrdersAssortmentUpdateMessageOrderEntry entry = new OrdersAssortmentUpdateMessageOrderEntry("order1", 5);
        AssortmentUpdatedMessage expectedAssortmentUpdated = new AssortmentUpdatedMessage("12345", 10, List.of(entry));
        when(articleStockRequiredService.checkArticleStockRequired(expectedMessage)).thenReturn(expectedAssortmentUpdated);

        String reply = "{ \"orderEntryUpdatedMessages\": [{ \"orderId\": \"order1\", \"articleId\": \"12345\", \"status\": \"READY_TO_DELIVER\" }] }";
        when(assertDoesNotThrow(() -> bus.talkSync(null, "orders.assortment_updated", new Gson().toJson(expectedAssortmentUpdated)))).thenReturn(reply);

        // Act
        articleStockRequiredReceiver.onMessageReceived(route, replyTo, corrId, messageJson);

        // Assert
        verify(articleStockRequiredService).checkArticleStockRequired(expectedMessage);
        assertDoesNotThrow(() -> {
            verify(bus).talkSync(null, "orders.assortment_updated", new Gson().toJson(expectedAssortmentUpdated));
        });
        verify(orderEntryService, times(1)).handleOrderEntryUpdated(any(OrderEntryUpdatedMessage.class));
    }

    @Test
    void testOnMessageReceivedDoesCatchException() {
        // Arrange
        String route = "testRoute";
        String replyTo = "testReplyTo";
        String corrId = "testCorrId";
        String messageJson = "{\"articleId\":\"12345\",\"amount\":10}";

        ArticleStockUpdateRequiredMessage expectedMessage = new Gson().fromJson(messageJson, ArticleStockUpdateRequiredMessage.class);
        AssortmentUpdatedMessage expectedAssortmentUpdated = new AssortmentUpdatedMessage("12345", 10, Collections.emptyList());

        when(articleStockRequiredService.checkArticleStockRequired(expectedMessage)).thenReturn(expectedAssortmentUpdated);

        when(assertDoesNotThrow(() -> bus.talkSync(null, "orders.assortment_updated", new Gson().toJson(expectedAssortmentUpdated)))).thenThrow(new IOException("Test IO Exception"));

        // Act & Assert
        assertDoesNotThrow(() -> articleStockRequiredReceiver.onMessageReceived(route, replyTo, corrId, messageJson));

        verify(articleStockRequiredService).checkArticleStockRequired(expectedMessage);
        verify(orderEntryService, times(0)).handleOrderEntryUpdated(any(OrderEntryUpdatedMessage.class));
    }

}
