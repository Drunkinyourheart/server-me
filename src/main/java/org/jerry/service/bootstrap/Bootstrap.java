package org.jerry.service.bootstrap;

import org.jerry.service.monitor.Monitor;
import org.jerry.service.server.DemoHandler;
import org.jerry.service.server.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Jerry Deng
 * @date 4/22/15.
 */
public class Bootstrap {

    public static void main(String[] args) throws IOException {

        final Monitor monitor = new Monitor();
//        monitor.addMonitored(crawler);
        monitor.addMonitored(null);
        monitor.start();


        final HttpServer httpServer = new HttpServer(new InetSocketAddress(8800), new DemoHandler());
        httpServer.start();
        // http://localhost:8800/?url=http://www.huxiu.com/article/100861/1.html
        System.out.println("crawler http server is started!port is " + 8800);

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
//                server.stop();
                monitor.destroy();
//                httpServer.stop();
//                listMsgContext.stop();
//                zRegister.unregister();
            }

        });
    }

}
