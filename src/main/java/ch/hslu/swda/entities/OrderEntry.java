package ch.hslu.swda.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class OrderEntry {
    private ObjectId articleId;

    private String articleName;

    private long amount;

    private double pricePerUnit;

    private OrderStatus status;

    public ObjectId getArticleId() {
        return articleId;
    }

    public void setArticleId(ObjectId articleId) {
        this.articleId = articleId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
