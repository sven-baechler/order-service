package ch.hslu.swda.mongo;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.logging.LogService;
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
import java.util.ArrayList;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoService {
    private final MongoConfig config;
    private final LogService logService;
    private final CodecRegistry pojoCodecRegistry;

    public MongoService(final LogService logService) {
        this.logService = logService;
        this.config = new MongoConfig();
        this.pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    public boolean insertOrder(Order order) {
        try (MongoClient client = this.getMongoClient()) {
            MongoCollection<Order> collection = this.getCollection(client, config.getOrderCollectionName(), Order.class);
            collection.insertOne(order);

            this.logService.info("Order saved to DB with id %s", order.getId().toHexString()).send();
            return true;
        }
        catch (Exception ex) {
            this.logService.info("Could not save order to DB with id %s", order.getId().toHexString()).send();
            return false;
        }
    }

    public Order getOrder(ObjectId orderId) {
        try (MongoClient client = this.getMongoClient()) {
            MongoCollection<Order> collection = this.getCollection(client, config.getOrderCollectionName(), Order.class);
            Order order = collection.find(filterByOrderId(orderId)).first();

            if (order == null) {
                this.logService.info(String.format("could not find order with id %s", orderId.toString()));
                return null;
            }

            return order;
        }
        catch (Exception ex) {
            this.logService.error("Error while get order with id %s", orderId.toString()).send();
            return null;
        }
    }

    public boolean updateOrder(Order order) {
        try (MongoClient client = this.getMongoClient()) {
            MongoCollection<Order> collection = this.getCollection(client, config.getOrderCollectionName(), Order.class);
            collection.findOneAndReplace(filterByOrderId(order.getId()), order);
            this.logService.info("updated order with id %s", order.getId().toString()).send();
            return true;
        }
        catch (Exception ex) {
            this.logService.error("Error while updating order with id %s", order.getId().toString()).send();
            return false;
        }
    }

    public ArrayList<Order> findAllOrders() {
        ArrayList<Order> orders = new ArrayList<>();

        try (MongoClient client = this.getMongoClient()) {
            MongoCollection<Order> collection = this.getCollection(client, config.getOrderCollectionName(), Order.class);

            orders = collection.find().into(new ArrayList<>());
        }
        catch (Exception ex) {
            this.logService.info("Error fetching all orders").send();
        }

        return orders;
    }

    public List<Order> findOrdersByArticleIdAndStatus(ObjectId articleId, OrderStatus status) {
        List<Order> orders = new ArrayList<>();

        try (MongoClient client = this.getMongoClient()) {
            MongoCollection<Order> collection = this.getCollection(client, config.getOrderCollectionName(), Order.class);

            Bson filter = Filters.and(Filters.eq("entries.articleId", articleId), Filters.eq("status", status));
            orders = collection.find(filter).into(new ArrayList<>());
        }
        catch (Exception ex) {
            this.logService.info("Error fetching orders for article ID %s and status %s", articleId.toHexString(), status).send();
        }

        return orders;
    }

    private MongoClient getMongoClient() {
        return MongoClients.create(this.config.getConnectionString());
    }

    private <T> MongoCollection<T> getCollection(MongoClient client, String collectionName, Class<T> clazz) {
        MongoDatabase database = client.getDatabase(this.config.getDatabaseName()).withCodecRegistry(this.pojoCodecRegistry);
        return database.getCollection(collectionName, clazz);
    }

    private Bson filterByOrderId(ObjectId orderId) {
        return Filters.eq("_id", orderId);
    }
}
