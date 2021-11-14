package cityrecommend;

import exception.WikipediaNoArcticleException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Controller {
	private static final String APPID22046 = "32fc4065e28603f29c061d7064f10147"; //id of 22046    
	private static final String[] TERMSVECTOR = new String[] {"bar","beach","restaurant","museum","hotel","transport","temple"};
	private static final String[] CITIES = new String[] {"Stockholm", "Tripei", "Los Angeles", "Edinburgh", "Tokyo", "Moscow", "Rhodes"};
	private static final String[] COUNTRIES = new String[] {"SE", "NO", "US", "GB", "JP", "RU", "GR"};
	private static final boolean LOG = true; // set to true to turn on status logs print outs in the terminal
	private static final String FILEPATH = "save01.json";

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<City> cities = new ArrayList<>();
		HashMap<String, ArrayList<String>> citiesHash = new HashMap<>();
		File saveFile = new File(FILEPATH);

		if (saveFile.exists()) {       
			try { 
				cities = readJSON(saveFile);
			} catch (Exception e) {
				System.out.print(e.getMessage());
				System.out.print(e.getStackTrace());            	
			}
		} else {
			try {
				addCities(cities);
				writeJSON(cities, saveFile);
			} catch (Exception e) {
				System.out.print(e.getMessage());
				System.out.print(e.getStackTrace());        		
			}        	
		}

		PerceptronYoungTraveller young = new PerceptronYoungTraveller();
		ArrayList<City> recommendedYoung = young.recommend(cities, true);
		printRecommendedCities(young, recommendedYoung);
		printClosestCity(young);        
		PerceptronMiddleTraveller middle = new PerceptronMiddleTraveller();
		ArrayList<City> recommendedMiddle = middle.recommend(cities, false);
		printRecommendedCities(middle, recommendedMiddle);
		printClosestCity(middle);   
		PerceptronElderTraveller elder = new PerceptronElderTraveller();        
		ArrayList<City> recommendedElder = elder.recommend(cities);
		printRecommendedCities(elder, recommendedElder);
		printClosestCity(elder);


		System.out.println("\t\tSorted:");
		ArrayList<City> recommendedYoungSorted = young.sortRecommendations(recommendedYoung);
		printRecommendedCities(young, recommendedYoungSorted);
		ArrayList<City> recommendedMiddleSorted = middle.sortRecommendations(recommendedMiddle);
		printRecommendedCities(middle, recommendedMiddleSorted);
		ArrayList<City> recommendedElderSorted = elder.sortRecommendations(recommendedElder);
		printRecommendedCities(elder, recommendedElderSorted);

		makeHashMap(cities, citiesHash);
	}

	//calculates the shortest distance between a city object and Athens 
	public static City cityDistance(PerceptronTraveller pTrv) throws IndexOutOfBoundsException {
		int index =0;
		double Min = 1;
		for (int i = 0; i <  pTrv.getRecCities().size(); i++) {
			if (pTrv.getRecCities().get(i).getVectorRepresentation()[9] < Min) {
				Min =  pTrv.getRecCities().get(i).getVectorRepresentation()[9];
				index = i;
			}
		}
		return pTrv.getRecCities().get(index);
	}
	
	/**TODO Add a second attribute of a hashmap to pass the city-country pairs
	 * Creates and adds city objects in an ArrayList. If they already exist in the arrayList
	 * it prints out the timestamp the city object was added instead.
	 * @param An arraylist.
	 * @return An arraylist.
	 */
	private static ArrayList<City> addCities(ArrayList<City> cities) {
		int found;	   
		for (int i = 0; i < CITIES.length; i++) {
			Date date = new Date();  //needs to be in here to update current time  
			try {
				if ((found = alreadyExists(CITIES[i], cities)) > 0) {
					System.out.println("A City with the name \"" + cities.get(found).getCityName() + "\" has already been added on " + cities.get(found).getTimestamp());
				} else {        		   
					cities.add(new City(CITIES[i], COUNTRIES[i], TERMSVECTOR, APPID22046, LOG, date.getTime()));	
				}        	   
			}
			catch (WikipediaNoArcticleException e0) {
				System.out.println(e0.getMessage());
			}
			catch (Exception e1) {             
				System.out.println(e1.getMessage());
			}
		}
		System.out.println();
		return cities;
	}


	/**
	 * Iterates over an arraylist of cities and returns the index of the 
	 * city if found in the arraylist. It returns -1 otherwise.
	 * @param String of a name of a city
	 * @param ArrayList of already saved cities
	 * @return An integer
	 */
	private static int alreadyExists(String cityName, ArrayList<City> cities) {
		for (int i = 0; i < cities.size(); i++) {		   
			if (cities.get(i).getCityName().equals(cityName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Prints out the closest city to Athens from a Perceptron's recommended cities list.
	 * It uses the cityDistance() method.
	 * @param A perceptron object
	 */
	private static void printClosestCity(PerceptronTraveller p) {
		try {            
			System.out.println("Closest city for elder travellers:\t\t" + cityDistance(p).getCityName() + "\n"); 
		} catch (IndexOutOfBoundsException e2) {
			System.out.println("No recommended cities for this traveller");
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		}
	}

	/**
	 * Prints all cities in an arraylist
	 * @param A perceptron object
	 * @param An arraylist of cities
	 */
	private static void printRecommendedCities(PerceptronTraveller p, ArrayList<City> recommended) {
		String pName = p.getClass().getSimpleName();	   
		String oName = null;	   
		//Set name for each perceptron type
		if (pName.equals("PerceptronYoungTraveller")) {
			oName = "young";
		} else if (pName.equals("PerceptronMiddleTraveller")) {
			oName = "middle";
		} else if (pName.equals("PerceptronElderTraveller")) {
			oName = "elder";
		}	   
		System.out.printf("Recommended cities for " + oName + " travellers:\t");
		for (City city: recommended) {
			System.out.printf(city.getCityName() + ", ");
		}
		System.out.println();
	}
}    
