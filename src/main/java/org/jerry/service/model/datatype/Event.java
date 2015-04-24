package org.jerry.service.model.datatype;

import java.util.List;

/**
 * @author Jerry Deng
 * @date 4/23/15.
 */
public interface Event {

    void setStartTime(Long startTime);

    void setStopTime(Long stopTime);

    int size();

    int fieldIndex(String var1);

    boolean contains(String var1);

    Object getValue(int var1);

    String getString(int var1);

    Integer getInteger(int var1);

    Long getLong(int var1);

    Boolean getBoolean(int var1);

    Short getShort(int var1);

    Byte getByte(int var1);

    Double getDouble(int var1);

    Float getFloat(int var1);

    byte[] getBinary(int var1);

    Object getValueByField(String var1);

    String getStringByField(String var1);

    Integer getIntegerByField(String var1);

    Long getLongByField(String var1);

    Boolean getBooleanByField(String var1);

    Short getShortByField(String var1);

    Byte getByteByField(String var1);

    Double getDoubleByField(String var1);

    Float getFloatByField(String var1);

    byte[] getBinaryByField(String var1);

    List<Object> getValues();

}
