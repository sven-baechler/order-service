package ch.hslu.swda.receivers;

import ch.hslu.swda.bus.BusConnector;
import ch.hslu.swda.bus.MessageReceiver;
import ch.hslu.swda.services.logging.LogService;
import ch.hslu.swda.messages.ArticleStockUpdateRequiredMessage;
import ch.hslu.swda.messages.AssortmentUpdatedMessage;
import ch.hslu.swda.messages.AssortmentUpdatedReply;
import ch.hslu.swda.messages.OrderEntryUpdatedMessage;
import ch.hslu.swda.micro.Routes;
import ch.hslu.swda.services.mongo.MongoService;
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
    private final LogService logService;
    private final OrderEntryService orderEntryService;
    private final ArticleStockRequiredService articleStockRequiredService;

    public ArticleStockRequiredReceiver(final String exchangeName, final BusConnector bus) {
        this.exchangeName = exchangeName;
        this.bus = bus;
        this.logService = new LogService(exchangeName, bus, LOG);
        this.orderEntryService = new OrderEntryService(this.logService, new MongoService(logService));
        this.articleStockRequiredService = new ArticleStockRequiredService(new MongoService(logService), logService);
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

                // TODO-sven: so machen?
                assortmentUpdatedReply.getOrderEntryUpdatedMessages().stream()
                        .forEach(orderEntryUpdatedMessage -> orderEntryService.handleOrderEntryUpdated(orderEntryUpdatedMessage));
            }
        } catch (IOException | InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
