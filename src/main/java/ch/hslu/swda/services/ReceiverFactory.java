package ch.hslu.swda.services;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.receivers.ArticleStockRequiredReceiver;
import ch.hslu.swda.receivers.OrderEntryUpdatedReceiver;
import ch.hslu.swda.receivers.OrderListRequestedReceiver;
import ch.hslu.swda.receivers.OrderReceiver;
import ch.hslu.swda.services.logging.LogService;
import ch.hslu.swda.services.mongo.MongoService;
import org.slf4j.LoggerFactory;

public class ReceiverFactory {
    private final BusConnector bus;
    private final String exchangeName;

    public ReceiverFactory() {
        this.bus = new BusConnector();
        this.exchangeName = new RabbitMqConfig().getExchange();
    }

    public MessageReceiver getReceiver(String receiver) {
        return switch (receiver) {
            case "OrderReceiver" -> new OrderReceiver(
                    this.exchangeName,
                    this.bus,
                    getLogServiceByClass(OrderReceiver.class),
                    new OrderCreateService(getMongoService())
            );
            case "ArticleStockRequiredReceiver" -> new ArticleStockRequiredReceiver(
                    this.exchangeName,
                    this.bus,
                    new OrderEntryService(getMongoService(), getLogServiceByClass(OrderEntryService.class)),
                    new ArticleStockRequiredService(getMongoService(), getLogServiceByClass(ArticleStockRequiredService.class))
            );
            case "OrderEntryUpdatedReceiver" -> new OrderEntryUpdatedReceiver(
                    getLogServiceByClass(OrderEntryUpdatedReceiver.class),
                    new OrderEntryService(getMongoService(), getLogServiceByClass(OrderEntryService.class))
            );
            case "OrderListRequestedReceiver" -> new OrderListRequestedReceiver(
                    this.exchangeName,
                    this.bus,
                    new GetOrderListService(getMongoService())
            );
            default -> null;
        };
    }

    public BusConnector getBus() {
        return this.bus;
    }

    public String getExchangeName() {
        return this.exchangeName;
    }

    private LogService getLogServiceByClass(Class<?> classInput) {
        return new LogService(
                this.exchangeName,
                this.bus,
                LoggerFactory.getLogger(classInput)
        );
    }

    private MongoService getMongoService() {
        return new MongoService(getLogServiceByClass(MongoService.class));
    }
}
