package bgu.spl181.net.api.json;

import java.io.Serializable;
import java.util.List;

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
    private String price;
    @SerializedName("bannedCountries")
    @Expose
    private List<String> bannedCountries = null;
    @SerializedName("availableAmount")
    @Expose
    private String availableAmount;
    @SerializedName("totalAmount")
    @Expose
    private String totalAmount;
    private final static long serialVersionUID = 201720431157349819L;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getBannedCountries() {
        return bannedCountries;
    }

    public void setBannedCountries(List<String> bannedCountries) {
        this.bannedCountries = bannedCountries;
    }

    public String getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(String availableAmount) {
        this.availableAmount = availableAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
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
}