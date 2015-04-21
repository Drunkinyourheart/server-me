package org.jerry.service.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 用户监控，可以通过telnet 输入p字母打印出监控信息
 */
public class Monitor extends Thread implements LifeCycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class);

    private static int DEFAULT_LISTENING_PORT = 8100;

    private List<Dumpable> list = new CopyOnWriteArrayList<Dumpable>();
    protected int serverPort = DEFAULT_LISTENING_PORT;
    private ServerSocket serverSocket;


    public Monitor() {
    }

    public Monitor(int port) {
        serverPort = port;
    }

    public void addMonitored(Dumpable m_) {
        list.add(m_);
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            LOGGER.error("cannot create server socket on port " + serverPort, e);
            return;
        }
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                LOGGER.error("cannot accept socket", e);
                return;
            }
            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream out = new PrintStream(socket.getOutputStream());
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("p")) {
                        for (Dumpable m : list) {
                            m.dump(out, "------------\n");
                        }
                    }

                }
                socket.close();
            } catch (Throwable e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void destroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
