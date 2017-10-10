package sitemap;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import models.Offer;
import models.SitePage;

import java.io.File;
import java.util.Date;
import java.util.HashSet;

public class SitemapGenerator {
    HashSet<SitePage> pagesSet;

    public SitemapGenerator() {
        pagesSet = new HashSet<>();
    }

    public void generateSiteMap(String url){

    }
}
