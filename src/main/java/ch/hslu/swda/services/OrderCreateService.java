package ch.hslu.swda.services;

import ch.hslu.swda.dto.OrderCreatedMessageOrderEntry;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.messages.OrderCreatedMessage;
import ch.hslu.swda.services.mongo.MongoService;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

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

        // TODO-sven: so machen?
        order.getEntries().stream().forEach(orderEntry -> orderEntry.setStatus(OrderStatus.ORDERED));

        mongoService.insertOrder(order);

        List<OrderCreatedMessageOrderEntry> orderCreatedMessageOrderEntries = order.getEntries().stream()
                .map(orderEntry -> new OrderCreatedMessageOrderEntry(orderEntry.getArticleId().toString(), orderEntry.getAmount()))
                .collect(Collectors.toList());

        return new OrderCreatedMessage(order.getId().toString(), orderCreatedMessageOrderEntries);
    }
}
