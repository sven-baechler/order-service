package ch.hslu.swda.messages;

import ch.hslu.swda.dto.OrdersAssortmentUpdateMessageOrderEntry;

import java.util.Collections;
import java.util.List;

public record AssortmentUpdatedMessage(String articleId, int stock,
                                       List<OrdersAssortmentUpdateMessageOrderEntry> entries) {

    public AssortmentUpdatedMessage(String articleId, int stock, List<OrdersAssortmentUpdateMessageOrderEntry> entries) {
        this.articleId = articleId;
        this.stock = stock;
        this.entries = Collections.unmodifiableList(entries);
    }
}
