package cityrecommend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {
    private static final String APPID22046 = "32fc4065e28603f29c061d7064f10147"; //id of 22046    
    private static final String[] TERMSVECTOR = new String[] {"bar","beach","mountain","museum","hotel","transport","site"};    
    private static final boolean LOG = true; // set to true to turn on status logs prints in the terminal

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ArrayList<City> cities = new ArrayList<>();
        
        cities.add(new City("Rome", "IT", APPID22046, TERMSVECTOR, LOG));
        cities.add(new City("Los Angeles", "US", APPID22046, TERMSVECTOR, LOG));
        cities.add(new City("Stockholm", "SE", APPID22046, TERMSVECTOR, LOG));
        cities.add(new City("Stavanger", "NO", APPID22046, TERMSVECTOR, LOG));
        cities.add(new City("Dublin", "IE", APPID22046, TERMSVECTOR, LOG));
        
        cities.add(new City("Edinburgh", "GB", APPID22046, TERMSVECTOR, LOG));
        cities.add(new City("Tokyo", "JP", APPID22046, TERMSVECTOR, LOG));
        cities.add(new City("Moscow", "RU", APPID22046, TERMSVECTOR, LOG));
        cities.add(new City("Cairo", "EG", APPID22046, TERMSVECTOR, LOG));
        cities.add(new City("Rhodes", "GR", APPID22046, TERMSVECTOR, LOG));
        
        System.out.println("TERMSVECTOR: \t\t" + Arrays.toString(TERMSVECTOR));
                
        for (int i = 0; i < cities.size(); i++) {
            System.out.println("City: " + cities.get(i).getCityName() + "\t\tfeatures: " + Arrays.toString(cities.get(i).getNormalizedFeatures()));
        }
    }
}