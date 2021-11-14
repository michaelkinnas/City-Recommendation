package cityrecommend;

import exception.WikipediaNoArcticleException;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
	private static final String APPID22046 = "32fc4065e28603f29c061d7064f10147"; //id of 22046    
	private static final String[] TERMSVECTOR = new String[] {"bar","beach","restaurant","museum","hotel","transport","temple"};
	private static final String[] CITIES = new String[] {"Stockholm", "Tripei", "Los Angeles", "Edinburgh", "Tokyo", "Moscow", "Rhodes"};
	private static final String[] COUNTRIES = new String[] {"SE", "NO", "US", "GB", "JP", "RU", "GR"};
	private static final boolean LOG = true; // set to true to turn on status logs print outs in the terminal
   
    /**
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {
    	//Create city objects
        ArrayList<City> cities = new ArrayList<>();
        for (int i = 0; i < CITIES.length; i++) {  
            try {
            	cities.add(new City(CITIES[i], COUNTRIES[i], TERMSVECTOR, APPID22046, LOG));	
            }
            catch (WikipediaNoArcticleException e0) {
                System.out.println(e0.getMessage());
            }
            catch (Exception e1) {
                System.out.println("Error with added city"); 
            }
        }
        
        //create perceptron objects
        PerceptronYoungTraveller young = new PerceptronYoungTraveller();    
        PerceptronMiddleTraveller middle = new PerceptronMiddleTraveller(); 
        PerceptronElderTraveller elder = new PerceptronElderTraveller();        
        
        //Recommended cities and closest city for young     
        try {
            ArrayList<City> recommendedYoung = young.recommend(cities, true);
            System.out.printf("\nRecommended cities for young travellers:\t");        
            for (int i = 0; i < recommendedYoung.size(); i++) {
            	System.out.printf(recommendedYoung.get(i).getCityName() + ", ");
            }   
            System.out.println("\nClosest city for young travellers:\t\t" + cityDistance(young).getCityName());
        } 
        catch (IndexOutOfBoundsException e2) {
            System.out.println("No recommended cities for young travellers");
        }
        catch (Exception e1) {
                System.out.println("Error with added city"); 
        }
        
        //Recommended cities and closest city for middle  
        try {
            ArrayList<City> recommendedMiddle = middle.recommend(cities);
            System.out.printf("\nRecommended cities for middle travellers:\t");        
            for (int i = 0; i < recommendedMiddle.size(); i++) {
            	System.out.printf(recommendedMiddle.get(i).getCityName() + ", ");
            }
            System.out.println("\nClosest city for middle travellers:\t\t" + cityDistance(middle).getCityName());       
        }
        catch (IndexOutOfBoundsException e2) {
            System.out.println("No recommended cities for middle travellers");
        }
        catch (Exception e1) {
                System.out.println("Error with added city"); 
        }
  
        //Recommended cities and closest city for elder  
        try {
            ArrayList<City> recommendedElder = elder.recommend(cities, false);
            System.out.printf("\nRecommended cities for elder travellers:\t");  
            for (int i = 0; i < recommendedElder.size(); i++) {
            	System.out.printf(recommendedElder.get(i).getCityName() + ", ");
            }
            System.out.println("\nClosest city for elder travellers:\t\t" + cityDistance(elder).getCityName()); 
        }
        catch (IndexOutOfBoundsException e2) {
            System.out.println("No recommended cities for elder travellers");
        }
        catch (Exception e1) {
            System.out.println("Error with added city"); 
        } 
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
}    
