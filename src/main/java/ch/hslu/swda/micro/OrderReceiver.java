package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.dto.OrderCreatedMessageOrderEntry;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.OrderCreatedMessage;
import ch.hslu.swda.messages.OrderReceivedMessage;
import ch.hslu.swda.mongo.MongoConfig;
import ch.hslu.swda.mongo.MongoService;
import ch.hslu.swda.services.OrderCreateService;
import com.google.gson.Gson;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class OrderReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(OrderReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final LogService logService;
    private final MongoService mongoService;
    private final OrderCreateService orderCreateService;

    public OrderReceiver(final String exchangeName, final BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logService = new LogService(exchangeName, bus, LOG);
        this.mongoService = new MongoService(logService);
        this.orderCreateService = new OrderCreateService(mongoService, logService);
    }

    /**
     * @see MessageReceiver#onMessageReceived(String, String, String, String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {
        LOG.debug("received message of type: {}", route);

        Gson gson = new Gson();
        OrderReceivedMessage orderReceivedMessage = gson.fromJson(message, OrderReceivedMessage.class);
        Order order = orderReceivedMessage.getOrder();

        OrderCreatedMessage orderCreatedMessage = orderCreateService.CreateOrder(order);


        try {
            this.bus.talkAsync(this.exchangeName, Routes.ORDER_CREATED, gson.toJson(orderCreatedMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
