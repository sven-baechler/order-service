package ch.hslu.swda.dto;

public class OrderCreatedMessageOrderEntry {

    private final String articleId;
    private final long amount;

    public OrderCreatedMessageOrderEntry(String articleId, long amount) {
        this.articleId = articleId;
        this.amount = amount;
    }

    public String getArticleId() {
        return articleId;
    }

    public long getAmount() {
        return amount;
    }
}
