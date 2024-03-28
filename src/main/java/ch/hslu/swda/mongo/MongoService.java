package ch.hslu.swda.mongo;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
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

    public boolean updateOrderEntryStatus(ObjectId orderId, ObjectId articleId, OrderStatus status) {
        try (MongoClient client = this.getMongoClient()) {
            MongoCollection<Order> collection = this.getCollection(client, config.getOrderCollectionName(), Order.class);

            Bson filter = Filters.eq("_id", orderId);

            Order order = collection.find(filter).first();

            if (order == null) {
                this.logService.error(String.format("could not find order with id %s", orderId.toString()));
                return false;
            }

            for (OrderEntry orderEntry : order.getEntries()) {
                if (orderEntry.getArticleId().equals(articleId)) {
                    orderEntry.setStatus(status);
                }
                // TODO: log error and return false if no order entry with articleId in order
            }

            collection.findOneAndReplace(filter, order);

            this.logService.info(
                    "Updated status of order entry from order %s with article id %s to %s",
                    orderId.toString(), articleId.toString(), status.toString()
            ).send();

            return true;
        }
        catch (Exception ex) {
            this.logService.error("Error while updating status of order entry from order %s with article id %s to %s",
                    orderId.toString(), articleId.toString(), status.toString()
            ).send();
            return false;
        }
    }

    public boolean updateOrderStatus(ObjectId orderId) {
        try (MongoClient client = this.getMongoClient()) {
            MongoCollection<Order> collection = this.getCollection(client, config.getOrderCollectionName(), Order.class);

            Bson filter = Filters.eq("_id", orderId);

            Order order = collection.find(filter).first();

            if (order == null) {
                this.logService.error(String.format("could not find order with id %s", orderId.toString()));
                return false;
            }

            if (order.getEntries().stream().allMatch(oe -> oe.getStatus() == OrderStatus.READY_TO_DELIVER)) {
                order.setStatus(OrderStatus.READY_TO_DELIVER);
                collection.findOneAndReplace(filter, order);
                this.logService.info(
                        "All order entries from order %s are ready to deliver, order status changed to ready to deliver", orderId.toString()
                ).send();
            }

            return true;
        }
        catch (Exception ex) {
            this.logService.error("Error while updating status of order with id %s", orderId.toString()).send();
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
}
