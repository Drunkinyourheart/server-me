package org.jerry.service.yp.eventtype;

import java.util.HashMap;
import java.util.Map;

public class RowEvent {

    private String tableName;

    private EventType eventType;

    private String id;

    private Map<String, String> afterValues = new HashMap<String, String>();

    public String toString() {
        return "tableName:" + tableName + ";eventType:" + eventType + ";id:" + id + ";afterValues:" + afterValues.toString();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void addAfterValue(String name, String value) {
        afterValues.put(name, value);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAfterValue(String col) {
        return this.afterValues.get(col);
    }
}
