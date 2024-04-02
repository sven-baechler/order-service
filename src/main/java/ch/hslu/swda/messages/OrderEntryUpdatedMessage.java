package ch.hslu.swda.messages;

import ch.hslu.swda.entities.OrderStatus;

public class OrderEntryUpdatedMessage {

    private String orderId;
    private String articleId;

    private OrderStatus status = OrderStatus.READY_TO_DELIVER;

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getArticleId() {
        return articleId;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
