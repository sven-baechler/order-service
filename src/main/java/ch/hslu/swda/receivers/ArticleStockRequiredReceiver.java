package ch.hslu.swda.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.messages.ArticleStockUpdateRequiredMessage;
import ch.hslu.swda.messages.AssortmentUpdatedMessage;
import ch.hslu.swda.messages.AssortmentUpdatedReply;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.micro.Routes;
import ch.hslu.swda.services.ArticleStockRequiredService;
import ch.hslu.swda.services.OrderEntryService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ArticleStockRequiredReceiver implements MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(ArticleStockRequiredReceiver.class);
    private final String exchangeName;
    private final BusConnector bus;
    private final OrderEntryService orderEntryService;
    private final ArticleStockRequiredService articleStockRequiredService;

    public ArticleStockRequiredReceiver(final String exchangeName, final BusConnector bus, final OrderEntryService orderEntryService, final ArticleStockRequiredService articleStockRequiredService) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.orderEntryService = orderEntryService;
        this.articleStockRequiredService = articleStockRequiredService;
    }

    @Override
    public void onMessageReceived(String route, String replyTo, String corrId, String message) {
        LOG.debug("received message [{}] in ArticleStockRequiredReceiver", message);
        Gson gson = new Gson();
        ArticleStockUpdateRequiredMessage articleStockUpdateRequiredMessage = gson.fromJson(message, ArticleStockUpdateRequiredMessage.class);
        AssortmentUpdatedMessage assortmentUpdatedMessage = this.articleStockRequiredService.checkArticleStockRequired(articleStockUpdateRequiredMessage);

        try {
            String reply = this.bus.talkSync(this.exchangeName, Routes.ORDERS_ASSORTMENT_UPDATED, gson.toJson(assortmentUpdatedMessage));
            if (reply != null) {
                AssortmentUpdatedReply assortmentUpdatedReply = new Gson().fromJson(reply, AssortmentUpdatedReply.class);
                for (OrderEntryUpdatedMessage orderEntryUpdatedMessage : assortmentUpdatedReply.getOrderEntryUpdatedMessages()) {
                    this.orderEntryService.handleOrderEntryUpdated(orderEntryUpdatedMessage);
                }

                assortmentUpdatedReply.getOrderEntryUpdatedMessages().stream()
                        .forEach(orderEntryService::handleOrderEntryUpdated);
            }
        } catch (IOException | InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
