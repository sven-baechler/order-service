package ch.hslu.swda.services;

import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.mongo.MongoService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;

import java.util.Optional;

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

        Order order = this.mongoService.getOrder(orderId);

        if (order != null) {
            if (this.updateOrderEntryStatus(order, articleId, status)) {
                this.updateOrderStatus(order);
            }
        }
    }

    private boolean updateOrderEntryStatus(Order order, ObjectId articleId, OrderStatus status) {
        Optional<OrderEntry> matchingEntry = order.getEntries().stream()
                .filter(entry -> entry.getArticleId().equals(articleId))
                .findFirst();

        if (matchingEntry.isPresent()) {
            matchingEntry.get().setStatus(status);
            return this.mongoService.updateOrder(order);
        } else {
            this.logService.error(String.format("could not find article with id %s in order with id %s", articleId.toString(), order.getId().toString()));
            return false;
        }
    }
    private void updateOrderStatus(Order order) {
        if (order.getEntries().stream().allMatch(oe -> oe.getStatus() == OrderStatus.READY_TO_DELIVER)) {
            order.setStatus(OrderStatus.READY_TO_DELIVER);

            if (this.mongoService.updateOrder(order)) {
                this.logService.info("All order entries from order %s are ready to deliver, order status changed to ready to deliver", order.getId().toString()).send();
            }
        }
        else {
            this.logService.info("Not all order entries from order %s are ready to deliver yet, order status stays the same", order.getId().toString()).send();
        }
    }
}
