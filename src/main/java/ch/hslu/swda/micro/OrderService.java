package ch.hslu.swda.micro;

import ch.hslu.swda.services.ReceiverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class OrderService  implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private static final String START_LISTENING_LOG_MESSAGE = "Starting listening for messages with routing [{}]";
    private static final String QUEUE_NAME_PREFIX = "Starting listening for messages with routing [{}]";
    private final ReceiverFactory receiverFactory;

    public OrderService(ReceiverFactory receiverFactory) throws IOException, TimeoutException {
        this.receiverFactory = receiverFactory;
        this.receiverFactory.getBus().connect();

        this.startMessageReceivers();
    }

    @Override
    public void close() {
        this.receiverFactory.getBus().close();
    }

    private void startMessageReceivers() throws IOException {
        this.receiveOrderReceived();
        this.receiveArticleStockUpdateRequired();
        this.receiveOrderEntryUpdated();
        this.orderListRequestedReceived();
    }

    private void receiveOrderReceived() throws IOException {
        LOG.debug(START_LISTENING_LOG_MESSAGE, Routes.ORDER_RECEIVED);
        this.receiverFactory.getBus().listenFor(this.receiverFactory.getExchangeName(), QUEUE_NAME_PREFIX + Routes.ORDER_RECEIVED, Routes.ORDER_RECEIVED, this.receiverFactory.getReceiver("OrderReceiver"));
    }

    private void receiveArticleStockUpdateRequired() throws IOException {
        LOG.debug(START_LISTENING_LOG_MESSAGE, Routes.ARTICLE_STOCK_UPDATE_REQUIRED);
        this.receiverFactory.getBus().listenFor(this.receiverFactory.getExchangeName(), QUEUE_NAME_PREFIX + Routes.ARTICLE_STOCK_UPDATE_REQUIRED, Routes.ARTICLE_STOCK_UPDATE_REQUIRED, this.receiverFactory.getReceiver("ArticleStockRequiredReceiver"));
    }

    private void receiveOrderEntryUpdated() throws IOException {
        LOG.debug(START_LISTENING_LOG_MESSAGE, Routes.ORDER_ENTRY_UPDATED);
        this.receiverFactory.getBus().listenFor(this.receiverFactory.getExchangeName(), QUEUE_NAME_PREFIX + Routes.ORDER_ENTRY_UPDATED, Routes.ORDER_ENTRY_UPDATED, this.receiverFactory.getReceiver("OrderEntryUpdatedReceiver"));
    }

    private void orderListRequestedReceived() throws IOException {
        LOG.debug(START_LISTENING_LOG_MESSAGE, Routes.ORDER_LIST_REQUESTED);
        this.receiverFactory.getBus().listenFor(this.receiverFactory.getExchangeName(), QUEUE_NAME_PREFIX + Routes.ORDER_LIST_REQUESTED, Routes.ORDER_LIST_REQUESTED, this.receiverFactory.getReceiver("OrderListRequestedReceiver"));
    }
}
