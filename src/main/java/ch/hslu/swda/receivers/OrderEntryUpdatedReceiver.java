package ch.hslu.swda.receivers;

import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.services.logging.LogService;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.services.OrderEntryService;
import com.google.gson.Gson;

public class OrderEntryUpdatedReceiver implements MessageReceiver {
    private final LogService logService;
    private final OrderEntryService orderEntryService;

    public OrderEntryUpdatedReceiver(final LogService logService, final OrderEntryService orderEntryService) {
        this.logService = logService;
        this.orderEntryService = orderEntryService;
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        this.logService.info("received message of type: {} with message {}", route, message);

        Gson gson = new Gson();
        OrderEntryUpdatedMessage orderEntryUpdatedMessage = gson.fromJson(message, OrderEntryUpdatedMessage.class);

        this.orderEntryService.handleOrderEntryUpdated(orderEntryUpdatedMessage);
    }
}
