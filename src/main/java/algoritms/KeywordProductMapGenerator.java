package algoritms;

import connector.HttpRequestSender;
import models.Offer;
import models.SitePage;
import support.HttpDataFetcher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class KeywordProductMapGenerator implements IProductMapGenerator {
    private final static String FILE_NAME = "k_offers.xml";
    private Set<SitePage> pagesSet;
    private Integer connectionTimeout;
    private Integer requestTimeout;
    private Integer socketTimeout;
    private AtomicInteger executedPages = new AtomicInteger();
    private Map<String, String> headers = Collections.EMPTY_MAP;
    private String searcheable;

    public KeywordProductMapGenerator(String searcheable, Integer connectionTimeout, Integer requestTimeout, Integer socketTimeout) {
        this.searcheable = searcheable;
        this.connectionTimeout = connectionTimeout;
        this.requestTimeout = requestTimeout;
        this.socketTimeout = socketTimeout;
        pagesSet = ConcurrentHashMap.newKeySet();
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

    @Override
    public void generateSiteMap(String url) {
        LOGGER.info("Generating first page from " + url + getFirstFetchPrefix());
        preGenerateFirstPage(url, pagesSet, false);
        Runtime rnt = Runtime.getRuntime();
        int cpus = rnt.availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(cpus);
        for (int i = 0; i < cpus; i++) {
            HttpDataFetcher fetcher = new HttpDataFetcher("Thread_" + i, url, pagesSet, executedPages, getSender(), false);
            threadPool.execute(fetcher);
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted, while waiting ending of algorhitm.", e);
        }
        List<Offer> offers = getOffersList(this.pagesSet);
        afterExecutionSerialization(offers, FILE_NAME);
    }

    @Override
    public String getFirstFetchPrefix() {
        return "/suche?term=" + searcheable;
    }
}
