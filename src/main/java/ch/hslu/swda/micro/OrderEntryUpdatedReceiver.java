package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.mongo.MongoConfig;
import ch.hslu.swda.services.OrderEntryService;
import com.google.gson.Gson;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class OrderEntryUpdatedReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(OrderEntryUpdatedReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final LogService logService;

    private final OrderEntryService orderEntryService;

    public OrderEntryUpdatedReceiver(final String exchangeName, final BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logService = new LogService(exchangeName, bus, LOG);
        this.orderEntryService = new OrderEntryService(LOG, this.logService);
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.debug("received message of type: {} with message {}", route, message);

        Gson gson = new Gson();
        OrderEntryUpdatedMessage orderEntryUpdatedMessage = gson.fromJson(message, OrderEntryUpdatedMessage.class);

        this.orderEntryService.handleOrderEntryUpdated(orderEntryUpdatedMessage);
    }
}
