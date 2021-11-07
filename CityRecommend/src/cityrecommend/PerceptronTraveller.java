package cityrecommend;

import java.util.ArrayList;

public interface PerceptronTraveller {
    
    public ArrayList<City> recommend(ArrayList<City> cities); 
    
    public ArrayList<City> recommend(ArrayList<City> cities, boolean UpLowCased);
}
