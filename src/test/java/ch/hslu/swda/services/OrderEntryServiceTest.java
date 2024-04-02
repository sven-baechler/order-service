package ch.hslu.swda.services;

import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.mongo.MongoService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class OrderEntryServiceTest {

    @Mock
    private MongoService mongoService;

    @InjectMocks
    private OrderEntryService orderEntryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateOrderEntryThenUpdateOrderStatus() {
        // Arrange
        String orderId = new ObjectId().toString();
        String articleId = new ObjectId().toString();
        OrderStatus status = OrderStatus.DELIVERED;
        doReturn(true).when(mongoService).updateOrderEntryStatus(new ObjectId(orderId), new ObjectId(articleId), status);

        OrderEntryUpdatedMessage message = new OrderEntryUpdatedMessage();
        message.setOrderId(orderId);
        message.setArticleId(articleId);
        message.setStatus(status);

        // Act
        orderEntryService.handleOrderEntryUpdated(message);

        // Assert
        verify(mongoService, times(1)).updateOrderEntryStatus(new ObjectId(orderId), new ObjectId(articleId), status);
        verify(mongoService, times(1)).updateOrderStatus(new ObjectId(orderId));
    }

    @Test
    void updateOrderEntryDontUpdateOrderStatus() {
        // Arrange
        String orderId = new ObjectId().toString();
        String articleId = new ObjectId().toString();
        OrderStatus status = OrderStatus.DELIVERED;
        doReturn(false).when(mongoService).updateOrderEntryStatus(new ObjectId(orderId), new ObjectId(articleId), status);

        OrderEntryUpdatedMessage message = new OrderEntryUpdatedMessage();
        message.setOrderId(orderId);
        message.setArticleId(articleId);
        message.setStatus(status);

        // Act
        orderEntryService.handleOrderEntryUpdated(message);

        // Assert
        verify(mongoService, times(1)).updateOrderEntryStatus(new ObjectId(orderId), new ObjectId(articleId), status);
        verify(mongoService, times(0)).updateOrderStatus(new ObjectId(orderId));
    }
}
