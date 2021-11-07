package cityrecommend;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {
	private static final String APPID22046 = "32fc4065e28603f29c061d7064f10147"; //id of 22046    
	private static final String[] TERMSVECTOR = new String[] {"bar","beach","restaurant","museum","hotel","transport","temple"};
	private static final String[] CITIES = new String[] {"Las Vegas", "Stockholm", "Stavanger", "Dublin", "Edinburgh", "Tokyo", "Moscow", "Rhodes", "Skoulikomermigotripa"};
	private static final String[] COUNTRIES = new String[] {"US", "SE", "NO", "IE", "GB", "JP", "RU", "GR", "AA"};
	private static final boolean LOG = true; // set to true to turn on status logs print outs in the terminal
   
    /**
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {
    	//Create city objects
        ArrayList<City> cities = new ArrayList<>();
        for (int i = 0; i < CITIES.length; i++) {        
        	cities.add(new City(CITIES[i], COUNTRIES[i], TERMSVECTOR, APPID22046, LOG));		
        }
        
        //create perceptron objects
        PerceptronYoungTraveller young = new PerceptronYoungTraveller();    
        PerceptronMiddleTraveller middle = new PerceptronMiddleTraveller(); 
        PerceptronElderTraveller elder = new PerceptronElderTraveller();        
        
        //Recommended cities and closest city for young     
        ArrayList<City> recommendedYoung = young.recommend(cities, false);
        System.out.printf("\nRecommended cities for young travellers:\t");        
        for (int i = 0; i < recommendedYoung.size(); i++) {
        	System.out.printf(recommendedYoung.get(i).getCityName() + ", ");
        }
        System.out.println("Closest city for young travellers: " + cityDistance(young).getCityName());
    
        //Recommended cities and closest city for middle  
        ArrayList<City> recommendedMiddle = middle.recommend(cities);
        System.out.printf("\nRecommended cities for middle travellers:\t");        
        for (int i = 0; i < recommendedMiddle.size(); i++) {
        	System.out.printf(recommendedMiddle.get(i).getCityName() + ", ");
        }
        System.out.println("\nClosest city for middle travellers: " + cityDistance(middle).getCityName());         
    
  
        //Recommended cities and closest city for elder  
        ArrayList<City> recommendedElder = elder.recommend(cities, true);
        System.out.printf("\nRecommended cities for elder travellers:\t");  
        for (int i = 0; i < recommendedElder.size(); i++) {
        	System.out.printf(recommendedElder.get(i).getCityName() + ", ");
    	}
        System.out.println("\nClosest city for elder travellers: " + cityDistance(elder).getCityName());       
   }
    
   public static City cityDistance(PerceptronTraveller pTrv) {
	   int index =0;
	   double Min = 1;
	   for (int i = 0; i <  pTrv.getRecCities().size(); i++){
		   if (pTrv.getRecCities().get(i).getVectorRepresentation()[9] < Min){
	           Min =  pTrv.getRecCities().get(i).getVectorRepresentation()[9];
	           index = i;
	       }
	   }
	   return pTrv.getRecCities().get(index);
   	}
}    
