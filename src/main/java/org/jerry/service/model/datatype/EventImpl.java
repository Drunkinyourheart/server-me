package org.jerry.service.model.datatype;

import java.util.ArrayList;
import java.util.List;

public class EventImpl implements Event {

    public enum EventStatus {
        INITED, HANDLING, HANDLED
    }

    private Long timestamp;
    private Long startTime;
    private Long stopTime;
    private List<Object> values = new ArrayList<Object>();
    private long taskId;

    public EventImpl(Object... vals) {
        for(Object o: vals) {
            values.add(o);
        }
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getStopTime() {
        return stopTime;
    }

    @Override
    public void setStopTime(Long stopTime) {
        this.stopTime = stopTime;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long get_outAckVal() {
        return _outAckVal;
    }

    public void set_outAckVal(long _outAckVal) {
        this._outAckVal = _outAckVal;
    }

    long _outAckVal = 0;

    public void updateAckVal(long val) {
        _outAckVal = _outAckVal ^ val;
    }

    public long getAckVal() {
        return _outAckVal;
    }

    public int size() {
        return values.size();
    }

    @Override
    public int fieldIndex(String var1) {
        return 0;
    }

    @Override
    public boolean contains(String var1) {
        return false;
    }

    public Object getValue(int i) {
        return values.get(i);
    }

    public String getString(int i) {
        return (String) values.get(i);
    }

    public Integer getInteger(int i) {
        return (Integer) values.get(i);
    }

    public Long getLong(int i) {
        return (Long) values.get(i);
    }

    public Boolean getBoolean(int i) {
        return (Boolean) values.get(i);
    }

    public Short getShort(int i) {
        return (Short) values.get(i);
    }

    public Byte getByte(int i) {
        return (Byte) values.get(i);
    }

    public Double getDouble(int i) {
        return (Double) values.get(i);
    }

    public Float getFloat(int i) {
        return (Float) values.get(i);
    }

    public byte[] getBinary(int i) {
        return (byte[]) values.get(i);
    }


    public Object getValueByField(String field) {
        return values.get(fieldIndex(field));
    }

    public String getStringByField(String field) {
        return (String) values.get(fieldIndex(field));
    }

    public Integer getIntegerByField(String field) {
        return (Integer) values.get(fieldIndex(field));
    }

    public Long getLongByField(String field) {
        return (Long) values.get(fieldIndex(field));
    }

    public Boolean getBooleanByField(String field) {
        return (Boolean) values.get(fieldIndex(field));
    }

    public Short getShortByField(String field) {
        return (Short) values.get(fieldIndex(field));
    }

    public Byte getByteByField(String field) {
        return (Byte) values.get(fieldIndex(field));
    }

    public Double getDoubleByField(String field) {
        return (Double) values.get(fieldIndex(field));
    }

    public Float getFloatByField(String field) {
        return (Float) values.get(fieldIndex(field));
    }

    public byte[] getBinaryByField(String field) {
        return (byte[]) values.get(fieldIndex(field));
    }

    public List<Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

}
