package ch.hslu.swda.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReceiverFactoryTest {
    private ReceiverFactory receiverFactory;

    @BeforeEach
    void setUp() {
        receiverFactory = new ReceiverFactory();
    }

    @Test
    void testGetOrderReceiver() {
        assertNotNull(receiverFactory.getReceiver("OrderReceiver"));
    }

    @Test
    void testGetArticleStockRequiredReceiver() {
        assertNotNull(receiverFactory.getReceiver("ArticleStockRequiredReceiver"));
    }

    @Test
    void testGetOrderEntryUpdatedReceiver() {
        assertNotNull(receiverFactory.getReceiver("OrderEntryUpdatedReceiver"));
    }

    @Test
    void testGetOrderListRequestedReceiver() {
        assertNotNull(receiverFactory.getReceiver("OrderListRequestedReceiver"));
    }

    @Test
    void testGetReceiverWithInvalidType() {
        assertNull(receiverFactory.getReceiver("InvalidType"));
    }

}