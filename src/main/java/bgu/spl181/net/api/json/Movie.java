package bgu.spl181.net.api.json;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movie implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private AtomicInteger price;
    @SerializedName("bannedCountries")
    @Expose
    private List<String> bannedCountries = null;
    @SerializedName("availableAmount")
    @Expose
    private AtomicInteger availableAmount;
    @SerializedName("totalAmount")
    @Expose
    private Integer totalAmount;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AtomicInteger getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price.set(price);
    }

    public List<String> getBannedCountries() {
        return bannedCountries;
    }

    public void setBannedCountries(List<String> bannedCountries) {
        this.bannedCountries = bannedCountries;
    }

    public Integer getAvailableAmount() {
        return availableAmount.get();
    }

    public void setAvailableAmount(Integer availableAmount) {
        this.availableAmount.set(availableAmount);
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String info() {
        String banned=" ";

        for(String country: bannedCountries){
            banned=banned+"\""+country+"\" ";
        }

        banned=banned.substring(0,banned.length()-1);

        return "\"" + name + "\" "
                + availableAmount + " "
                + price
                + banned;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", bannedCountries=" + bannedCountries +
                ", availableAmount=" + availableAmount +
                ", totalAmount=" + totalAmount +
                '}';
    }
}