package ch.hslu.swda.services;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.RabbitMqConfig;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.micro.OrderReceiver;
import ch.hslu.swda.mongo.MongoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceFactory {
    private final BusConnector bus;
    private final String exchangeName;
    private final LogService logService;
    private final MongoService mongoService;
    private final OrderCreateService orderCreateService;

    // TODO: eigentlich so
    private static final Logger LOG = LoggerFactory.getLogger(OrderReceiver.class);

    public ServiceFactory() {
        this.bus = new BusConnector();
        this.exchangeName = new RabbitMqConfig().getExchange();
        this.logService = new LogService(exchangeName, bus, LoggerFactory.getLogger("TODO"));
        this.mongoService = new MongoService(logService);
        this.orderCreateService = new OrderCreateService(mongoService);
    }

    public LogService getLogService() {
        return this.logService;
    }

    public MongoService getMongoService() {
        return this.mongoService;
    }

    public OrderCreateService getOrderCreateService() {
        return this.orderCreateService;
    }

    public BusConnector getBus() {
        return this.bus;
    }

    public String getExchangeName() {
        return this.exchangeName;
    }
}
