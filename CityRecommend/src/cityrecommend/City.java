package cityrecommend;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import amadeus.AmadeusController;
import amadeusAuthentication.AmAuth;

import foursquare.FourSquareController;
import weather.OpenWeatherMap;


public class City {
	private String cityName;
	private double[] vectorRepresentation = new double[10];
	private long timestamp;	
	private AmadeusController covidData;

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


	public City(String cityName, String countryInitials, String[] termsVector, String owappid, String fsapid, boolean log, long timestamp) throws IOException, Exception {
		this.cityName = cityName;
		this.vectorRepresentation = DataRetriever.populateData(cityName, countryInitials, termsVector, owappid, fsapid, log);
		this.timestamp = timestamp;
		this.covidData = populateCovidData(countryInitials);
	}
	
	
	private AmadeusController populateCovidData(String countryInitials) throws IOException, InterruptedException {
		//Get amadeus token
		//String client_id_22046 = "IAAOZ01yvqsGryfjLV3M2huurSACX6sr";
		//String client_secret_22046 = "TY2OztMIIaDMRMUx";
		String client_id_22039 = "oUH4ATXGGKxJZlLOTm3fODGJTSnAfhzG";
		String client_secret_22039 = "KH4EL4AmhjepN0Bx";
		
		String client_id = client_id_22039;
		String client_secret = client_secret_22039;
		
		
		HttpRequest requestToken = HttpRequest.newBuilder()
				.uri(URI.create("https://test.api.amadeus.com/v1/security/oauth2/token"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.method("POST", HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret))
				.build();
		HttpResponse<String> responseToken = HttpClient.newHttpClient().send(requestToken, HttpResponse.BodyHandlers.ofString());

		ObjectMapper mapper = new ObjectMapper();
		AmAuth amadeusAuthFields = mapper.readValue(responseToken.body(), AmAuth.class);		
		String AmadeusAuthToken = amadeusAuthFields.getAccessToken();
		
			
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://test.api.amadeus.com/v1/duty-of-care/diseases/covid19-area-report?countryCode=" + countryInitials))
				.header("Authorization", "Bearer " + AmadeusAuthToken)
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		ObjectMapper mapper2 = new ObjectMapper();
		AmadeusController amadeusDataFields = mapper2.readValue(response.body(), AmadeusController.class);	
		
		return amadeusDataFields;		
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
	
	public AmadeusController getCovidData() {
		return covidData;
	}
	
	

	private static class DataRetriever {
		private static final double ATHENSLAT = 37.983810;
		private static final double ATHENSLON = 23.727539;
		//private final int MAXDIST = 15326;                        //distance between Athens and Sydney in km
		private static final int MAXDIST = 20038;                   //max distance between two points on planet earth's surface in km
		//private static final int FEATUREMAX = 10;   
		//private static final int FEATUREMIN = 0;
		private static final int TEMPMAX = 331;
		private static final int TEMPMIN = 184;        
		private static final double normalisedFeatures[] = new double[10];

		/**
		 * Constructor function		
		 * @throws IOException
		 * @throws WikipediaNoArcticleException
		 * @throws InterruptedException 
		 */
		private static double[] populateData(String cityName, String countryInitials, String[] termsVector, String owappid, String fsappid, boolean log) throws IOException, InterruptedException {
			if (log) System.out.printf("Processing data for " + cityName + "...");			
			retrieveFeatureScore(cityName, termsVector, fsappid);
			if (log) System.out.printf("...");
			retrieveWeatherData(cityName, countryInitials, owappid);
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
		 * @author it22046
		 * @param Double array. The city features.
		 * @return Double array. The normalized vector.
		 */
		
		private static double[] normaliser(double[] features){       
			double[] normalisedFeatures = new double[10];
			for (int i =0; i < 7; i++) {
				normalisedFeatures[i] = features[i] / 10;
			}
			normalisedFeatures[7] = featureNormalizer(features[7], TEMPMAX, TEMPMIN);              	//case of temperature         
			normalisedFeatures[8] = features[8] / 100;                                             	//case of cloud coverage         
			normalisedFeatures[9] = features[9] / MAXDIST;                      					//case of cities distance
			return normalisedFeatures;
		}
		/**
		 * Helper to the normalizedFeatures method.
		 * @author it22046
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
		 * @author it22046
		 * @param String. The name of the city.
		 * @param String. THe country code initials.
		 * @param String. The appid key required for the open weather data API.
		 * @throws IOException
		 */
		private static void retrieveWeatherData(String city, String country, String owappid) throws IOException {            
			ObjectMapper mapper = new ObjectMapper();
			OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&APPID="+owappid+""), OpenWeatherMap.class);
			normalisedFeatures[7] = weather_obj.getMain().getTemp();			
			normalisedFeatures[8] = weather_obj.getClouds().getAll();			
			normalisedFeatures[9] = calculateDistance(ATHENSLAT, ATHENSLON, weather_obj.getCoord().getLat(), weather_obj.getCoord().getLon(), 'K');			
		}

		/**
		 * Populates jackson classes with venue data from a specific location
		 * using the Foursquare API
		 * @author it22046
		 * @param A string with the authorization code for foursquare
		 * @param A string with the name for the venue (term)
		 * @param A string with the name for the location (city name)
		 * @param An integer for the limit of results to return
		 * @param A string for the method of sorting returned results
		 * @param A jackson object controller to map the JSON to.
		 * @throws IOException
		 * @throws InterruptedException
		 */	
		private static FourSquareController getVenuesFromLocation(String auth, String query, String location, int limit, String sort) throws IOException, InterruptedException {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://api.foursquare.com/v3/places/search?query=" + query + "&near=" + location + "&limit=" + limit + "&sort=" + sort))
					.header("Accept", "application/json")
					.header("Authorization", auth)
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			ObjectMapper mapper = new ObjectMapper();
			FourSquareController fields = mapper.readValue(response.body(), FourSquareController.class);
			return fields;		
		}
		/**
		 * Fetches specific venue popularity or rating values using the foursquare API. 
		 * @author it22046
		 * @param String, the FSQ code for the venue
		 * @return Double, a double in the range of 0.0 to 10.0 for rating or 0.0 to 1.0 for popularity
		 * or -1 if no rating or popularity is returned
		 * @throws IOException
		 * @throws InterruptedException
		 */	
		private static double getVenueRating(String fsq) throws IOException, InterruptedException {
			double result = 5;
			String[] field = {"rating", "popularity"};	

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://api.foursquare.com/v3/places/" + fsq + "?fields=" + field[0]))
					.header("Accept", "application/json")
					.header("Authorization", "fsq3V4uGPDvrRFXcv6I2sgiuT85a2KdNFva9nW0yBmfO5c0=")
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();

			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());		
			if (!response.body().equals("{}")) { 
				result = Double.parseDouble(response.body().replaceAll("[^0-9,.]", ""));
			}
			return result;
		}

		/**
		 * Fetches rating values from the square API for each term and calculates the average.
		 * The result is a double from 0.0 to 10.0
		 * @author it22046
		 * @param The location or city for the inquiry
		 * @param The term or venue keyword to fetch
		 * @param The app id token for the foursquare API aauthentication
		 * @return A double with the rating of the term
		 * @throws IOException
		 * @throws InterruptedException
		 */
		private static double calculateFeatureScoreByRating(String location, String query, String fsappid) throws IOException, InterruptedException {
			//String fsauth = "fsq3V4uGPDvrRFXcv6I2sgiuT85a2KdNFva9nW0yBmfO5c0=";	
			String[] sortingOptions = {"RATING", "RELEVANCE", "DISTANCE", "POPULARITY"};
			int limit = 1;						
			double sum = 0;

			FourSquareController fsFields = getVenuesFromLocation(fsappid, query, location, limit, sortingOptions[2]);			
			
			System.out.print(query + ": ");
			for (int i = 0; i < limit; i++) {
				if (fsFields.getResults().isEmpty()) {
					System.out.print("5.0");
					sum += 5;
				} else {
					sum += getVenueRating(fsFields.getResults().get(i).getFsq_id());
					System.out.print(sum + ", ");
				}				
			}			
			return sum / limit;
		}

		/*
		private static double calculateFeatureScoreByDistance(String location, String query, String fsappid) throws IOException, InterruptedException {
			String[] sortingOptions = {"RATING", "RELEVANCE", "DISTANCE", "POPULARITY"};
			int limit = 10;
			double sum = 0;
			FourSquareController fsFields = getVenuesFromLocation(fsappid, query, location, limit, sortingOptions[2]);
			int responseSize = fsFields.getResults().size();
			

			for (int i = 0; i < responseSize; i++) {
				if (fsFields.getResults().isEmpty()) {
					System.out.println(query + " not found seting 0");
					sum += 5;
					break;			
				} else {
					System.out.println(fsFields.getResults().get(i).getName());
					sum += fsFields.getResults().get(i).getDistance();
				}
			}
			System.out.println();
			System.out.println(query + " distance / total: " + sum / responseSize);
			return sum / responseSize;
		}
		/*
		/**
		 * Populates the first 7 terms with the coresponding value rating from 0.0 to 10.0
		 * @param The city name
		 * @param The String array with the terms
		 * @param The app id token for the foursquare API authentication
		 * @throws IOException
		 * @throws InterruptedException
		 */
		private static void retrieveFeatureScore(String cityName, String[] termsVector, String fsappid) throws IOException, InterruptedException {			
			for (int i = 0; i < termsVector.length; i++) {
				normalisedFeatures[i] = calculateFeatureScoreByRating(cityName, termsVector[i], fsappid);
				//normalisedFeatures[i] = calculateFeatureScoreByDistance(cityName, termsVector[i], fsappid);
			}			
		}
		
		
	}
}