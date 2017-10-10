package parsers;

import models.DescriptionData;
import models.Offer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataParser {
    private final static String PRODUCT_NAME_REGEX = "^(\\/\\p\\/)";
    private final static String DOM_CONTAINER_ELEMENT = "container";
    private final static String DOM_PRODUCT_NAME_ELEMENT = "productName";
    private final static List<String> notLinkStartsRules = new ArrayList<>();
    private final static List<String> notLinkEqualsRules = new ArrayList<>();
    private StringLengthComparator comparator = new StringLengthComparator();
    private int descriptionContainerDefaultSize = 200;
    private final static String LINE_SEPARATOR = System.lineSeparator();

    public DataParser() {
        notLinkStartsRules.add("//");
        notLinkStartsRules.add("http");
        notLinkStartsRules.add("mailto");
        notLinkEqualsRules.add("/");
    }

    public boolean hasAProduct(String pageName) {
        return pageName.matches(PRODUCT_NAME_REGEX);
    }

    public List<String> getUrls(String page) {
        Document doc = Jsoup.parse(page);
        Elements links = doc.select("a[href]");
        return links.eachAttr("href").stream().filter(this::check).collect(Collectors.toList());
    }

    private boolean check(String o) {
        for (String rule : notLinkStartsRules) {
            if (o.startsWith(rule)) {
                return false;
            }
        }
        for (String rule : notLinkEqualsRules) {
            if (o.equals(rule)) {
                return false;
            }
        }
        return true;
    }

    public Offer getProductData(String page) {
        Document doc = Jsoup.parse(page);
        Elements elements = doc.getElementsByClass(DOM_CONTAINER_ELEMENT);
        Offer offer = new Offer();
        String[] brandAndName = elements.select("h1[class*=productName]").first().text().split(" \\| ");
        offer.setBrand(brandAndName[0]);
        offer.setName(brandAndName[1]);
        Element initialPrice = elements.select("span[class*=finalPrice]").select("span[class*=from]").first();
        offer.setInitialPrice(initialPrice != null ? initialPrice.text() : "");
        offer.setPrice(getPrice(elements));
        offer.setArticleId(getArticle(elements));
//        offer.setColor(getColor(elements));
        offer.setSizes(getSizes(elements));
        offer.setShippingCosts(getShippingCost(elements));
        offer.setDescription(getDescription(elements));
        return offer;
    }

    private String getData(Elements elements, String classNameRegex) {
        return elements.select(classNameRegex).first().data();
    }

    private String getPrice(Elements elements) {
        String result = null;
        Element priceElement = elements.select("span[class*=finalPrice]").first();
        if (priceElement.select("span:not([class])").first() != null) {
            result = priceElement.select("span:not([class])").first().text();
        } else {
            result = priceElement.text();
        }
        if (elements.select("div[class*=tax]").first() != null) {
            result = result + " " + elements.select("div[class*=tax]").first().text();
        }
        return result;
    }

    private String getArticle(Elements elements) {
        List<String> result = elements.select("div[class*=container]").stream().filter(x -> x.hasText() && x.text().contains("Artikel-Nr: ")).map(Element::text).collect(Collectors.toList());
        return Stream.of(result.get(result.size() - 1).split("Artikel-Nr: ")).min(comparator).orElse("NONE");
    }

    private List<DescriptionData> getDescription(Elements elements) {
        List<DescriptionData> result = new ArrayList<>();
        for (Element element : elements.select("div[class*=wrapper]").select("div[class*=container]")) {
            if (element.select("p[class*=subline]").first() != null) {
                DescriptionData data = new DescriptionData(getSubline(element));
                data.setData(getAttributes(element.select("div[class*=attributeWrapper]").first()));
                result.add(data);
            }

            Element materialContainer = element.select("div[class*=materialCareAttributeWrapper]").first();
            if (materialContainer != null) {
                DescriptionData data = new DescriptionData(getSubline(materialContainer));
                data.setData(getAttributes(materialContainer.select("div[class*=materialAttributeWrapper] div").select("div").first()));
                materialContainer = materialContainer.select("div[class*=careSymbolWrapper]").first();
                if (materialContainer != null) {
                    data.addData(getSymbolAttributes(materialContainer));
                }
                result.add(data);
            }

            Element extrasWrapper = element.select("div[class*=extrasWrapper]").first();
            if (extrasWrapper != null) {
                DescriptionData data = new DescriptionData(getSubline(extrasWrapper));
                data.setData(getAttributes(extrasWrapper.select("div[class*=attributeWrapper]").first()));
                result.add(data);
            }
        }
        return result;
    }

    private String getColor(Elements elements) {
        Element element = elements.select("div[class*=.rc-tooltip]").first();
        return null;
    }

    private List<String> getSizes(Elements elements) {
        List<String> sizes = new ArrayList<>(8);
        Elements sizesContainers = elements.select("div[class*=js-size-dropdown-wrapper wrapper]").select("div[class*=list]").select("div[data-reactid]");
        Elements sizeData = sizesContainers.select("span:not([class*=disabled])").select("span[class*=paddingLeft]");

        if (sizeData.isEmpty()){
            sizeData = sizesContainers.select("div[class*=row]");
            for (Element element:sizeData){
                sizes.add(getSizeDataFromTable(element));
            }
        } else {
            Iterator<Element> iter = sizeData.iterator();
            while (iter.hasNext()) {
                Element nextElement = iter.next();
                sizes.add(nextElement.text());
            }
        }
        return sizes.isEmpty() ? Collections.emptyList() : sizes;
    }

    private String getSizeDataFromTable(Element element){
        return element.children().eachText().stream().collect(Collectors.joining(" "));
    }

    private String getShippingCost(Elements elements) {
        Elements shipping = elements.select("div[class*=promises]").select("div[class*=headline]");
        return shipping.eachText().stream().filter(x -> !x.matches("Kauf auf Rechnung")).collect(Collectors.joining(", "));
    }

    private String getSubline(Element parentElement) {
        return parentElement.select("p[class*=subline]").first().text();
    }

    private List<String> getAttributes(Element element) {
        Elements attrWrapper = element.select("ul[class*=orderedList] div li");
        if (attrWrapper.isEmpty()) {
            attrWrapper = element.select("ul[class*=orderedList] div ");
        }
        if (attrWrapper.isEmpty()) {
            attrWrapper = element.select("ul[class*=orderedList] li ");
        }
        List<String> data = new ArrayList<>(10);
        for (Element listElement : attrWrapper) {
            data.add(listElement.text());
        }
        return data;
    }

    private List<String> getSymbolAttributes(Element element) {
        return element.select("div[class*=tooltip careSymbols]").eachText();
    }

    private class StringLengthComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return Integer.compare(o1.length(), o2.length());
        }
    }
}
