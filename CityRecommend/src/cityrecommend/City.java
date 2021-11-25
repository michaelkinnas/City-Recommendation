package cityrecommend;

import java.io.IOException;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.WikipediaNoArcticleException;
import weather.OpenWeatherMap;
import wikipedia.MediaWiki;

public class City {
	private String cityName;
	private double[] vectorRepresentation = new double[10];
	private long timestamp;

	//Required to read from json file
	public City() {
		super();
	}

	//Copy constructor
	public City(City original) {
		this.cityName = original.cityName;
		this.vectorRepresentation = original.vectorRepresentation;
		this.timestamp = original.timestamp;
	}


	public City(String cityName, String countryInitials, String[] termsVector, String appid, boolean log, long timestamp) throws IOException, Exception, WikipediaNoArcticleException {
		this.cityName = cityName;
		this.vectorRepresentation = DataRetriever.populateData(cityName, countryInitials, termsVector, appid, log);
		this.timestamp = timestamp;
	}


	public boolean equals(City city) {    	
		return city.getCityName().equals(this.getCityName());
	}    


	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public double[] getVectorRepresentation() {
		return vectorRepresentation;
	}

	public void setVectorRepresentation(double[] vectorRepresentation) {
		this.vectorRepresentation = vectorRepresentation;
	}	

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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
		private static final double normalisedFeatures[] = new double[10];

		/**
		 * Constructor function		
		 * @throws IOException
		 * @throws WikipediaNoArcticleException
		 */
		private static double[] populateData(String cityName, String countryInitials, String[] termsVector, String appid, boolean log) throws IOException, WikipediaNoArcticleException {
			if (log) System.out.printf("Processing data for " + cityName + "...");
			retrieveFeatureCount(cityName, termsVector);
			if (log) System.out.printf("...");
			retrieveWeatherData(cityName, countryInitials, appid);
			if (log) System.out.println("done!");
			return normaliser(normalisedFeatures);
		}
		/**
		 * Calculates distance between two sets of longitude and latitude in signed degrees format (DDD.dddd)
		 * Using real numbers: East-West from -180.0000 to 180.0000 and North-South from 90.0000 to -90.0000
		 * @param Double. Latitude of first place
		 * @param Double. Longitude of first place	
		 * @param Double. Latitude of second place
		 * @param Double. Longitude of second place
		 * @param Char. The letter K or N for kilometers or nautical miles
		 * @return Double. Calculated distance.
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

		/**
		 * Helper to calculateDistance.
		 * @param Double. Degrees.
		 * @return Double.
		 */
		private static double deg2rad(double deg) {
			return (deg * Math.PI / 180.0);
		}    		
		/**
		 * Helper to calculateDistance.
		 * @param Double. Radians
		 * @return Double.
		 */
		private static double rad2deg(double rad) {
			return (rad * 180.0 / Math.PI);
		}		
		/**
		 * Normalizes the vector of the cities of the city class.
		 * @param Double array. The city features.
		 * @return Double array. The normalized vector.
		 */
		private static double[] normaliser(double[] features){       
			double[] normalisedFeatures = new double[10];
			for (int i = 0; i <= 6; i++) {
				normalisedFeatures[i] = featureNormalizer(features[i], FEATUREMAX, FEATUREMIN);    	//case of city features  
			}        
			normalisedFeatures[7] = featureNormalizer(features[7], TEMPMAX, TEMPMIN);              	//case of temperature         
			normalisedFeatures[8] = features[8] / 100;                                             	//case of cloud coverage         
			normalisedFeatures[9] = features[9] / MAXDIST;                      					//case of cities distance
			return normalisedFeatures;
		}		
		/**
		 * Helper to the normalizedFeatures method.
		 * @param Double. The feature of a vector of a city class.
		 * @param Integer. The max value of the normalizer function.
		 * @param Integer. The min value of the normalizer function.
		 * @return Double. The normalized number.
		 */
		private static double featureNormalizer(double feature, int max, int min) {
			return (feature - min) / (max - min);
		}		
		/**
		 * Retrieves the from the weather data API and saves the corresponding vector values into the features array of a city class.
		 * @param String. The name of the city.
		 * @param String. THe country code initials.
		 * @param String. The appid key required for the open weather data API.
		 * @throws IOException
		 */
		private static void retrieveWeatherData(String city, String country, String appid) throws IOException {            
			ObjectMapper mapper = new ObjectMapper();
			OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&APPID="+appid+""), OpenWeatherMap.class);
			normalisedFeatures[7] = weather_obj.getMain().getTemp();
			normalisedFeatures[8] = weather_obj.getClouds().getAll();
			normalisedFeatures[9] = calculateDistance(ATHENSLAT, ATHENSLON, weather_obj.getCoord().getLat(), weather_obj.getCoord().getLon(), 'K'); 
		}


		/**
		 * Retrieves a wikipedia article from the wikipedia API
		 * @param String. The name of the city.
		 * @param String[]. A string array of the features to be counted from the wiki article.
		 * @throws IOException
		 * @throws WikipediaNoArcticleException
		 */
		private static void retrieveFeatureCount(String city, String[] keywords) throws IOException, WikipediaNoArcticleException {            
			ObjectMapper mapper = new ObjectMapper();
			MediaWiki mediaWiki_obj = mapper.readValue(new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&titles="+city+"&format=json&formatversion=2"),MediaWiki.class);
			if (mediaWiki_obj.getQuery().getPages().get(0).getExtract() == null) {
				throw new WikipediaNoArcticleException(city);
			} else {
				countWords(mediaWiki_obj, keywords);
			}
		}


		/**
		 * Saves the number of found keywords in a wikipedia article to the corresponding places of the features vector array of a city object.
		 * @param MediaWiki object. The jackson object of the wikipedia article.
		 * @param String[]. An array of keywords to be counted.
		 */
		private static void countWords(MediaWiki mediaWiki_obj, String[] keywords) {
			for (int i = 0; i < 7; i++) {
				normalisedFeatures[i] = countCriterionfCity(mediaWiki_obj.getQuery().getPages().get(0).getExtract(), keywords[i]);
				if (normalisedFeatures[i] > 10) {
					normalisedFeatures[i] = 10;
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