package org.jerry.service.bootstrap;

import org.jerry.service.model.consumer.Consumer;
import org.jerry.service.model.datatype.Event;
import org.jerry.service.model.producer.Producer;
import org.jerry.service.model.producer.XmlProducer;
import org.jerry.service.model.worker.PdcCsmWorker;
import org.jerry.service.monitor.Monitor;
import org.jerry.service.server.DemoHandler;
import org.jerry.service.server.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Jerry Deng
 * @date 4/24/15.
 */
public class BootstrapF {
    public static void main(String[] args) throws IOException {


        // 定义consumer

        Consumer consumer = new Consumer() {
            @Override
            public <T> T consume(Event event) throws Exception {
                String name = Thread.currentThread().getName();
                System.out.println("i am " + name + ", i consume : " + event);
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

            }
        };

        Producer producer = new XmlProducer();

//        final Worker worker = new Worker(200000, 2, consumer);
        final PdcCsmWorker worker = new PdcCsmWorker(500, producer, 2, consumer, 2);
        worker.start();

        final Monitor monitor = new Monitor();
//        monitor.addMonitored(crawler);
        monitor.addMonitored(worker);
        monitor.start();

        final HttpServer httpServer = new HttpServer(new InetSocketAddress(8800), new DemoHandler());
        httpServer.start();
        // http://localhost:8800/?url=http://www.huxiu.com/article/100861/1.html
        System.out.println("main-app  http server is started!port is " + 8800);

//        for (long i = 0; i < 100000; ++i) {
//            worker.addEvent(new EventImpl(String.valueOf(i)));
//        }
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
//                server.stop();
                worker.stop();
                monitor.destroy();
//                httpServer.stop();
//                listMsgContext.stop();
//                zRegister.unregister();
            }

        });
    }
}
