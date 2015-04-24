package org.jerry.service.model.consumer;

import org.jerry.service.model.datatype.Event;
import org.jerry.service.monitor.Dumpable;

/**
 * @author Jerry Deng
 * @date 4/23/15.
 */
public interface Consumer extends Dumpable {

    public <T>  T consume(Event event) throws Exception;

    public void destroy() throws Exception;

}
