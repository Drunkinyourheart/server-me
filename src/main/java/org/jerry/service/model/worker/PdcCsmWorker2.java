//package org.jerry.service.model.worker;
//
//import org.apache.log4j.Logger;
//import org.jerry.service.model.Alive;
//import org.jerry.service.model.consumer.Consumer;
//import org.jerry.service.model.datatype.Event;
//import org.jerry.service.model.producer.Producer;
//import org.jerry.service.monitor.Dumpable;
//import org.jerry.service.monitor.LifeCycle;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
//* @author Jerry Deng
//* @date 4/23/15.
//*/
//public class WorkerEx2 implements Dumpable, LifeCycle {
//
//    private static final Logger LOGGER = Logger.getLogger(WorkerEx2.class);
//
//    private static final int DEFAULT_THREAD_NUMBER = 2;
//    private final BlockingQueue<Event> blockingQueue;
//    private final Consumer consumer;
//    private final Producer producer;
//
//    private ExecutorService executorService;
//    private ScheduledExecutorService scheduledExecutorService;
//
//    private AtomicInteger threadIndex = new AtomicInteger(0);
//    private int consumerThreadNum = DEFAULT_THREAD_NUMBER;
//    private int producerThreadNum = DEFAULT_THREAD_NUMBER;
//    private volatile LifeNode phase = LifeNode.STOPED;
//
//    //---------------- extensions -------------------------------------------------------------------
//    private final List<Runnable> producers;
//    private final List<Runnable> consumers;
//
//    /**
//     * -----------------------------  Constructur ---------------------------------------------------
//     */
//
//    public WorkerEx2(Producer producer, Consumer consumer) {
//        this(new LinkedBlockingQueue<Event>(Integer.MAX_VALUE / 100), producer, DEFAULT_THREAD_NUMBER, consumer, DEFAULT_THREAD_NUMBER);
//    }
//
//    public WorkerEx2(Producer producer, int producerThreadNum, Consumer consumer, int consumerThreadNum) {
//        this(new LinkedBlockingQueue<Event>(Integer.MAX_VALUE / 100), producer, producerThreadNum, consumer, consumerThreadNum);
//    }
//
//    public WorkerEx2(int queueSize, Producer producer, int producerThreadNum, Consumer consumer, int consumerThreadNum) {
//        this(new LinkedBlockingQueue<Event>(queueSize), producer, producerThreadNum, consumer, consumerThreadNum);
//    }
//
//    public WorkerEx2(BlockingQueue<Event> blockingQueue, Producer producer, int producerThreadNum, Consumer consumer, int consumerThreadNum) {
//        this.blockingQueue = blockingQueue;
//        this.producer = producer;
//        this.producerThreadNum = producerThreadNum;
//        this.consumer = consumer;
//        this.consumerThreadNum = consumerThreadNum;
//        this.producers = new ArrayList<>(producerThreadNum);
//        this.consumers = new ArrayList<>(consumerThreadNum);
//    }
//
//    /**
//     * 添加待处理时间
//     */
//    public boolean addEvent(Event event) {
//        if (isStoped()) {
//            return false;
//        }
//        if (event == null) {
//            LOGGER.info("待处理的事件不能为null：");
//            return false;
//        }
//        boolean isOK = blockingQueue.offer(event);
//        LOGGER.info("add event-" + event);
//        if (isOK) {
//            event.setStartTime(System.currentTimeMillis());
//        }
//        return isOK;
//    }
//
//    @Override
//    public String dump() {
//        return null;
//    }
//
//    @Override
//    public void dump(Appendable out, String indent) throws IOException {
//        out.append(String.format("%-50s", ("msg to handl count : ")) + "[" + blockingQueue.size() + "]").append("    " + System.getProperty("line.separator"));
//        consumer.dump(out, indent);
//    }
//
//    @Override
//    public synchronized void start() {
//
//        if (phase != LifeNode.STOPED) {
//            throw new RuntimeException("WorkerEx" + this + " already started");
//        }
//        setPhase(LifeNode.STARTING);
//        int threadNum = this.producerThreadNum + this.consumerThreadNum;
//        executorService = Executors.newFixedThreadPool(threadNum, new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable r) {
//                Thread t = new Thread(r, "WorkerEx-Thread#" + threadIndex.incrementAndGet());
//                t.setDaemon(true);
//                return t;
//            }
//        });
//
//        scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable r) {
//                Thread t = new Thread(r, "WorkerEx-scheduled-Thread#" + threadIndex.incrementAndGet());
//                t.setDaemon(true);
//                return t;
//            }
//        });
//
//        for (int i = 0; i < producerThreadNum; i++) {
//            Runnable producer = createProducer();
//            consumers.add(producer);
//            executorService.submit(producer);
//        }
//        for (int i = 0; i < consumerThreadNum; ++i) {
//            Runnable consumer = createConsumer();
//            consumers.add(consumer);
//            executorService.submit(consumer);
//        }
//
//        setPhase(LifeNode.STARTED);
//    }
//
//    @Override
//    public synchronized void stop() {
//        if (executorService != null) {
//            executorService.shutdown();
//        }
//        try {
//            for (int i = 0; i < producerThreadNum; ++i) {
//                producer.destroy();
//            }
//            for (int i = 0; i < consumerThreadNum; ++i) {
//                consumer.destroy();
//            }
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//        }
//        setPhase(LifeNode.STOPED);
//        long randomKey = new Random(100000000).nextLong();
//        LOGGER.info("WorkerEx stoped at time: " + System.currentTimeMillis() + " token is " + randomKey);
//        Event event = null;
//        //以后用scribe 代替
//        while ((event = blockingQueue.poll()) != null) {
////            LOGGER.info("token:" + randomKey + ", unfetched url is " + crawlURL.getId());
//        }
//    }
//
//    public boolean isRunning() {
//        final LifeNode _phase = phase;
//        return _phase == LifeNode.STARTING || _phase == LifeNode.STARTED;
//    }
//
//    public boolean isStoped() {
//        return phase == LifeNode.STOPED;
//    }
//
//    public void join() throws InterruptedException {
//        if (executorService != null) {
//            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
//        }
//    }
//
//    private Runnable createProducer() {
//        return new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (isRunning()) {
//                        Event event = (Event) producer.produce();
//                        while (event != null && isRunning()) {
//                            LOGGER.info("WorkerEx is started to produce " + event);
//                            try {
//                                blockingQueue.put(event);
//                            } catch (Exception e) {
//                                LOGGER.error(e.getMessage(), e);
//                            }
//                            event = (Event) producer.produce();
//                        }
//                    }
//                } catch (Exception e) {
//                    LOGGER.error(e.getMessage(), e);
//                }
//
//            }
//        };
//    }
//
//
//    /**
//     * 相当于处理器-生产者
//     */
//    private Runnable innerProduceProcessor = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                while (isRunning()) {
//                    Event event = (Event) producer.produce();
//                    while (event != null && isRunning()) {
//                        LOGGER.info("WorkerEx is started to produce " + event);
//                        try {
//                            blockingQueue.put(event);
//                        } catch (Exception e) {
//                            LOGGER.error(e.getMessage(), e);
//                        }
//                        event = (Event) producer.produce();
//                    }
//                }
//            } catch (Exception e) {
//                LOGGER.error(e.getMessage(), e);
//            }
//
//        }
//    };
//
//    protected Runnable createConsumer() {
//        return new Runnable() {
//            @Override
//            public void run() {
//                try {
////                Event event = blockingQueue.poll();
//                    Event event = blockingQueue.take();
//                    while (isRunning()) {
//                        while (event != null && isRunning()) {
//                            LOGGER.info("WorkerEx is started to consume " + event);
//                            try {
//                                consumer.consume(event);
//                            } catch (Exception e) {
//                                LOGGER.error(e.getMessage(), e);
//                            }
//                            event = blockingQueue.poll();
//                        }
//                        while (isRunning() && event == null) {
//                            event = blockingQueue.take();
//                        }
//                    }
//                } catch (Exception e) {
//                    LOGGER.error(e.getMessage(), e);
//                }
//
//            }
//        };
//    }
//
//    /**
//     * 相当于处理器-消费者, 可优化, 暂不修改
//     */
//    private Runnable innerProcessor = new Runnable() {
//        @Override
//        public void run() {
//            try {
////                Event event = blockingQueue.poll();
//                Event event = blockingQueue.take();
//                while (isRunning()) {
//                    while (event != null && isRunning()) {
//                        LOGGER.info("WorkerEx is started to consume " + event);
//                        try {
//                            consumer.consume(event);
//                        } catch (Exception e) {
//                            LOGGER.error(e.getMessage(), e);
//                        }
//                        event = blockingQueue.poll();
//                    }
//                    while (isRunning() && event == null) {
//                        event = blockingQueue.take();
//                    }
//                }
//            } catch (Exception e) {
//                LOGGER.error(e.getMessage(), e);
//            }
//
//        }
//    };
//
//
//    public LifeNode getPhase() {
//        return phase;
//    }
//
//    public void setPhase(LifeNode phase) {
//        this.phase = phase;
//    }
//
//    private enum LifeNode {
//        STARTING, STARTED, STOPED
//    }
//
//    private class WorkerMonitor implements Runnable {
//
//        @Override
//        public void run() {
//            // expunge
//            expungeWorker(taskBuilderWorkers);
//            expungeWorker(crawlerTaskAssignWorkers);
//            expungeWorker(extractorWorkers);
//            expungeWorker(urlFilerWorkers);
//            expungeWorker(timeoutWorkers);
//            expungeWorker(scheduleWorkers);
//
//            startWorker();
//        }
//    }
//    private void expungeWorker(List<? extends RunnableFuture> workers) {
//
//        List<Runnable> deadWorkers = statsDeadWorker(pr);
//        workers.removeAll(deadWorkers);
//    }
//
//    private List<Alive> statsDeadWorker(List<? extends Alive> workers) {
//        List<Alive> deadWorkers = new ArrayList<Alive>();
//
//        for (Runnable r : producers) {
//            r.
//        }
//        for (Alive alive : workers) {
//            if (!alive.isAlive()) {
//                deadWorkers.add(alive);
//            }
//        }
//        return deadWorkers;
//    }
//
//
//
//    @Override
//    public String toString() {
//        return "WorkerEx [blockingQueue=" + blockingQueue + ", consumer="
//                + consumer + ", phase=" + phase
//                + ", threadNum=" + (this.producerThreadNum + ", " + this.consumerThreadNum) + "]";
//    }
//
//}
