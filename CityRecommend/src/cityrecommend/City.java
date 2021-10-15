/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cityrecommend;

import java.util.Arrays;
/**
 *
 * @author Michail Kinnas it22046
 */
public class City {  
    private double testLatitude = (Math.random() * (90 + 90) - 90); 
    private double testLongitude = (Math.random() * (180 + 180) - 180);
    
    private int hardFeatureMax = 10;    //THIS WILL CHANGE
    private int hardFeatureMin = 0;     //THIS WILL CHANGE
    
    private int hardTempMax = 331;
    private int hardTempMin = 184;
    
    //private int maxDist = 15326;      //distance between Athens and Sydney
    private int maxDist = 20038;        //max distance between two points on planet Earth's surface (Earths circumference 40075km / 2)
    
    private double[] features;           

    public City(double[] features) {            //THIS IS STUPID BUT TEMPORARY
        this.features = features;
        this.features[9] = this.distance(37.983810, 23.727539, testLatitude, testLongitude, 'K');
        this.features = this.normalizedFeatures(features);
    }
    
    private double[] normalizedFeatures(double[] features){       
        double[] normalizedArray = new double[10];
        
        for (int i = 0; i <= 6; i++) {
            normalizedArray[i] = featureNormalizer(features[i], hardFeatureMax, hardFeatureMin);    //case of city features  
        }        
        normalizedArray[7] = featureNormalizer(features[7], hardTempMax, hardTempMin);              //case of temperature         
        normalizedArray[8] = features[8] / 100;                                                     //case of cloud coverage         
        normalizedArray[9] = geodesicNormalizer(features[9], maxDist);                              //case of cities distance
        
        return normalizedArray;
    }    
    
    private double featureNormalizer(double feature, int max, int min) {
        return (feature - min) / (max - min);
    }
    
    private double geodesicNormalizer(double distance, int max) {        
        return distance / max;
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
    
    //FOR DEBUGGING ONLY
    public void testPrint() {
        System.out.println("Normalized features: " + Arrays.toString(features));
        System.out.println("Random generated Latitude - Longitude: " + testLatitude + ", " + + testLongitude);
       
    }
}
