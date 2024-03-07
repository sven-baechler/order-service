package ch.hslu.swda.entities;

import java.util.List;

public class ReOrder {
    private ReOrderStatus status;
    private List<ReOrderEntry> entries;

    public List<ReOrderEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ReOrderEntry> entries) {
        this.entries = entries;
    }

    public ReOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ReOrderStatus status) {
        this.status = status;
    }
}
