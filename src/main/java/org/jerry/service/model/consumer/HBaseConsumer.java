package org.jerry.service.model.consumer;

import org.jerry.service.model.datatype.Event;
import org.jerry.service.monitor.Dumpable;

import java.io.IOException;

/**
 * @author Jerry Deng
 * @date 4/23/15.
 */
public class HBaseConsumer implements Consumer{
    @Override
    public <T> T consume(Event event) throws Exception {
        return null;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public String dump() {
        return null;
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {
        out.append(String.format("%-50s", ("current thread name is  : ")) + "[" + Thread.currentThread().getName() + "]").append("    " + System.getProperty("line.separator"));
    }
}
