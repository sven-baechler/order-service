package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.mongo.MongoService;
import ch.hslu.swda.services.OrderEntryService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OrderEntryUpdatedReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(OrderEntryUpdatedReceiver.class);
    private final LogService logService;
    private final MongoService mongoService;

    private final OrderEntryService orderEntryService;

    public OrderEntryUpdatedReceiver(final String exchangeName, final BusConnector bus) {
        this.logService = new LogService(exchangeName, bus, LOG);
        this.mongoService = new MongoService(logService);
        this.orderEntryService = new OrderEntryService(this.logService, this.mongoService);
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.debug("received message of type: {} with message {}", route, message);

        Gson gson = new Gson();
        OrderEntryUpdatedMessage orderEntryUpdatedMessage = gson.fromJson(message, OrderEntryUpdatedMessage.class);

        this.orderEntryService.handleOrderEntryUpdated(orderEntryUpdatedMessage);
    }
}
