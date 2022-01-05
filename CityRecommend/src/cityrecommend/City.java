package cityrecommend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import amadeus.AmadeusController;
import amadeusAuthentication.AmAuth;
import exceptions.AmadeusErrorException;
import exceptions.FoursquareNoCityException;
import exceptions.OpenWeatherCityNotFoundException;
import foursquare.FourSquareController;
import weather.OpenWeatherMap;


public class City {
	private static final double ATHENSLAT = 37.983810;
	private static final double ATHENSLON = 23.727539;
	//private final int MAXDIST = 15326;                        //distance between Athens and Sydney in km
	private static final int MAXDIST = 20038;                   //max distance between two points on planet earth's surface in km
	//private static final int FEATUREMAX = 10;   
	//private static final int FEATUREMIN = 0;
	private static final int TEMPMAX = 331;
	private static final int TEMPMIN = 184;
	
	private String owappid;
	private String fsappid;
	
	private String[] terms;

	private String cityName;
	private String countryCode;
	private double[] vectorRepresentation = new double[10];
	private long timestamp;	
	private AmadeusController covidData;
	private double score;
	


	//Required to read from json file
	public City() {
		super();
	}

	/**
	 * Copy constructor
	 * @param City object
	 */
	public City(City original) {
		this.cityName = original.cityName;
		this.countryCode = original.countryCode;
		this.vectorRepresentation = original.vectorRepresentation;
		this.timestamp = original.timestamp;
		this.covidData = original.covidData;
		this.terms = original.terms;
		this.owappid = original.owappid;
		this.fsappid = original.fsappid;
		this.score = original.score;
	}

	/**
	 * Main constructor
	 * @author it22046
	 * @param String, the name of a city
	 * @param String, the country code of the country of the city in ISO 3166 format (XX)
	 * @param A String array for the terms or keywords of places to get ratings for
	 * @param A String with the APPID key for the open weather API
	 * @param A String with the APPID for the foursquare API
	 * @param A long, a timestamp of when the object is created
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws AmadeusErrorException
	 * @throws OpenWeatherCityNotFoundException
	 * @throws FoursquareNoCityException
	 */
	public City(String cityName, String countryCode, String[] terms, String owappid, String fsappid, long timestamp) throws IOException, InterruptedException, AmadeusErrorException, OpenWeatherCityNotFoundException, FoursquareNoCityException {
		this.score = 0;
		this.cityName = cityName;
		this.countryCode = countryCode;
		this.timestamp = timestamp;
		this.terms = terms;
		this.owappid = owappid;
		this.fsappid = fsappid;
		
		
		retrieveWeatherData();
		//System.out.print( this.countryCode + ", ");
		//System.out.print("please wait...");
		//retrieveFeatureScore();
		//retrieveCovidData();
		//normalise();
		//System.out.println("done!");
	}



