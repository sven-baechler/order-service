package ch.hslu.swda.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.services.logging.LogService;
import ch.hslu.swda.messages.OrderCreatedMessage;
import ch.hslu.swda.messages.OrderReceivedMessage;
import ch.hslu.swda.micro.Routes;
import ch.hslu.swda.services.OrderCreateService;
import com.google.gson.Gson;
import java.io.IOException;

public class OrderReceiver implements MessageReceiver {
    private final String exchangeName;
    private final BusConnector bus;
    private final LogService logService;
    private final OrderCreateService orderCreateService;

    public OrderReceiver(final String exchangeName, final BusConnector bus, final LogService logService, final OrderCreateService orderCreateService) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logService = logService;
        this.orderCreateService = orderCreateService;
    }

    /**
     * @see MessageReceiver#onMessageReceived(String, String, String, String)
     */
    @Override
    public void onMessageReceived(final String route, final String replyTo, final String corrId, final String message) {
        this.logService.info("received message of type: {}", route);

        Gson gson = new Gson();
        OrderReceivedMessage orderReceivedMessage = gson.fromJson(message, OrderReceivedMessage.class);
        Order order = orderReceivedMessage.getOrder();

        OrderCreatedMessage orderCreatedMessage = this.orderCreateService.createOrder(order);

        try {
            this.bus.talkAsync(this.exchangeName, Routes.ORDER_CREATED, gson.toJson(orderCreatedMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
