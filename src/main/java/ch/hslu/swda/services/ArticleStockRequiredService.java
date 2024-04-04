package ch.hslu.swda.services;

import ch.hslu.swda.dto.OrdersAssortmentUpdateMessageOrderEntry;
import ch.hslu.swda.entities.Order;
import ch.hslu.swda.entities.OrderEntry;
import ch.hslu.swda.entities.OrderStatus;
import ch.hslu.swda.logging.LogService;
import ch.hslu.swda.messages.ArticleStockUpdateRequiredMessage;
import ch.hslu.swda.messages.AssortmentUpdatedMessage;
import ch.hslu.swda.mongo.MongoService;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArticleStockRequiredService {
    private final MongoService mongoService;
    private final LogService logService;

    public ArticleStockRequiredService(final MongoService mongoService, final LogService logService) {
        this.mongoService = mongoService;
        this.logService = logService;

    }
    public AssortmentUpdatedMessage CheckArticleStockRequired(ArticleStockUpdateRequiredMessage articleStockUpdateRequiredMessage) {
        ObjectId articleId = new ObjectId(articleStockUpdateRequiredMessage.articleId());
        List<Order> orders = mongoService.findOrdersByArticleIdAndStatus(articleId, OrderStatus.ORDERED);

        orders.sort(Comparator.comparing(Order::getDatetime));
        List<OrdersAssortmentUpdateMessageOrderEntry> entries = new ArrayList<>();
        for (Order order : orders) {
            long amount = 0;
            for (OrderEntry orderEntry : order.getEntries()) {
                if (orderEntry.getArticleId().equals(articleId)) {
                    amount += orderEntry.getAmount();
                }
            }

            entries.add(new OrdersAssortmentUpdateMessageOrderEntry(order.getId().toString(), amount));
        }

        AssortmentUpdatedMessage assortmentUpdatedMessage = new AssortmentUpdatedMessage(
                articleStockUpdateRequiredMessage.articleId(),
                (int) articleStockUpdateRequiredMessage.amount(),
                entries
        );
        this.logService.info(
                "Reorder with %s items of %s received. %s entries may be processed now",
                articleStockUpdateRequiredMessage.amount(), articleStockUpdateRequiredMessage.articleId(), entries.size()
        ).send();

        return assortmentUpdatedMessage;
    }
}
