package ch.hslu.swda.messages;

import com.google.gson.Gson;
import org.slf4j.event.Level;

public class LogMessage {
    private long timestamp;

    private Level level;

    private String message;

    private String source;

    private String correlationId;

    private String branchOfficeId;

    private String customerId;

    private String userId;

    public long getTimestamp() {
        return timestamp;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getSource() {
        return source;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getBranchOfficeId() {
        return branchOfficeId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public void setBranchOfficeId(String branchOfficeId) {
        this.branchOfficeId = branchOfficeId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
