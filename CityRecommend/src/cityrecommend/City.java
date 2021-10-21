/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cityrecommend;
/**
 *
 * @author Michail Kinnas it22046
 */
public class City {
    private double[] features = new double[10];
    private String cityName;    

    public City(String cityName, String countryInitials) {
        this.cityName = cityName;
        this.features = DataRetriever.populateData(cityName, countryInitials);
    }

    public void setFeatures(double[] features) {
        this.features = features;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double[] getFeatures() {
        return features;
    }

    public String getCityName() {
        return cityName;
    }
}
