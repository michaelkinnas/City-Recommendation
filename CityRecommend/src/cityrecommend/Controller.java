package cityrecommend;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    private static final String APPID22046 = "32fc4065e28603f29c061d7064f10147"; //id of 22046    
    private static final String[] TERMSVECTOR = new String[] {"bar","beach","restaurant","museum","hotel","transport","temple"};    
    private static final boolean LOG = true; // set to true to turn on status logs prints in the terminal

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException { //TEMP THROW
        ArrayList<City> cities = new ArrayList<>();
        
        cities.add(new City("Rome", "IT", TERMSVECTOR, APPID22046, LOG));
        //cities.add(new City("Los Angeles", "US", APPID22046, TERMSVECTOR, LOG));
        cities.add(new City("Stockholm", "SE", TERMSVECTOR, APPID22046, LOG));
        cities.add(new City("Stavanger", "NO", TERMSVECTOR, APPID22046, LOG));
        cities.add(new City("Dublin", "IE", TERMSVECTOR, APPID22046, LOG));
        
        cities.add(new City("Edinburgh", "GB", TERMSVECTOR, APPID22046, LOG));
        cities.add(new City("Tokyo", "JP", TERMSVECTOR, APPID22046, LOG));
        cities.add(new City("Moscow", "RU", TERMSVECTOR, APPID22046, LOG));
        cities.add(new City("Cairo", "EG", TERMSVECTOR, APPID22046, LOG));
        cities.add(new City("Rhodes", "GR", TERMSVECTOR, APPID22046, LOG));
        
        
        PerceptronYoungTraveller young = new PerceptronYoungTraveller();        
        ArrayList<City> recommendedYoung = new ArrayList<>();
        recommendedYoung = young.recommend(cities, true);
        System.out.printf("\nRecommended cities for young travellers:\t");        
        for (int i = 0; i < recommendedYoung.size(); i ++) {
        	System.out.printf(recommendedYoung.get(i).getCityName() + ", ");
        }        
        
        PerceptronMiddleTraveller middle = new PerceptronMiddleTraveller();        
        ArrayList<City> recommendedMiddle = new ArrayList<>();
        recommendedMiddle = middle.recommend(cities, true);
        System.out.printf("\nRecommended cities for middle travellers:\t");        
        for (int i = 0; i < recommendedMiddle.size(); i ++) {
        	System.out.printf(recommendedMiddle.get(i).getCityName() + ", ");
        }        
        
        PerceptronElderTraveller elder = new PerceptronElderTraveller();        
        ArrayList<City> recommendedElder = new ArrayList<>();
        recommendedElder = elder.recommend(cities, true);
        System.out.printf("\nRecommended cities for elder travellers:\t");        
        for (int i = 0; i < recommendedElder.size(); i ++) {
        	System.out.printf(recommendedElder.get(i).getCityName() + ", ");
        }
    }
}
    
