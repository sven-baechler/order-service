package ch.hslu.swda.mongo;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.logging.LogService;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoService {
    private final MongoConfig config;
    private final LogService logService;

    public MongoService(final LogService logService) {
        this.logService = logService;
        this.config = new MongoConfig();
    }

    public boolean InsertOrder(Order order) {
        String connectionString = config.getConnectionString();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        try (MongoClient client = MongoClients.create(connectionString)) {
            MongoDatabase database = client.getDatabase(config.getDatabaseName()).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Order> collection = database.getCollection(config.getOrderCollectionName(), Order.class);

            collection.insertOne(order);
        }
        catch (Exception ex) {
            this.logService.info("Could not save order to DB with id %s", order.getId().toHexString()).send();
            return false;
        }

        this.logService.info("Order saved to DB with id %s", order.getId().toHexString()).send();
        return true;
    }
}
