package algoritms;

import connector.HttpRequestSender;
import models.Offer;
import models.SitePage;
import org.apache.log4j.Logger;
import parsers.DataParser;
import serializer.XmlSerializer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface IProductMapGenerator {
    final static Logger LOGGER = Logger.getLogger(IProductMapGenerator.class);

    Map<String, String> getHeaders();

    void setHeaders(Map<String, String> headers);

    HttpRequestSender getSender();

    int incrementExecutedCounter();

    int getExecutedCounter();

    void generateSiteMap(String url);

    String getFirstFetchPrefix();

    default void preGenerateFirstPage(String url, Set<SitePage> pagesSet, boolean fetchAllLinks) {
        HttpRequestSender sender = getSender();
        sender.setHeaders(getHeaders());
        DataParser parser = new DataParser();
        String result = sender.doRequest(url + getFirstFetchPrefix()).get();
        SitePage firstPage = new SitePage(url);
        if (fetchAllLinks) {
            pagesSet.addAll(parser.getAllUrls(result));
        } else {
            pagesSet.addAll(parser.getKeywordSpecificUrls(result));
        }
        firstPage.setFetched(true);
        incrementExecutedCounter();
        LOGGER.info(Thread.currentThread().getName() + " fetched: " + url + getFirstFetchPrefix() + " and got new " + pagesSet.size() + " links.");
    }

    default List<Offer> getOffersList(Set<SitePage> pagesSet) {
        return pagesSet.stream().filter(x -> x.getOffer() != null).map(SitePage::getOffer).collect(Collectors.toList());
    }

    default void afterExecutionSerialization(List<Offer> offers, String fileName) {
        StringBuilder builder = new StringBuilder("HTTP TASK HAS ENDED. Fetched ").append(getExecutedCounter()).append(" pages with products quantity: ").append(offers.size()).append(" pcs.");
        LOGGER.info(builder.toString());
        XmlSerializer serializer = new XmlSerializer(fileName);
        String filename = serializer.write(offers);
        LOGGER.info("Writed output to " + filename);
    }
}