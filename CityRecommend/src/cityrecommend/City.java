/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor
 */
package cityrecommend;

import java.io.IOException;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import weather.OpenWeatherMap;
import wikipedia.MediaWiki;
/**
 *
 * @author Michail Kinnas it22046
 */
public class City {
    private double[] features = new double[10];
    private String cityName;    

    public City(String cityName, String countryInitials, String appid) {
        this.cityName = cityName;
        this.features = DataRetriever.populateData(cityName, countryInitials, appid);
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
    
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::                         INSIDE CLASS                           :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static class DataRetriever{        
        private static final double ATHENSLAT = 33.9519347;
        private static final double ATHENSLON = -83.357567;    
        //private final int MAXDIST = 15326;                        //distance between Athens and Sydney
        private static final int MAXDIST = 20038;                   //max distance between two points on planet Earth's surface (Earths circumference 40075km / 2)    

        private static final int FEATUREMAX = 10;   
        private static final int FEATUREMIN = 0;

        private static final int TEMPMAX = 331;
        private static final int TEMPMIN = 184;


        private static double getDistance(String cityName) {
            double cityLatitude = getCityLatitude(cityName);
            double cityLongitude = getCityLongitude(cityName);

            return distance(ATHENSLAT, ATHENSLON, cityLatitude, cityLongitude, 'K');
        }

        private static double[] populateData(String cityName, String countryInitials, String appid) {
            double unormalizedArray[] = new double[10];
            
            try {
                RetrieveData(cityName, countryInitials, appid);
            } catch (IOException e) {
            //DO NOTHING
        }
            
            
            return null;
        }

        /*
        Calculates distance between two sets of longitude and latitude in signed degrees format (DDD.dddd)
        Using real numbers: East-West from -180.0000 to 180.0000 and North-South from 90.0000 to -90.0000
        */
        private static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
             if (unit == 'K') {
                dist = dist * 1.609344;
            } else if (unit == 'N') {
                dist = dist * 0.8684;
            }
            return (dist);
        }        
        /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
        /*::  This function converts decimal degrees to radians             :*/
        /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
        private static double deg2rad(double deg) {
          return (deg * Math.PI / 180.0);
        }    
        /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
        /*::  This function converts radians to decimal degrees             :*/
        /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
        private static double rad2deg(double rad) {
          return (rad * 180.0 / Math.PI);
        }

        private static double[] normalizedFeatures(double[] features){       
            double[] normalizedArray = new double[10];

            for (int i = 0; i <= 6; i++) {
                normalizedArray[i] = featureNormalizer(features[i], FEATUREMAX, FEATUREMIN);    //case of city features  
            }        
            normalizedArray[7] = featureNormalizer(features[7], TEMPMAX, TEMPMIN);              //case of temperature         
            normalizedArray[8] = features[8] / 100;                                             //case of cloud coverage         
            normalizedArray[9] = geodesicNormalizer(features[9], MAXDIST);                      //case of cities distance

            return normalizedArray;
        }

        private static double featureNormalizer(double feature, int max, int min) {
            return (feature - min) / (max - min);
        }

        private static double geodesicNormalizer(double distance, int max) {        
            return distance / max;
        }

        //TO BE IMPLEMENTED
        private static double getCityLatitude(String city) {
            return 0;
        }
        //TO BE IMPLEMENTED
        private static double getCityLongitude(String city) {
            return 0;
        }
        
        private static void RetrieveData(String city, String country, String appid) throws  IOException {
            /**Retrieves weather information, geotag (lan, lon) and a Wikipedia article for a given city.
            * @param city The Wikipedia article and OpenWeatherMap city. 
            * @param country The country initials (i.e. gr, it, de).
            * @param appid Your API key of the OpenWeatherMap.*/ 
             
            ObjectMapper mapper = new ObjectMapper(); 
            OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&APPID="+appid+""), OpenWeatherMap.class);
            System.out.println(city+" temperature: " + (weather_obj.getMain()).getTemp());
            System.out.println(city+" lat: " + weather_obj.getCoord().getLat()+" lon: " + weather_obj.getCoord().getLon());
            MediaWiki mediaWiki_obj =  mapper.readValue(new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&titles="+city+"&format=json&formatversion=2"),MediaWiki.class);
            System.out.println(city+" Wikipedia article: "+mediaWiki_obj.getQuery().getPages().get(0).getExtract());
        }
    }
}
