package org.jerry.service.model.worker;

import org.apache.log4j.Logger;
import org.jerry.service.model.consumer.Consumer;
import org.jerry.service.model.datatype.Event;
import org.jerry.service.model.producer.Producer;
import org.jerry.service.monitor.Dumpable;
import org.jerry.service.monitor.LifeCycle;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jerry Deng
 * @date 4/23/15.
 */
public class PdcCsmWorker implements Dumpable, LifeCycle {

    private static final Logger LOGGER = Logger.getLogger(PdcCsmWorker.class);

    private static final int DEFAULT_THREAD_NUMBER = 2;
    private final BlockingQueue<Event> blockingQueue;
    private final Consumer consumer;
    private final Producer producer;

    private ExecutorService executorService;
    private AtomicInteger threadIndex = new AtomicInteger(0);
    private int consumerThreadNum = DEFAULT_THREAD_NUMBER;
    private int producerThreadNum = DEFAULT_THREAD_NUMBER;
    private volatile LifeNode phase = LifeNode.STOPED;

    /**
     * -----------------------------  Constructur ---------------------------------------------------
     */

    public PdcCsmWorker(Producer producer, Consumer consumer) {
        this(new LinkedBlockingQueue<Event>(Integer.MAX_VALUE / 100), producer, DEFAULT_THREAD_NUMBER, consumer, DEFAULT_THREAD_NUMBER);
    }

    public PdcCsmWorker(Producer producer, int producerThreadNum, Consumer consumer, int consumerThreadNum) {
        this(new LinkedBlockingQueue<Event>(Integer.MAX_VALUE / 100), producer, producerThreadNum, consumer, consumerThreadNum);
    }

    public PdcCsmWorker(int queueSize, Producer producer, int producerThreadNum, Consumer consumer, int consumerThreadNum) {
        this(new LinkedBlockingQueue<Event>(queueSize), producer, producerThreadNum, consumer, consumerThreadNum);
    }

    public PdcCsmWorker(BlockingQueue<Event> blockingQueue, Producer producer, int producerThreadNum, Consumer consumer, int consumerThreadNum) {
        this.blockingQueue = blockingQueue;
        this.producer = producer;
        this.producerThreadNum = producerThreadNum;
        this.consumer = consumer;
        this.consumerThreadNum = consumerThreadNum;
    }

    /**
     * 添加待处理时间
     */
    public boolean addEvent(Event event) {
        if (isStoped()) {
            return false;
        }
        if (event == null) {
            LOGGER.info("待处理的事件不能为null：");
            return false;
        }
        boolean isOK = blockingQueue.offer(event);
        LOGGER.info("add event-" + event);
        if (isOK) {
            event.setStartTime(System.currentTimeMillis());
        }
        return isOK;
    }



    @Override
    public String dump() {
        return null;
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException {
        out.append(String.format("%-50s", ("msg to handl count : ")) + "[" + blockingQueue.size() + "]").append("    " + System.getProperty("line.separator"));
        consumer.dump(out, indent);
    }

    @Override
    public synchronized void start() {

        if (phase != LifeNode.STOPED) {
            throw new RuntimeException("PdcCsmWorker" + this + " already started");
        }
        setPhase(LifeNode.STARTING);
        int threadNum = this.producerThreadNum + this.consumerThreadNum;
        executorService = Executors.newFixedThreadPool(threadNum, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "PdcCsmWorker-Thread#" + threadIndex.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        });
        for (int i = 0; i < producerThreadNum; i++) {
            executorService.submit(innerProduceProcessor);
        }
        for (int i = 0; i < consumerThreadNum; ++i) {
            executorService.submit(innerProcessor);
        }

        setPhase(LifeNode.STARTED);
    }

    @Override
    public synchronized void stop() {
        if (executorService != null) {
            executorService.shutdown();
        }
        try {
            producer.destroy();
            consumer.destroy();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        setPhase(LifeNode.STOPED);
        long randomKey = new Random(100000000).nextLong();
        LOGGER.info("PdcCsmWorker stoped at time: " + System.currentTimeMillis() + " token is " + randomKey);
        Event event = null;
        //以后用scribe 代替
        while ((event = blockingQueue.poll()) != null) {
//            LOGGER.info("token:" + randomKey + ", unfetched url is " + crawlURL.getId());
        }
    }

    public boolean isRunning() {
        final LifeNode _phase = phase;
        return _phase == LifeNode.STARTING || _phase == LifeNode.STARTED;
    }

    public boolean isStoped() {
        return phase == LifeNode.STOPED;
    }

    public void join() throws InterruptedException {
        if (executorService != null) {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 相当于处理器-生产者
     */
    private Runnable innerProduceProcessor = new Runnable() {
        @Override
        public void run() {
            try {
                while (isRunning()) {
                    Event event = (Event)producer.produce();
                    while (event != null && isRunning()) {
                        LOGGER.info("PdcCsmWorker is started to produce " + event);
                        try {
                            blockingQueue.put(event);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                        event = (Event)producer.produce();
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

        }
    };


    /**
     * 相当于处理器-消费者, 可优化, 暂不修改
     */
    private Runnable innerProcessor = new Runnable() {
        @Override
        public void run() {
            try {
//                Event event = blockingQueue.poll();
                Event event = blockingQueue.take();
                while (isRunning()) {
                    while (event != null && isRunning()) {
                        LOGGER.info("PdcCsmWorker is started to consume " + event);
                        try {
                            consumer.consume(event);
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                        event = blockingQueue.poll();
                    }
                    while (isRunning() && event == null) {
                        event = blockingQueue.take();
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

        }
    };


    public LifeNode getPhase() {
        return phase;
    }

    public void setPhase(LifeNode phase) {
        this.phase = phase;
    }

    private enum LifeNode {
        STARTING, STARTED, STOPED
    }

    @Override
    public String toString() {
        return "PdcCsmWorker [blockingQueue=" + blockingQueue + ", consumer="
                + consumer + ", phase=" + phase
                + ", threadNum=" + (this.producerThreadNum + ", " + this.consumerThreadNum) + "]";
    }

}
