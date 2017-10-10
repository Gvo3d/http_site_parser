package models;

import java.util.List;

public class DescriptionData {
    private String name;
    private List<String> data;

    public DescriptionData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void addData(List<String> data){
        this.data.addAll(data);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("DescriptionData{\n   name=");
            builder.append(name).append("\n   data:\n");
        data.forEach(x->builder.append("        ").append(x.toString()).append("\n"));
        return builder.toString();
    }
}
