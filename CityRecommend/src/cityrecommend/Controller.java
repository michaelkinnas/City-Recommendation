package cityrecommend;

import exception.WikipediaNoArcticleException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
	private static final String[] CITIES = new String[] {"Stockholm", "Tripei", "Los Angeles", "Edinburgh", "Tokyo", "Moscow", "Rhodes", "Rhodes"};
	private static final String[] COUNTRIES = new String[] {"SE", "NO", "US", "GB", "JP", "RU", "GR", "GR"};
	private static final boolean LOG = true; // set to true to turn on status logs print outs in the terminal
	private static final String FILEPATH = "save01.json";

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {    	  	
		ArrayList<City> cities = new ArrayList<>();
		HashMap<String, ArrayList<String>> citiesMap = new HashMap<>();
		File saveFile = new File(FILEPATH);

		if (saveFile.exists()) {       
			try { 
				//cities = readJSON(saveFile);
			} catch (Exception e) {
				System.out.print(e.getMessage());
				System.out.print(e.getStackTrace());            	
			}
		} else {
			try {
				addCities(cities);
				//writeJSON(cities, saveFile);
			} catch (Exception e) {
				System.out.print(e.getMessage());
				System.out.print(e.getStackTrace());        		
			}        	
		}

		PerceptronYoungTraveller youngPe = new PerceptronYoungTraveller();
		ArrayList<City> recommendedYoung = youngPe.recommend(cities, true);
		System.out.printf("Recommended cities for young travellers:\t");
		printCityNames(recommendedYoung);
		System.out.println("Closest city for young travellers:\t\t" + cityDistance(youngPe).getCityName() + "\n");


		PerceptronMiddleTraveller middlePe = new PerceptronMiddleTraveller();
		ArrayList<City> recommendedMiddle = middlePe.recommend(cities, false);
		System.out.printf("Recommended cities for middle travellers:\t");
		printCityNames(recommendedMiddle);
		System.out.println("Closest city for middle travellers:\t\t" + cityDistance(middlePe).getCityName() + "\n");	


		PerceptronElderTraveller elderPe = new PerceptronElderTraveller();        
		ArrayList<City> recommendedElder = elderPe.recommend(cities);
		System.out.printf("Recommended cities for elder travellers:\t");
		printCityNames(recommendedElder);
		System.out.println("Closest city for elder travellers:\t\t" + cityDistance(elderPe).getCityName() + "\n");

		/*
		System.out.println("\t\tSorted:");
		ArrayList<City> recommendedYoungSorted = young.sortRecommendations(recommendedYoung);
		printRecommendedCities(young, recommendedYoungSorted);
		ArrayList<City> recommendedMiddleSorted = middle.sortRecommendations(recommendedMiddle);
		printRecommendedCities(middle, recommendedMiddleSorted);
		ArrayList<City> recommendedElderSorted = elder.sortRecommendations(recommendedElder);
		printRecommendedCities(elder, recommendedElderSorted);
		 */

		/*
		makeHashMap(cities, citiesMap);
		System.out.println("\n\t\tHashMap:");
		System.out.println(citiesMap);
		 */
	}
	/**
	 * Initializes the process of adding new city objects by using the APIs from the internet.
	 * @param An empty ArrayList to add city objects to.
	 * @return The filled ArrayList with the added cities.
	 */
	private static ArrayList<City> addCities(ArrayList<City> cities) {
		int found;	   
		for (int i = 0; i < CITIES.length; i++) {
			Date date = new Date();
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
	 * Itereates over an arrayList of city objects and prints their names
	 * @param ArrayList of city objects.
	 */
	private static void printCityNames(ArrayList<City> recommended) {		
		for (City city: recommended) {
			System.out.printf(city.getCityName() + ", ");
		}
		System.out.println();
	}


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
}    
