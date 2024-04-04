package ch.hslu.swda.messages;

import ch.hslu.swda.entities.Order;

public class OrderReceivedMessage {
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
