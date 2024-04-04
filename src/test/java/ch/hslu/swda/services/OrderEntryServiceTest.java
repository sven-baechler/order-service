package ch.hslu.swda.services;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.logging.Log;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.mongo.MongoService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class OrderEntryServiceTest {

    @Mock
    private MongoService mongoService;

    @Mock
    private LogService logService;

    @Mock
    private Log log;

    @InjectMocks
    private OrderEntryService orderEntryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateOrderEntryStatusThenUpdateOrderStatus() {
        // Arrange
        this.mockLogService();

        ObjectId orderId = new ObjectId("000000000000000000000000");
        ObjectId articleId = new ObjectId("000000000000000000000000");
        OrderStatus status = OrderStatus.READY_TO_DELIVER;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.ORDERED);
        OrderEntry orderEntry1 = new OrderEntry();
        orderEntry1.setArticleId(articleId);
        orderEntry1.setStatus(OrderStatus.ORDERED);
        order.setEntries(Arrays.asList(orderEntry1));

        doReturn(order).when(mongoService).getOrder(orderId);
        doReturn(true).when(mongoService).updateOrder(order);

        OrderEntryUpdatedMessage message = new OrderEntryUpdatedMessage();
        message.setOrderId(orderId.toString());
        message.setArticleId(articleId.toString());
        message.setStatus(status);

        // Act
        orderEntryService.handleOrderEntryUpdated(message);

        // Assert
        verify(mongoService, times(2)).updateOrder(order);

        // TODO: cannot verify invocation of private methods in OrderEntryService ???
    }

    @Test
    void updateOrderEntryStatusDontUpdateOrderStatus() {
        // Arrange
        this.mockLogService();

        ObjectId orderId = new ObjectId("000000000000000000000000");
        ObjectId articleId = new ObjectId("000000000000000000000000");
        OrderStatus status = OrderStatus.READY_TO_DELIVER;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.ORDERED);
        OrderEntry orderEntry1 = new OrderEntry();
        orderEntry1.setArticleId(articleId);
        orderEntry1.setStatus(OrderStatus.ORDERED);
        OrderEntry orderEntry2 = new OrderEntry();
        orderEntry2.setArticleId(new ObjectId("111111111111111111111111"));
        orderEntry2.setStatus(OrderStatus.ORDERED);
        order.setEntries(Arrays.asList(orderEntry1, orderEntry2));

        doReturn(order).when(mongoService).getOrder(orderId);
        doReturn(true).when(mongoService).updateOrder(order);

        OrderEntryUpdatedMessage message = new OrderEntryUpdatedMessage();
        message.setOrderId(orderId.toString());
        message.setArticleId(articleId.toString());
        message.setStatus(status);

        // Act
        orderEntryService.handleOrderEntryUpdated(message);

        // Assert
        verify(mongoService, times(1)).updateOrder(order);

        // TODO: cannot verify invocation of private methods in OrderEntryService ???
    }

    @Test
    void updateOrderEntryStatusNoSuchArticle() {
        // Arrange
        this.mockLogService();

        ObjectId mockOrderId = new ObjectId("000000000000000000000000");
        ObjectId mockArticleId = new ObjectId("000000000000000000000000");
        OrderStatus mockOrderStatus = OrderStatus.ORDERED;
        OrderStatus mockArticleStatus = OrderStatus.ORDERED;
        Order mockOrder = new Order();
        mockOrder.setId(mockOrderId);
        mockOrder.setStatus(mockOrderStatus);
        OrderEntry orderEntry1 = new OrderEntry();
        orderEntry1.setArticleId(mockArticleId);
        orderEntry1.setStatus(mockArticleStatus);
        mockOrder.setEntries(Arrays.asList(orderEntry1));

        doReturn(mockOrder).when(mongoService).getOrder(mockOrderId);
        doReturn(true).when(mongoService).updateOrder(mockOrder);

        ObjectId orderId = new ObjectId("000000000000000000000000");
        ObjectId articleId = new ObjectId("111111111111111111111111");
        OrderStatus articleStatus = OrderStatus.ORDERED;
        OrderEntryUpdatedMessage message = new OrderEntryUpdatedMessage();
        message.setOrderId(orderId.toString());
        message.setArticleId(articleId.toString());
        message.setStatus(articleStatus);

        // Act
        orderEntryService.handleOrderEntryUpdated(message);

        // Assert
        verify(mongoService, times(0)).updateOrder(mockOrder);
        verify(logService, times(1)).error(String.format("could not find article with id %s in order with id %s", articleId, orderId));
    }

    private void mockLogService() {
        doReturn(this.log).when(logService).info(anyString(), any());
        doNothing().when(this.log).send();
    }
}
