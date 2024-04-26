package ch.hslu.swda.services;

import ch.hslu.swda.dto.OrderCreatedMessageOrderEntry;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.messages.OrderCreatedMessage;
import ch.hslu.swda.services.mongo.MongoService;
import org.bson.types.ObjectId;

import java.util.List;

public class OrderCreateService {
    private final MongoService mongoService;
    public OrderCreateService(final MongoService mongoService) {
        this.mongoService = mongoService;
    }
    public OrderCreatedMessage createOrder(Order order) {
        order.setId(new ObjectId());
        order.setStatus(OrderStatus.ORDERED);
        order.getEntries().forEach(orderEntry -> orderEntry.setStatus(OrderStatus.ORDERED));

        mongoService.insertOrder(order);

        List<OrderCreatedMessageOrderEntry> orderCreatedMessageOrderEntries = order.getEntries().stream()
                .map(orderEntry -> new OrderCreatedMessageOrderEntry(orderEntry.getArticleId().toString(), orderEntry.getAmount()))
                .toList();

        return new OrderCreatedMessage(order.getId().toString(), orderCreatedMessageOrderEntries);
    }
}
