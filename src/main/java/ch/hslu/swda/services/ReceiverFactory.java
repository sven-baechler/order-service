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
        switch (receiver) {
            case "OrderReceiver":
                LogService logService = getLogServiceByClass(OrderReceiver.class);
                OrderCreateService orderCreateService = new OrderCreateService(getMongoService());
                return new OrderReceiver(this.exchangeName, this.bus, logService, orderCreateService);
            case "ArticleStockRequiredReceiver":
                return new ArticleStockRequiredReceiver(this.exchangeName, this.bus);
            case "OrderEntryUpdatedReceiver":
                return new OrderEntryUpdatedReceiver(this.exchangeName, this.bus);
            case "OrderListRequestedReceiver":
                return new OrderListRequestedReceiver(this.exchangeName, this.bus);
            default:
                return null;
        }
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
