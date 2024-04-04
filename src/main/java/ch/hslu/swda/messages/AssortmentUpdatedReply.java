package ch.hslu.swda.messages;

import java.util.List;

public class AssortmentUpdatedReply {
    private List<OrderEntryUpdatedMessage> orderEntryUpdatedMessages;

    public List<OrderEntryUpdatedMessage> getOrderEntryUpdatedMessages() {
        return orderEntryUpdatedMessages;
    }
}
