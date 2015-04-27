package org.jerry.service.yp.eventtype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransEvent {

    private Date cmitTime;

    private List<RowEvent> rowEvents = new ArrayList<RowEvent>();

    public boolean hasTableEvent(String tableName) {
        for (RowEvent rowEvent : rowEvents) {
            if (tableName.toUpperCase().equals(rowEvent.getTableName())) {
                return true;
            }
        }
        return false;
    }

    public List<RowEvent> getSpecifyTableEvents(String tableName) {
        List<RowEvent> result = new ArrayList<RowEvent>();
        for (RowEvent rowEvent : rowEvents) {
            if (tableName.toUpperCase().equals(rowEvent.getTableName())) {
                result.add(rowEvent);
            }
        }
        return result;
    }

    public String toString() {
        return "cmitTime:" + cmitTime + " " + rowEvents.toString();
    }

    public Date getCmitTime() {
        return cmitTime;
    }

    public void setCmitTime(Date cmitTime) {
        this.cmitTime = cmitTime;
    }

    public List<RowEvent> getRowEvents() {
        return rowEvents;
    }

    public void addRowEvent(RowEvent rowEvent) {
        rowEvents.add(rowEvent);
    }
}
