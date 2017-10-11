package algoritms;

import connector.HttpRequestSender;
import models.Offer;
import models.SitePage;
import support.HttpDataFetcher;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FullProductMapGenerator implements IProductMapGenerator {
    private final static String FILE_NAME = "full_offers.xml";
    private Set<SitePage> pagesSet;
    private Integer connectionTimeout;
    private Integer requestTimeout;
    private Integer socketTimeout;
    private AtomicInteger executedPages = new AtomicInteger();
    private Map<String, String> headers = Collections.EMPTY_MAP;


    public FullProductMapGenerator(Integer connectionTimeout, Integer requestTimeout, Integer socketTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.requestTimeout = requestTimeout;
        this.socketTimeout = socketTimeout;
        pagesSet = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void generateSiteMap(String url) {
        LOGGER.info("Generating first page from " + url);
        preGenerateFirstPage(url, pagesSet, true);
        Runtime rnt = Runtime.getRuntime();
        int cpus = rnt.availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(cpus);
        for (int i = 0; i < cpus; i++) {
            HttpDataFetcher fetcher = new HttpDataFetcher("Thread_" + i, url, pagesSet, executedPages, getSender(), true, Thread.currentThread());
            threadPool.execute(fetcher);
        }
        synchronized (Thread.currentThread()) {
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) {
                threadPool.shutdown();
            }
        }
        List<Offer> offers = getOffersList(this.pagesSet);
        afterExecutionSerialization(offers, FILE_NAME);
    }

    @Override
    public String getFirstFetchPrefix() {
        return "";
    }


    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public HttpRequestSender getSender() {
        return new HttpRequestSender(connectionTimeout, requestTimeout, socketTimeout);
    }

    @Override
    public int incrementExecutedCounter() {
        return executedPages.incrementAndGet();
    }

    @Override
    public int getExecutedCounter() {
        return executedPages.get();
    }
}