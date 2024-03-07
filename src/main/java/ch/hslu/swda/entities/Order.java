package ch.hslu.swda.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class Order {
    @BsonId
    private ObjectId id;

    private ObjectId branchOfficeId;

    private ObjectId sellerId;

    @JsonProperty
    private Customer customer;

    private Date datetime;

    private OrderStatus status;

    @JsonProperty
    private List<OrderEntry> entries;


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getBranchOfficeId() {
        return branchOfficeId;
    }

    public void setBranchOfficeId(ObjectId branchOfficeId) {
        this.branchOfficeId = branchOfficeId;
    }

    public ObjectId getSellerId() {
        return sellerId;
    }

    public void setSellerId(ObjectId sellerId) {
        this.sellerId = sellerId;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public List<OrderEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<OrderEntry> entries) {
        this.entries = entries;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
