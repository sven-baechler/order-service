package ch.hslu.swda.messages;

import com.google.gson.Gson;

public class ArticleStockUpdateRequiredMessage {

    private final String articleId;

    private final long amount;

    public ArticleStockUpdateRequiredMessage(String articleId, long amount) {
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
