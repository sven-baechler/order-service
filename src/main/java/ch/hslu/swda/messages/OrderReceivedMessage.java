package ch.hslu.swda.messages;

import ch.hslu.swda.entities.Customer;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class OrderReceivedMessage {
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}