package ch.hslu.swda.messages;

import ch.hslu.swda.dto.OrderCreatedMessageOrderEntry;

import java.util.Collections;
import java.util.List;

public class OrderCreatedMessage {

    private final String orderId;

    private final List<OrderCreatedMessageOrderEntry> entries;

    public OrderCreatedMessage(String orderId, List<OrderCreatedMessageOrderEntry> entries) {
        this.orderId = orderId;
        this.entries = Collections.unmodifiableList(entries);
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderCreatedMessageOrderEntry> getEntries() {
        return this.entries;
    }
}
