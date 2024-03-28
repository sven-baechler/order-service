package ch.hslu.swda.services;

import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.mongo.MongoService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;

public class OrderEntryService {
    private static Logger LOG;
    private final LogService logService;
    private final MongoService mongoService;

    public OrderEntryService(final Logger log, final LogService logService, final MongoService mongoService) {
        LOG = log;
        this.logService = logService;
        this.mongoService = mongoService;
    }

    public void handleOrderEntryUpdated(OrderEntryUpdatedMessage orderEntryUpdatedMessage) {
        ObjectId orderId = new ObjectId(orderEntryUpdatedMessage.getOrderId());
        ObjectId articleId = new ObjectId(orderEntryUpdatedMessage.getArticleId());
        OrderStatus status = orderEntryUpdatedMessage.getStatus();

        this.mongoService.updateOrderEntryStatus(orderId, articleId, status);
        this.mongoService.updateOrderStatus(orderId);
    }
}
