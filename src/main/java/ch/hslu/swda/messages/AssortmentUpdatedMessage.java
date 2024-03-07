package ch.hslu.swda.messages;

import ch.hslu.swda.dto.OrdersAssortmentUpdateMessageOrderEntry;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.ReOrder;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;

public class AssortmentUpdatedMessage {

    private final String articleId;
    private final int stock;
    private final List<OrdersAssortmentUpdateMessageOrderEntry> entries;

    public AssortmentUpdatedMessage(String articleId, int stock, List<OrdersAssortmentUpdateMessageOrderEntry> entries) {
        this.articleId = articleId;
        this.stock = stock;
        this.entries = Collections.unmodifiableList(entries);
    }

    public String getArticleId() {
        return articleId;
    }

    public int getStock() {
        return stock;
    }

    public List<OrdersAssortmentUpdateMessageOrderEntry> getEntries() {
        return entries;
    }
}
