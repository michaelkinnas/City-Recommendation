package cityrecommend;

import java.io.IOException;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import weather.OpenWeatherMap;
import wikipedia.MediaWiki;

public class City {
	private double[] vectorRepresentation = new double[10];
    private String cityName;    

    public City(String cityName, String countryInitials, String[] termsVector, String appid, boolean log) throws IOException {
        this.cityName = cityName;
        this.vectorRepresentation = DataRetriever.populateData(cityName, countryInitials, termsVector, appid, log);        
    }

    public void setVectorRepresentation(double[] features) {
        this.vectorRepresentation = features;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double[] getVectorRepresentation() {
        return vectorRepresentation;
    }

    public String getCityName() {
        return cityName;
    }    
    
    
    private static class DataRetriever {
        private static final double ATHENSLAT = 37.983810;
        private static final double ATHENSLON = 23.727539;
        //private final int MAXDIST = 15326;                        //distance between Athens and Sydney in km
        private static final int MAXDIST = 20038;                   //max distance between two points on planet earth's surface in km
        private static final int FEATUREMAX = 10;   
        private static final int FEATUREMIN = 0;
        private static final int TEMPMAX = 331;
        private static final int TEMPMIN = 184;
        
        private static final double normalizedFeatures[] = new double[10]; 
        
        
        private static double[] populateData(String cityName, String countryInitials, String[] termsVector, String appid, boolean log) throws IOException {
        	if (log) System.out.printf("Processing data for " + cityName + "...");
        	retrieveFeatureCount(cityName, termsVector);
        	if (log) System.out.printf("...");
        	retrieveWeatherData(cityName, countryInitials, appid);
        	if (log) System.out.println("done!");
            return normalizedFeatures(normalizedFeatures);
        } 
        /*
        Calculates distance between two sets of longitude and latitude in signed degrees format (DDD.dddd)
        Using real numbers: East-West from -180.0000 to 180.0000 and North-South from 90.0000 to -90.0000
        */
        private static double calculateDistance(double lat1, double lon1, double lat2, double lon2, char unit) {
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
            double[] normalizedVector = new double[10];
            for (int i = 0; i <= 6; i++) {
            	normalizedVector[i] = featureNormalizer(features[i], FEATUREMAX, FEATUREMIN);    //case of city features  
            }        
            normalizedVector[7] = featureNormalizer(features[7], TEMPMAX, TEMPMIN);              //case of temperature         
            normalizedVector[8] = features[8] / 100;                                             //case of cloud coverage         
            normalizedVector[9] = geodesicNormalizer(features[9], MAXDIST);                     //case of cities distance
            return normalizedVector;
        }
        

        private static double featureNormalizer(double feature, int max, int min) {
            return (feature - min) / (max - min);
        }
        

        private static double geodesicNormalizer(double distance, int max) {        
            return distance / max;
        }
        
        
        private static void retrieveWeatherData(String city, String country, String appid) throws IOException {            
            ObjectMapper mapper = new ObjectMapper();
            OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&APPID="+appid+""), OpenWeatherMap.class);
            normalizedFeatures[7] = weather_obj.getMain().getTemp();
            normalizedFeatures[8] = weather_obj.getClouds().getAll();
            normalizedFeatures[9] = calculateDistance(ATHENSLAT, ATHENSLON, weather_obj.getCoord().getLat(), weather_obj.getCoord().getLon(), 'K'); 
        }
        
       
        private static void retrieveFeatureCount(String city, String[] keywords) throws IOException {            
            ObjectMapper mapper = new ObjectMapper();
            MediaWiki mediaWiki_obj = mapper.readValue(new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&titles="+city+"&format=json&formatversion=2"),MediaWiki.class);
            countWords(mediaWiki_obj, keywords);
        }
                
        private static void countWords(MediaWiki mediaWiki_obj, String[] keywords) {
            for (int i = 0; i < 7; i++) {
            	normalizedFeatures[i] = countCriterionfCity(mediaWiki_obj.getQuery().getPages().get(0).getExtract(), keywords[i]);
                if (normalizedFeatures[i] > 10) {
                	normalizedFeatures[i] = 10;
                }
            }
        } 
        /** Counts the number of times a criterion occurs in the city wikipedia article.
        @param cityArticle  The String of the retrieved wikipedia article.
        @param criterion The String of the criterion we are looking for.
        @return An integer, the number of times the criterion-string occurs in the wikipedia article.
        */	
        private static int countCriterionfCity(String cityArticle, String criterion) {
            cityArticle=cityArticle.toLowerCase();
            int index = cityArticle.indexOf(criterion);
            int count = 0;
            while (index != -1) {
                count++;
                cityArticle = cityArticle.substring(index + 1);
                index = cityArticle.indexOf(criterion);
            }
            return count;
        }
    }
}