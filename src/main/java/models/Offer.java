package models;

import java.util.ArrayList;
import java.util.List;

public class Offer {
    private String brand;
    private String name;
    private String color;
    private String price;
    private List<String> sizes;
    private String initialPrice;
    private List<DescriptionData> description;
    private String articleId;
    private String shippingCosts;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPrice() {
        return price;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(String initialPrice) {
        this.initialPrice = initialPrice;
    }

    public List<DescriptionData> getDescription() {
        return description;
    }

    public void setDescription(List<DescriptionData> description) {
        this.description = description;
    }

    public void addDescriptionData(DescriptionData data){
        this.description.add(data);
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getShippingCosts() {
        return shippingCosts;
    }

    public void setShippingCosts(String shippingCosts) {
        this.shippingCosts = shippingCosts;
    }

    @Override
    public String toString() {
        StringBuilder desr = new StringBuilder();
        for (DescriptionData data: description){
            desr.append(data).append("\n");
        }
        return "Offer{" +
                "brand='" + brand + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", price='" + price + '\'' +
                ", initialPrice='" + initialPrice + '\'' +
                ", description='" + desr.toString() + '\'' +
                ", articleId='" + articleId + '\'' +
                ", sizes='" + sizes + '\'' +
                ", shippingCosts='" + shippingCosts + '\'' +
                '}';
    }
}
