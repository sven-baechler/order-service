package ch.hslu.swda.services;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.services.logging.LogService;
import ch.hslu.swda.messages.ArticleStockUpdateRequiredMessage;
import ch.hslu.swda.messages.AssortmentUpdatedMessage;
import ch.hslu.swda.services.mongo.MongoService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleStockRequiredServiceTest {
    final private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Mock
    private MongoService mongoService;

    @Mock
    private LogService logService;

    @InjectMocks
    private ArticleStockRequiredService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testArticleStockRequiredWithMultipleOrders() {
        // arrange
        ObjectId articleId = new ObjectId();
        ArticleStockUpdateRequiredMessage message = new ArticleStockUpdateRequiredMessage(articleId.toString(), 10);

        Order order = new Order();
        order.setId(new ObjectId());
        Date orderDate = assertDoesNotThrow(() -> formatter.parse("2024-02-01"));
        order.setDatetime(orderDate);
        order.setStatus(OrderStatus.ORDERED);

        OrderEntry firstEntry = new OrderEntry();
        firstEntry.setArticleId(articleId);
        firstEntry.setAmount(4);
        firstEntry.setPricePerUnit(10.0);
        firstEntry.setStatus(OrderStatus.ORDERED);

        OrderEntry secondEntry = new OrderEntry();
        secondEntry.setArticleId(new ObjectId());
        secondEntry.setAmount(8);
        secondEntry.setPricePerUnit(12.0);
        secondEntry.setStatus(OrderStatus.ORDERED);
        order.setEntries(Arrays.asList(firstEntry, secondEntry));

        Order secondOrder = new Order();
        Date secondOrderDate = assertDoesNotThrow(() -> formatter.parse("2024-01-01"));
        secondOrder.setDatetime(secondOrderDate);
        secondOrder.setId(new ObjectId());
        secondOrder.setStatus(OrderStatus.ORDERED);

        OrderEntry thirdEntry = new OrderEntry();
        thirdEntry.setArticleId(new ObjectId());
        thirdEntry.setAmount(2);
        thirdEntry.setPricePerUnit(12.0);
        thirdEntry.setStatus(OrderStatus.ORDERED);

        OrderEntry fourthEntry = new OrderEntry();
        fourthEntry.setArticleId(articleId);
        fourthEntry.setAmount(2);
        fourthEntry.setPricePerUnit(10.0);
        fourthEntry.setStatus(OrderStatus.ORDERED);
        secondOrder.setEntries(Arrays.asList(thirdEntry, fourthEntry));

        when(mongoService.findOrdersByArticleIdAndStatus(articleId, OrderStatus.ORDERED))
                .thenReturn(Arrays.asList(order, secondOrder));

        // act
        AssortmentUpdatedMessage result = service.checkArticleStockRequired(message);

        // assert
        assertNotNull(result);
        assertEquals(2, result.entries().size());
        assertEquals(2, result.entries().get(0).getAmount());
        assertEquals(4, result.entries().get(1).getAmount());
    }

    @Test
    void testArticleStockWithNoOrders() {
        // arrange
        ObjectId articleId = new ObjectId();
        ArticleStockUpdateRequiredMessage message = new ArticleStockUpdateRequiredMessage(articleId.toString(), 10);

        when(mongoService.findOrdersByArticleIdAndStatus(articleId, OrderStatus.ORDERED))
                .thenReturn(Collections.emptyList());

        // act
        AssortmentUpdatedMessage result = service.checkArticleStockRequired(message);

        // assert
        assertNotNull(result);
        assertEquals(0, result.entries().size());
    }

    @Test
    void testArticleStockWithMultipleOrdersWithDifferentStatus() {
        // arrange
        ObjectId articleId = new ObjectId();
        ArticleStockUpdateRequiredMessage message = new ArticleStockUpdateRequiredMessage(articleId.toString(), 5);

        Order order = new Order();
        order.setId(new ObjectId());
        Date orderDate = assertDoesNotThrow(() -> formatter.parse("2024-02-02"));
        order.setDatetime(orderDate);
        order.setStatus(OrderStatus.ORDERED);

        OrderEntry firstEntry = new OrderEntry();
        firstEntry.setArticleId(articleId);
        firstEntry.setAmount(5);
        firstEntry.setPricePerUnit(10.0);
        firstEntry.setStatus(OrderStatus.ORDERED);

        OrderEntry secondEntry = new OrderEntry();
        secondEntry.setArticleId(new ObjectId());
        secondEntry.setAmount(8);
        secondEntry.setPricePerUnit(12.0);
        secondEntry.setStatus(OrderStatus.DELIVERED);
        order.setEntries(Arrays.asList(firstEntry, secondEntry));

        Order secondOrder = new Order();
        Date secondOrderDate = assertDoesNotThrow(() -> formatter.parse("2024-03-03"));
        secondOrder.setDatetime(secondOrderDate);
        secondOrder.setId(new ObjectId());
        secondOrder.setStatus(OrderStatus.ORDERED);

        OrderEntry thirdEntry = new OrderEntry();
        thirdEntry.setArticleId(new ObjectId());
        thirdEntry.setAmount(3);
        thirdEntry.setPricePerUnit(12.0);
        thirdEntry.setStatus(OrderStatus.ORDERED);

        OrderEntry fourthEntry = new OrderEntry();
        fourthEntry.setArticleId(articleId);
        fourthEntry.setAmount(1);
        fourthEntry.setPricePerUnit(10.0);
        fourthEntry.setStatus(OrderStatus.CANCELED);
        secondOrder.setEntries(Arrays.asList(thirdEntry, fourthEntry));

        when(mongoService.findOrdersByArticleIdAndStatus(articleId, OrderStatus.ORDERED))
                .thenReturn(Arrays.asList(order, secondOrder));

        // act
        AssortmentUpdatedMessage result = service.checkArticleStockRequired(message);

        // assert
        assertNotNull(result);
        assertEquals(1, result.entries().size());
        assertEquals(5, result.entries().get(0).getAmount());
    }

    @Test
    void testArticleStockWithMultipleOrdersWithMultipleEntriesWithSameArticleId() {
        // arrange
        ObjectId articleId = new ObjectId();
        ArticleStockUpdateRequiredMessage message = new ArticleStockUpdateRequiredMessage(articleId.toString(), 20);

        Order order = new Order();
        order.setId(new ObjectId());
        Date orderDate = assertDoesNotThrow(() -> formatter.parse("2024-02-01"));
        order.setDatetime(orderDate);
        order.setStatus(OrderStatus.ORDERED);

        OrderEntry firstEntry = new OrderEntry();
        firstEntry.setArticleId(articleId);
        firstEntry.setAmount(10);
        firstEntry.setPricePerUnit(10.0);
        firstEntry.setStatus(OrderStatus.ORDERED);

        OrderEntry secondEntry = new OrderEntry();
        secondEntry.setArticleId(articleId);
        secondEntry.setAmount(11);
        secondEntry.setPricePerUnit(12.0);
        secondEntry.setStatus(OrderStatus.ORDERED);
        order.setEntries(Arrays.asList(firstEntry, secondEntry));

        Order secondOrder = new Order();
        Date secondOrderDate = assertDoesNotThrow(() -> formatter.parse("2024-03-01"));
        secondOrder.setDatetime(secondOrderDate);
        secondOrder.setId(new ObjectId());
        secondOrder.setStatus(OrderStatus.ORDERED);

        OrderEntry thirdEntry = new OrderEntry();
        thirdEntry.setArticleId(articleId);
        thirdEntry.setAmount(2);
        thirdEntry.setPricePerUnit(12.0);
        thirdEntry.setStatus(OrderStatus.ORDERED);

        OrderEntry fourthEntry = new OrderEntry();
        fourthEntry.setArticleId(articleId);
        fourthEntry.setAmount(4);
        fourthEntry.setPricePerUnit(10.0);
        fourthEntry.setStatus(OrderStatus.ORDERED);
        secondOrder.setEntries(Arrays.asList(thirdEntry, fourthEntry));

        when(mongoService.findOrdersByArticleIdAndStatus(articleId, OrderStatus.ORDERED))
                .thenReturn(Arrays.asList(order, secondOrder));

        // act
        AssortmentUpdatedMessage result = service.checkArticleStockRequired(message);

        // assert
        assertNotNull(result);
        assertEquals(2, result.entries().size());
        assertEquals(21, result.entries().get(0).getAmount());
        assertEquals(6, result.entries().get(1).getAmount());
    }
}