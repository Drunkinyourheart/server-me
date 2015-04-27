package org.jerry.service.yp;

import org.hbase.async.HBaseClient;
import org.jerry.service.model.consumer.Consumer;
import org.jerry.service.model.datatype.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Jerry Deng
 * @date 4/23/15.
 */
public class HBaseConsumer implements Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseConsumer.class);

    private HBaseClient hBaseClient;

    public HBaseConsumer(String zk_quorum) {
        hBaseClient = new HBaseClient(zk_quorum);
    }

    @Override
    public <T> T consume(Event event) throws Exception {

//        (Rowevent.getValue(0);
//        PutRequest putRequest = new PutRequest()

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
