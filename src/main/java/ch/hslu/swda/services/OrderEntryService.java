package ch.hslu.swda.services;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.mongo.MongoConfig;
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

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class OrderEntryService {
    private static Logger LOG;
    private final LogService logService;

    public OrderEntryService(Logger log, LogService logService) {
        LOG = log;
        this.logService = logService;
    }

    public void handleOrderEntryUpdated(OrderEntryUpdatedMessage orderEntryUpdatedMessage) {
        ObjectId orderId = new ObjectId(orderEntryUpdatedMessage.getOrderId());
        ObjectId articleId = new ObjectId(orderEntryUpdatedMessage.getArticleId());

        MongoConfig config = new MongoConfig();
        String connectionString = config.getConnectionString();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        try (MongoClient client = MongoClients.create(connectionString)) {
            MongoDatabase database = client.getDatabase(config.getDatabaseName()).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Order> collection = database.getCollection(config.getOrderCollectionName(), Order.class);

            Bson filter = Filters.eq("_id", orderId);

            Order order = collection.find(filter).first();

            if (order == null) {
                LOG.error("could not update order status");
                return;
            }

            for (OrderEntry orderEntry : order.getEntries()) {
                if (orderEntry.getArticleId().equals(articleId)) {
                    orderEntry.setStatus(orderEntryUpdatedMessage.getStatus());
                }
            }

            if (order.getEntries().stream().allMatch(oe -> oe.getStatus() == OrderStatus.READY_TO_DELIVER)) {
                order.setStatus(OrderStatus.READY_TO_DELIVER);
                this.logService.info(
                        "All order entries from order %s are ready to deliver, order status changed to ready to deliver", orderId.toString()
                ).send();
            }

            collection.findOneAndReplace(filter, order);

            this.logService.info(
                    "Updated status of order entry from order %s with article id %s to %s",
                    orderId.toString(), articleId.toString(), orderEntryUpdatedMessage.getStatus().toString()
            ).send();
        }
    }
}
