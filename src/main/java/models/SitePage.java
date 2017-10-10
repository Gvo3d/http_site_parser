package models;

import java.util.concurrent.locks.ReentrantLock;

public class SitePage {
    private String url;
    private Offer offer;
    private ReentrantLock lock;
    private boolean fetched;

    public SitePage(String url) {
        this.lock = new ReentrantLock();
        this.url = url;
        this.offer = null;
        this.fetched = false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public boolean isFetched() {
        return fetched;
    }

    public void setFetched(boolean fetched) {
        this.fetched = fetched;
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

    @Override
    public String toString() {
        return "SitePage{" +
                "url='" + url + '\'' +
                ", offer=" + offer +
                ", fetched=" + fetched +
                '}';
    }
}
