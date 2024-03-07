package ch.hslu.swda.entities;

import org.bson.types.ObjectId;

public class ReOrderEntry {
    private ObjectId articleId;

    private String articleName;

    private long amount;

    public ObjectId getArticleId() {
        return articleId;
    }

    public void setArticleId(ObjectId articleId) {
        this.articleId = articleId;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