	/**
	 * Retrieves Covid data for a country using the Amadeus API
	 * @author it22046
	 * @param String, a country code using ISO 3166 format (XX)
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws AmadeusErrorException
	 */
	public void retrieveCovidData() throws IOException, InterruptedException, AmadeusErrorException {		
		//String client_id_22046 = "IAAOZ01yvqsGryfjLV3M2huurSACX6sr";
		//String client_secret_22046 = "TY2OztMIIaDMRMUx";			
		String client_id_22039 = "oUH4ATXGGKxJZlLOTm3fODGJTSnAfhzG";
		String client_secret_22039 = "KH4EL4AmhjepN0Bx";

		String client_id = client_id_22039;
		String client_secret = client_secret_22039;

		Thread.sleep(150);

		HttpRequest requestToken = HttpRequest.newBuilder()
				.uri(URI.create("https://test.api.amadeus.com/v1/security/oauth2/token"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.method("POST", HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret))
				.build();
		HttpResponse<String> responseToken = HttpClient.newHttpClient().send(requestToken, HttpResponse.BodyHandlers.ofString());

		ObjectMapper mapper = new ObjectMapper();
		AmAuth amadeusAuthFields = mapper.readValue(responseToken.body(), AmAuth.class);		
		String AmadeusAuthToken = amadeusAuthFields.getAccessToken();

		//System.out.println(AmadeusAuthToken);

		//for debugging
		//String coyntryCode = countryInitials;
		//String coyntryCode = "DD";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://test.api.amadeus.com/v1/duty-of-care/diseases/covid19-area-report?countryCode=" + this.countryCode))
				.header("Authorization", "Bearer " + AmadeusAuthToken)
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());		

		if (response.body().contains("errors")) {
			throw new AmadeusErrorException(this.countryCode);
		}				
		ObjectMapper mapper2 = new ObjectMapper();
		this.covidData = mapper2.readValue(response.body(), AmadeusController.class);	

	}
	
	
	@Override
	public boolean equals(Object o) {    	
		return (((City) o).getCityName().equals(this.cityName) && ((City) o).getCountryCode().equals(this.countryCode));
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
	private double calculateDistance(double lat1, double lon1, double lat2, double lon2, char unit) {
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
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/**
	 * Helper to calculateDistance.
	 * @param Double. Radians
	 * @return Double.
	 */
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}		

	/**
	 * Retrieves the from the weather data API and saves the corresponding vector values into the features array of a city class.
	 * @author it22046
	 * @param String. The name of the city.
	 * @param String. THe country code initials.
	 * @param String. The appid key required for the open weather data API.
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws OpenWeatherCityNotFoundException 
	 */
	private void retrieveWeatherData() throws IOException, InterruptedException, OpenWeatherCityNotFoundException {            
		ObjectMapper mapper = new ObjectMapper();
		String requestURL;
		//OpenWeatherMap weather_obj = mapper.readValue(new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&APPID="+owappid+""), OpenWeatherMap.class);
		if (this.countryCode == null) {
			requestURL = "http://api.openweathermap.org/data/2.5/weather?q="+this.cityName+"&APPID="+this.owappid+"";
		} else {
			requestURL = "http://api.openweathermap.org/data/2.5/weather?q="+this.cityName+","+this.countryCode+"&APPID="+this.owappid+"";
		}

		HttpRequest request = HttpRequest.newBuilder()
				//.uri(URI.create("http://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&APPID="+owappid+""))
				.uri(URI.create(requestURL))
				.header("Accept", "application/json")
				.header("Authorization", "fsq3V4uGPDvrRFXcv6I2sgiuT85a2KdNFva9nW0yBmfO5c0=")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();

		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		if (response.body().contains("404")) {
			throw new OpenWeatherCityNotFoundException(this.cityName);
		}
		OpenWeatherMap weather_obj = mapper.readValue(response.body(), OpenWeatherMap.class);
		countryCode = weather_obj.getSys().getCountry();
		this.vectorRepresentation[7] = weather_obj.getMain().getTemp();			
		this.vectorRepresentation[8] = weather_obj.getClouds().getAll();			
		this.vectorRepresentation[9] = calculateDistance(ATHENSLAT, ATHENSLON, weather_obj.getCoord().getLat(), weather_obj.getCoord().getLon(), 'K');			
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
	 * @throws FoursquareNoCityException 
	 */	
	private FourSquareController getVenuesFromLocation(String auth, String query, String location, String countryCode, int limit, String sort) throws IOException, InterruptedException, FoursquareNoCityException {
		String requestURL;

		if (countryCode == null) {
			requestURL = "https://api.foursquare.com/v3/places/search?query=" + query + "&near=" + location + "&limit=" + limit + "&sort=" + sort;
		} else {
			requestURL = "https://api.foursquare.com/v3/places/search?query=" + query + "&near=" + location + "," + countryCode +"&limit=" + limit + "&sort=" + sort;		
		}		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(requestURL))
				.header("Accept", "application/json")
				.header("Authorization", auth)
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

		if (response.body().contains("message")) {
			throw new FoursquareNoCityException(location);
		}

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
	private double getVenueRating(String fsq) throws IOException, InterruptedException {
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
	 * Populates the first 7 terms with the coresponding value rating from 0.0 to 10.0
	 * @author it22046
	 * @param The city name
	 * @param The String array with the terms
	 * @param The app id token for the foursquare API authentication
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws FoursquareNoCityException 
	 */
	public void retrieveFeatureScore() throws IOException, InterruptedException, FoursquareNoCityException {			
		for (int i = 0; i < this.terms.length; i++) {
			this.vectorRepresentation[i] = calculateFeatureScoreByRating(this.cityName, this.countryCode, this.terms[i], this.fsappid);			
		}		
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
	 * @throws FoursquareNoCityException 
	 */
	private double calculateFeatureScoreByRating(String location, String countryCode, String query, String fsappid) throws IOException, InterruptedException, FoursquareNoCityException {	
		String[] sortingOptions = {"RATING", "RELEVANCE", "DISTANCE", "POPULARITY"};
		int limit = 1;						
		double sum = 0;

		FourSquareController fsFields = getVenuesFromLocation(fsappid, query, location, countryCode, limit, sortingOptions[1]);

		for (int i = 0; i < limit; i++) {
			if (fsFields.getResults().isEmpty()) {
				//System.out.print("5.0");
				sum += 5.0;
			} else {
				sum += getVenueRating(fsFields.getResults().get(i).getFsqId());
				//System.out.print(sum + ", ");
			}				
		}			
		return sum / limit;
	}

	/**
	 * Normalizes the vector of the cities of the city class.
	 * @author it22046
	 * @param Double array. The city features.
	 * @return Double array. The normalized vector.
	 */
	public void normalise() {		
		
		for (int i =0; i < 7; i++) {
			this.vectorRepresentation[i] = this.vectorRepresentation[i] / 10;
		}
		this.vectorRepresentation[7] = featureNormalizer(this.vectorRepresentation[7], TEMPMAX, TEMPMIN);              	//case of temperature         
		this.vectorRepresentation[8] = this.vectorRepresentation[8] / 100;                                             	//case of cloud coverage         
		this.vectorRepresentation[9] = this.vectorRepresentation[9] / MAXDIST;                      					//case of cities distance

	}
	/**
	 * Helper to the normalizedFeatures method.
	 * @author it22046
	 * @param Double. The feature of a vector of a city class.
	 * @param Integer. The max value of the normalizer function.
	 * @param Integer. The min value of the normalizer function.
	 * @return Double. The normalized number.
	 */
	private double featureNormalizer(double feature, int max, int min) {
		return (feature - min) / (max - min);
	}	

	
	/**
	 * Setters and getters below this point
	 * 
	 */
	
	
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

	public String getCountryCode() {
		return this.countryCode;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getOwappid() {
		return owappid;
	}

	public void setOwappid(String owappid) {
		this.owappid = owappid;
	}

	public String getFsappid() {
		return fsappid;
	}

	public void setFsappid(String fsappid) {
		this.fsappid = fsappid;
	}

	public String[] getTerms() {
		return terms;
	}

	public void setTerms(String[] terms) {
		this.terms = terms;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setCovidData(AmadeusController covidData) {
		this.covidData = covidData;
	}
	
	
	
	

}