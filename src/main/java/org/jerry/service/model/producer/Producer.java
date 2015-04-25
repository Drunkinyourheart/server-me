package org.jerry.service.model.producer;

import org.jerry.service.model.datatype.EventImpl;
import org.jerry.service.monitor.Dumpable;

/**
 * @author Jerry Deng
 * @date 4/23/15.
 */
public interface Producer extends Dumpable {

    public <T> EventImpl produce() throws InterruptedException;

    void destroy();

}
