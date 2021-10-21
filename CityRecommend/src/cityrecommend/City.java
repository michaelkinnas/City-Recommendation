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
        populateData(cityName, countryInitials);
    }
    
    private void populateData(String cityName, String countryInitials) {
        //to be implemented
        //all these should be returned normalized from methods inside the dataRetriever class
        //for features[0] to features[6] call dataRetriever.countWords method
        //for features[7] get temp from dataRetriever.getTemp method
        //for features[8] get cloud coverage from dataRetriever.getTemp method
        //for geatures[9] get distance to Athens from dataRetriever.getDistance method
    }
    
}
