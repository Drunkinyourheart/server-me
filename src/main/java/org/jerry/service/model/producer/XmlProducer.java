package org.jerry.service.model.producer;

import org.apache.log4j.spi.LoggerFactory;
import org.jerry.service.model.datatype.EventImpl;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Random;

/**
 * @author Jerry Deng
 * @date 4/23/15.
 */
public class XmlProducer implements Producer {

    static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(XmlProducer.class);


    int i = 0;
    @Override
    public <T> EventImpl produce() throws InterruptedException {


        LOGGER.info("i produce a msg : " + i);
//        Thread.sleep(100);
        return new EventImpl(String.valueOf(i++));
    }

    @Override
    public void destroy() {

    }

    @Override
    public String dump() {
        return null;
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {

    }
}
