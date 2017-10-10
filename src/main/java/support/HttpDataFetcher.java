package support;

import connector.HttpRequestSender;
import models.SitePage;
import org.apache.log4j.Logger;
import parsers.DataParser;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class HttpDataFetcher implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(HttpDataFetcher.class);
    private Set<SitePage> pagesSet;
    private HttpRequestSender sender;
    private DataParser parser = new DataParser();
    private AtomicInteger executed;
    private String name;
    private String urlPrefix;
    private boolean fetchAllLinks;

    public HttpDataFetcher(String name, String urlPrefix, Set<SitePage> pagesSet, AtomicInteger executed, HttpRequestSender sender, boolean fetchAllLinks) {
        this.urlPrefix = urlPrefix;
        this.name = name;
        this.executed = executed;
        this.pagesSet = pagesSet;
        this.sender = sender;
        this.fetchAllLinks = fetchAllLinks;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(name);
        LOGGER.info(Thread.currentThread().getName() + " has started.");
        while (executed.get() < pagesSet.size()) {
            Iterator<SitePage> iterator = pagesSet.iterator();
            while (iterator.hasNext()) {
                SitePage page = iterator.next();
                ReentrantLock lock = page.getLock();
                if (lock.tryLock()) {
                    if (!page.isFetched()) {
                        String result = sender.doRequest(urlPrefix + page.getUrl()).get();
                        boolean isAProduct = parser.hasAProduct(page.getUrl());
                        if (isAProduct) {
                            page.setOffer(parser.getProductData(result));
                        }
                        if (fetchAllLinks) {
                            pagesSet.addAll(parser.getAllUrls(result));
                        } else {
                            if (page.getOffer() == null) {
                                pagesSet.addAll(parser.getKeywordSpecificUrls(result));
                            }
                        }
                        page.setFetched(true);
                        executed.incrementAndGet();
                        LOGGER.info(Thread.currentThread().getName() + " fetched " + (isAProduct ? "product:" : "page   :") + page.getUrl());
                    } else {
                        lock.unlock();
                    }
                }
            }
        }
        LOGGER.info(Thread.currentThread().getName() + " has ended.");
    }
}
