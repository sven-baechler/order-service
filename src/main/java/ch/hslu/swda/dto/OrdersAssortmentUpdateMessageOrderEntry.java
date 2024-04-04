package ch.hslu.swda.dto;

public class OrdersAssortmentUpdateMessageOrderEntry {
    private final String orderId;
    private final long amount;

    public OrdersAssortmentUpdateMessageOrderEntry(String orderId, long amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getAmount() {
        return amount;
    }
}
