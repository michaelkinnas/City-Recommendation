package cityrecommend;

import java.util.ArrayList;

public interface PerceptronTraveller {
    ArrayList<City> recommend(ArrayList<City> cities); 
    ArrayList<City> recommend(ArrayList<City> cities, boolean UpLowCased);
    ArrayList<City> getRecCities();   
}