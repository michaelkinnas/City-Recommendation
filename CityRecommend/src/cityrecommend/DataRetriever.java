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
public class DataRetriever {
    private static final double ATHENSLAT = 33.9519347;
    private static final double ATHENSLON = -83.357567;    
    //private final int MAXDIST = 15326;                //distance between Athens and Sydney
    private static final int MAXDIST = 20038;                  //max distance between two points on planet Earth's surface (Earths circumference 40075km / 2)    
    
    private static final int hardFeatureMax = 10;   
    private static final int hardFeatureMin = 0;
    
    private static final int hardTempMax = 331;
    private static final int hardTempMin = 184;
    
    
    public static double getDistance(String cityName) {
        double cityLatitude = getCityLatitude(cityName);
        double cityLongitude = getCityLongitude(cityName);
        
        return distance(ATHENSLAT, ATHENSLON, cityLatitude, cityLongitude, 'K');
    }
    
    public static double[] populateData(String cityName, String countryInitials) {
        //to be implemented
        //all these should be returned normalized from methods inside the dataRetriever class
        //for features[0] to features[6] call dataRetriever.countWords method
        //for features[7] get temp from dataRetriever.getTemp method
        //for features[8] get cloud coverage from dataRetriever.getTemp method
        //for geatures[9] get distance to Athens from dataRetriever.getDistance method
        //return normalizedArray[];
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
    
    public static double[] normalizedFeatures(double[] features){       
        double[] normalizedArray = new double[10];
        
        for (int i = 0; i <= 6; i++) {
            normalizedArray[i] = featureNormalizer(features[i], hardFeatureMax, hardFeatureMin);    //case of city features  
        }        
        normalizedArray[7] = featureNormalizer(features[7], hardTempMax, hardTempMin);              //case of temperature         
        normalizedArray[8] = features[8] / 100;                                                     //case of cloud coverage         
        normalizedArray[9] = geodesicNormalizer(features[9], MAXDIST);                              //case of cities distance
        
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

}
