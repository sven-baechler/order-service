package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class OrderService  implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private static final String START_LISTENING_LOG_MESSAGE = "Starting listening for messages with routing [{}]";
    private static final String QUEUE_NAME_PREFIX = "Starting listening for messages with routing [{}]";
    private final String exchangeName;
    private final BusConnector bus;

    public OrderService() throws IOException, TimeoutException {
        // thread info
        String threadName = Thread.currentThread().getName();
        LOG.debug("[Thread: {}] Service started", threadName);

        // setup rabbitmq connection
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.bus = new BusConnector();
        this.bus.connect();

        // start message receivers
        this.receiveOrderReceived();
        this.receiveArticleStockUpdateRequired();
        this.receiveOrderEntryUpdated();
        this.orderListRequestedReceived();
    }

    @Override
    public void close() throws Exception {
        this.bus.close();
    }

    private void receiveOrderReceived() throws IOException {
        LOG.debug(START_LISTENING_LOG_MESSAGE, Routes.ORDER_RECEIVED);
        this.bus.listenFor(exchangeName, QUEUE_NAME_PREFIX + Routes.ORDER_RECEIVED, Routes.ORDER_RECEIVED, new OrderReceiver(exchangeName, bus));
    }

    private void receiveArticleStockUpdateRequired() throws IOException {
        LOG.debug(START_LISTENING_LOG_MESSAGE, Routes.ARTICLE_STOCK_UPDATE_REQUIRED);
        this.bus.listenFor(exchangeName, QUEUE_NAME_PREFIX + Routes.ARTICLE_STOCK_UPDATE_REQUIRED, Routes.ARTICLE_STOCK_UPDATE_REQUIRED, new ArticleStockRequiredReceiver(exchangeName, bus));
    }

    private void receiveOrderEntryUpdated() throws IOException {
        LOG.debug(START_LISTENING_LOG_MESSAGE, Routes.ORDER_ENTRY_UPDATED);
        this.bus.listenFor(exchangeName, QUEUE_NAME_PREFIX + Routes.ORDER_ENTRY_UPDATED, Routes.ORDER_ENTRY_UPDATED, new OrderEntryUpdatedReceiver(exchangeName, bus));
    }

    private void orderListRequestedReceived() throws IOException {
        LOG.debug(START_LISTENING_LOG_MESSAGE, Routes.ORDER_LIST_REQUESTED);
        this.bus.listenFor(exchangeName, QUEUE_NAME_PREFIX + Routes.ORDER_LIST_REQUESTED, Routes.ORDER_LIST_REQUESTED, new OrderListRequestedReceiver(exchangeName, bus));
    }
}
