package ch.hslu.swda.services;

import ch.hslu.swda.dto.OrderCreatedMessageOrderEntry;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.OrderCreatedMessage;
import ch.hslu.swda.mongo.MongoService;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class OrderCreateService {
    private final MongoService mongoService;
    private final LogService logService;
    public OrderCreateService(final MongoService mongoService, final LogService logService) {
        this.mongoService = mongoService;
        this.logService = logService;
    }
    public OrderCreatedMessage CreateOrder(Order order) {
        order.setId(new ObjectId());
        order.setStatus(OrderStatus.ORDERED);
        for (OrderEntry orderEntry : order.getEntries()) {
            orderEntry.setStatus(OrderStatus.ORDERED);
        }

        mongoService.InsertOrder(order);

        List<OrderCreatedMessageOrderEntry> orderCreatedMessageOrderEntries = new ArrayList<>();
        for (OrderEntry orderEntry : order.getEntries()) {
            orderCreatedMessageOrderEntries.add(new OrderCreatedMessageOrderEntry(orderEntry.getArticleId().toString(), orderEntry.getAmount()));
        }

        return new OrderCreatedMessage(order.getId().toString(), orderCreatedMessageOrderEntries);
    }
}
