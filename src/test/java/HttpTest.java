import connector.HttpRequestSender;
import models.Offer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Test;
import parsers.DataParser;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class HttpTest {

    @Test
    public void doTest() {
        HttpRequestSender sender = new HttpRequestSender(1000, 1000, 1000);
        String result = sender.doRequest("https://www.aboutyou.de/p/steiff-collection/t-shirt-11-arm-3672522").get();
        System.out.println(result);
    }

    @Test
    public void getUrls() {
        HttpRequestSender sender = new HttpRequestSender(1000, 1000, 1000);
        String result = sender.doRequest("https://www.aboutyou.de").get();
        DataParser parser = new DataParser();
        List<String> urls = parser.getUrls(result);
        assertNotNull(urls);
        urls.forEach(System.out::println);
    }

    @Test
    public void getOffer() {
        HttpRequestSender sender = new HttpRequestSender(1000, 1000, 1000);
        String result = sender.doRequest("https://www.aboutyou.de/p/lacoste/t-shirt-mit-marken-emblem-3669296").get();
        DataParser parser = new DataParser();
        Offer offer = parser.getProductData(result);
        assertNotNull(offer);
        System.out.println(offer);
    }

}
