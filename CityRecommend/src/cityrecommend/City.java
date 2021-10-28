package cityrecommend;

import java.io.IOException;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import weather.OpenWeatherMap;
import wikipedia.MediaWiki;

public class City {
    private double[] normalizedFeatures = new double[10];
    private String cityName;    

    public City(String cityName, String countryInitials, String appid, String[] keywords) {
        this.cityName = cityName;
        this.normalizedFeatures = DataRetriever.populateData(cityName, countryInitials, appid, keywords);        
    }

    public void setNormalizedFeatures(double[] features) {
        this.normalizedFeatures = features;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double[] getNormalizedFeatures() {
        return normalizedFeatures;
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

        private static double[] populateData(String cityName, String countryInitials, String appid, String[] keywords) {            
            double unormalizedFeatures[] = new double[10];
            double featureArr[] = new double[7];
            double weatherArr[] = new double[3]; 
            
            try {
                featureArr = retrieveFeatureCount(cityName, keywords);
            } catch (IOException e) {
                //DO SOMETHING? HOW TO THROW EXCEPTION?
            }           
            
            try {
                weatherArr = retrieveWeatherData(cityName, countryInitials, appid);
            } catch (IOException e) {
                //DO SOMETHING? HOW TO THROW EXCEPTION?
            }      
            
            for (int i = 0; i < 7; i++) {
                unormalizedFeatures[i] = featureArr[i];                
            }
            
            for (int i = 0; i < 3; i++) {
                unormalizedFeatures[i+7] = weatherArr[i];
            }
                        
            return normalizedFeatures(unormalizedFeatures);
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
        
        private static double[] retrieveWeatherData(String city, String country, String appid) throws  IOException {
            double[] weatherArr = new double[3];
            ObjectMapper mapper = new ObjectMapper();            
          
            OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&APPID="+appid+""), OpenWeatherMap.class);
            //System.out.println(city+" temperature: " + (weather_obj.getMain()).getTemp());
            //System.out.println(city+" lat: " + weather_obj.getCoord().getLat()+" lon: " + weather_obj.getCoord().getLon());
            
            weatherArr[0] = weather_obj.getMain().getTemp();
            weatherArr[1] = weather_obj.getClouds().getAll();
            weatherArr[2] = calculateDistance(ATHENSLAT, ATHENSLON, weather_obj.getCoord().getLat(), weather_obj.getCoord().getLon(), 'K');
            
            return weatherArr;
        }
        
        /** Creates an array with the counted keywords from the wiki page.
        @param city The string with name of the city.
        @param keywords The string array with the 7 keywords.
        @return An array of size 7 with counted keywords.        
        */
        private static double[] retrieveFeatureCount(String city, String[] keywords) throws  IOException {            
            ObjectMapper mapper = new ObjectMapper();
            
            MediaWiki mediaWiki_obj = mapper.readValue(new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&titles="+city+"&format=json&formatversion=2"),MediaWiki.class);
            //System.out.println(city+" Wikipedia article: "+mediaWiki_obj.getQuery().getPages().get(0).getExtract()); //DEBUG MESSAGE
                        
            return countWords(mediaWiki_obj, keywords);
        }       
        
        private static double[] countWords(MediaWiki mediaWiki_obj, String[] keywords) {
            double[] wordsCount = new double[7];
            
            for (int i = 0; i < 7; i++) {
                wordsCount[i] = countCriterionfCity(mediaWiki_obj.getQuery().getPages().get(0).getExtract(), keywords[i]);
                if (wordsCount[i] > 10) {
                    wordsCount[i] = 10;
                }
            }            
            return wordsCount;
        }
        
        /** Counts the number of times a criterion occurs in the city wikipedia article.
        @param cityArticle  The String of the retrieved wikipedia article.
        @param criterion The String of the criterion we are looking for.
        @return An integer, the number of times the criterion-string occurs in the wikipedia article.
        */	
        public static int countCriterionfCity(String cityArticle, String criterion) {
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
