package ch.hslu.swda.services;

import ch.hslu.swda.dto.OrderCreatedMessageOrderEntry;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.messages.OrderCreatedMessage;
import ch.hslu.swda.mongo.MongoService;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class OrderCreateService {
    private final MongoService mongoService;
    public OrderCreateService(final MongoService mongoService) {
        this.mongoService = mongoService;
    }
    public OrderCreatedMessage createOrder(Order order) {
        order.setId(new ObjectId());
        order.setStatus(OrderStatus.ORDERED);
        for (OrderEntry orderEntry : order.getEntries()) {
            orderEntry.setStatus(OrderStatus.ORDERED);
        }

        mongoService.insertOrder(order);

        List<OrderCreatedMessageOrderEntry> orderCreatedMessageOrderEntries = new ArrayList<>();
        for (OrderEntry orderEntry : order.getEntries()) {
            orderCreatedMessageOrderEntries.add(new OrderCreatedMessageOrderEntry(orderEntry.getArticleId().toString(), orderEntry.getAmount()));
        }

        return new OrderCreatedMessage(order.getId().toString(), orderCreatedMessageOrderEntries);
    }
}
