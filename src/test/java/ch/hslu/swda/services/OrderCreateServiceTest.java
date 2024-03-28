package ch.hslu.swda.services;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.messages.OrderCreatedMessage;
import ch.hslu.swda.mongo.MongoService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderCreateServiceTest {

    @Mock
    private MongoService mongoService;

    @InjectMocks
    private OrderCreateService orderCreateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrderWithMultipleEntries() {
        Order order = new Order();
        OrderEntry entry1 = new OrderEntry();
        entry1.setArticleId(new ObjectId());
        entry1.setAmount(5);
        entry1.setPricePerUnit(10.0);
        entry1.setStatus(OrderStatus.ORDERED);

        OrderEntry entry2 = new OrderEntry();
        entry2.setArticleId(new ObjectId());
        entry2.setAmount(10);
        entry2.setPricePerUnit(15.0);
        entry2.setStatus(OrderStatus.ORDERED);

        order.setEntries(Arrays.asList(entry1, entry2));

        OrderCreatedMessage result = orderCreateService.CreateOrder(order);

        assertNotNull(result);
        assertEquals(2, result.getEntries().size());
        verify(mongoService, times(1)).insertOrder(any(Order.class));
    }

    @Test
    void testOrderAndEntriesStatusSetToOrdered() {
        Order order = new Order();
        OrderEntry entry = new OrderEntry();
        entry.setArticleId(new ObjectId());
        entry.setAmount(1);
        entry.setStatus(OrderStatus.ORDERED);
        order.setEntries(List.of(entry));

        orderCreateService.CreateOrder(order);

        assertEquals(OrderStatus.ORDERED, order.getStatus());
        order.getEntries().forEach(
                orderEntry -> assertEquals(OrderStatus.ORDERED, orderEntry.getStatus())
        );
    }

    @Test
    void testOrderCreatedMessageCorrectness() {
        Order order = new Order();
        OrderEntry entry = new OrderEntry();
        entry.setArticleId(new ObjectId());
        entry.setAmount(5);
        order.setEntries(List.of(entry));

        OrderCreatedMessage result = orderCreateService.CreateOrder(order);

        assertNotNull(result.getOrderId());
        assertEquals(1, result.getEntries().size());
        assertEquals(5, result.getEntries().get(0).getAmount());
    }

    @Test
    void testExceptionHandlingOnDatabaseError() {
        doThrow(new RuntimeException("Database error")).when(mongoService).insertOrder(any(Order.class));
        Order order = new Order();

        assertThrows(RuntimeException.class, () -> orderCreateService.CreateOrder(order));
    }
}