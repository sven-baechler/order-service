package ch.hslu.swda.micro;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.dto.OrdersAssortmentUpdateMessageOrderEntry;
import ch.hslu.swda.entities.*;
import ch.hslu.swda.logging.Log;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.ArticleStockUpdateRequiredMessage;
import ch.hslu.swda.messages.AssortmentUpdatedMessage;
import ch.hslu.swda.messages.AssortmentUpdatedReply;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class ArticleStockRequiredReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(ArticleStockRequiredReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final LogService logService;
    private final OrderEntryService orderEntryService;

    public ArticleStockRequiredReceiver(final String exchangeName, final BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logService = new LogService(exchangeName, bus, LOG);
        this.orderEntryService = new OrderEntryService(LOG, this.logService);
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.debug("received message [{}] in ArticleStockRequiredReceiver", message);
        Gson gson = new Gson();
        ArticleStockUpdateRequiredMessage articleStockUpdateRequiredMessage = gson.fromJson(message, ArticleStockUpdateRequiredMessage.class);
        ObjectId articleId = new ObjectId(articleStockUpdateRequiredMessage.getArticleId());


        MongoConfig config = new MongoConfig();
        String connectionString = config.getConnectionString();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        try (MongoClient client = MongoClients.create(connectionString)) {
            MongoDatabase database = client.getDatabase(config.getDatabaseName()).withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Order> collection = database.getCollection(config.getOrderCollectionName(), Order.class);

            //Bson filter = Filters.and(Filters.eq("entries.articleId", reOrderReceivedMessage.getArticleId()), Filters.eq("status", OrderStatus.ORDERED));
            Bson filter = Filters.and(Filters.eq("entries.articleId", articleId), Filters.eq("status", OrderStatus.ORDERED));
            List<Order> orders = collection.find(filter).into(new ArrayList<>());

            //TODO: order by date; does this work?
            orders.sort(Comparator.comparing(Order::getDatetime));
            List<OrdersAssortmentUpdateMessageOrderEntry> entries = new ArrayList<>();
            for (Order order : orders) {
                long amount = 0;
                for (OrderEntry orderEntry : order.getEntries()) {
                    if (orderEntry.getArticleId().equals(articleId)) {
                        amount += orderEntry.getAmount();
                    }
                }

                entries.add(new OrdersAssortmentUpdateMessageOrderEntry(order.getId().toString(), amount));
            }

            AssortmentUpdatedMessage assortmentUpdatedMessage = new AssortmentUpdatedMessage(
                    articleStockUpdateRequiredMessage.getArticleId(),
                    (int) articleStockUpdateRequiredMessage.getAmount(),
                    entries
            );
            this.logService.info(
                    "Reorder with %s items of %s received. %s entries may be processed now",
                    articleStockUpdateRequiredMessage.getAmount(), articleStockUpdateRequiredMessage.getArticleId(), entries.size()
            ).send();

            try {
                String reply = this.bus.talkSync(this.exchangeName, Routes.ORDERS_ASSORTMENT_UPDATED, gson.toJson(assortmentUpdatedMessage));
                if (reply != null) {
                    AssortmentUpdatedReply assortmentUpdatedReply = new Gson().fromJson(reply, AssortmentUpdatedReply.class);
                    for (OrderEntryUpdatedMessage orderEntryUpdatedMessage: assortmentUpdatedReply.orderEntryUpdatedMessages) {
                        this.orderEntryService.handleOrderEntryUpdated(orderEntryUpdatedMessage);
                    }
                }
            } catch (IOException | InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
