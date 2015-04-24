package org.jerry.service.model.consumer;

import org.jerry.service.model.datatype.Event;

import java.io.IOException;

/**
 * @author Jerry Deng
 * @date 4/24/15.
 */
public abstract class BasicConsumer implements Consumer{
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
//        out.append(String.format("%-50s", ("status is ")) + "[" + poolingmgr.getTotalStats() + "]").append("    " + System.getProperty("line.separator"));
    }
}
