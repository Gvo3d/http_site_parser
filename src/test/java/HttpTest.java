import connector.HttpRequestSender;
import models.Offer;
import models.SitePage;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import parsers.DataParser;
import serializer.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HttpTest {
    private static HttpRequestSender sender;
    private static DataParser parser;

    @Before
    public void init() {
        sender = new HttpRequestSender(1000, 1000, 1000);
        parser = new DataParser();
    }

    @Test
    public void sendRequest() {
        String result = sender.doRequest("https://www.aboutyou.de/p/lacoste/t-shirt-mit-marken-emblem-3669296").get();
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    public void getAllUrls() {
        String result = sender.doRequest("https://www.aboutyou.de").get();
        List<SitePage> urls = parser.getAllUrls(result);
        assertNotNull(urls);
        System.out.println("Urls list size: " + urls.size());
        urls.forEach(System.out::println);
    }

    @Test
    public void getSpecificUrls() {
        int currentTimeProductsOfPage = 18;
        String result = sender.doRequest("https://www.aboutyou.de/suche?term=Premium&category=138113").get();
        List<SitePage> urls = parser.getKeywordSpecificUrls(result);
        assertNotNull(urls);
        System.out.println("Urls list size: " + urls.size());
        assertEquals(currentTimeProductsOfPage, urls.size());
        urls.forEach(System.out::println);
    }

    @Test
    public void getOffer() {
        String result = sender.doRequest("https://www.aboutyou.de/p/sheego-denim/stretchjeans-die-jeggings-3705309").get();
        Offer offer = parser.getProductData(result);
        assertNotNull(offer);
        System.out.println(offer);
    }

    @Test
    public void writeOffers() {
        String[] offersUrls = {"https://www.aboutyou.de/p/lacoste/t-shirt-mit-marken-emblem-3669296", "https://www.aboutyou.de/p/sheego-trend/jumpsuit-3290897", "https://www.aboutyou.de/p/jack-und-jones/wildleder-trucker-lederjacke-3622014"};
        List<Offer> offers = new ArrayList<>(3);
        for (String url : offersUrls) {
            String result = sender.doRequest(url).get();
            Offer offer = parser.getProductData(result);
            offers.add(offer);
        }
        System.out.println("size:" + offers.size());
        for (Offer offer : offers) {
            System.out.println(offer);
        }
        XmlSerializer serializer = new XmlSerializer("test-offers.xml");
        String fileName = serializer.write(offers);
        assertNotNull(fileName);
        System.out.println("Writed to " + fileName);
    }
}
