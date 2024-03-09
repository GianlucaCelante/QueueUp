package it.pioppi.queueup.entity;

import java.time.LocalDateTime;
import java.util.Date;

public class NumberEntity {

    private String number;
    private String timestamp;
    private String client_id;

    public NumberEntity() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    @Override
    public String toString() {
        return "NumberEntity{" +
                "number='" + number + '\'' +
                ", timestamp=" + timestamp +
                ", client_id='" + client_id + '\'' +
                '}';
    }
}
