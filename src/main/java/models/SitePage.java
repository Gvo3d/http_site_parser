package models;

public class SitePage {
    private String url;
    private Offer offer;
    private boolean fetched;
    private boolean hasProduct;

    public SitePage(String url) {
        this.url = url;
        this.fetched = false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFetched() {
        return fetched;
    }

    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }

    public boolean isHasProduct() {
        return hasProduct;
    }

    public void setHasProduct(boolean hasProduct) {
        this.hasProduct = hasProduct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SitePage sitePage = (SitePage) o;

        return url.equals(sitePage.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
