package cityrecommend;

import java.util.ArrayList;
import org.apache.commons.lang.SerializationUtils;

public class PerceptronElderTraveller implements PerceptronTraveller{
    private double[] weightBias = {-0.2,-0.3,0.6,0.8,0.7,1.0,0.6,0.1,-0.1,0.2};
    private double bias = -1.7;    
    private ArrayList<City> recCities = new ArrayList<>();
    

    public double[] getWeightBias() {
        return weightBias;
    }
    

    public double getBias() {
        return bias;
    }
    

    public ArrayList<City> getRecCities() {
        return recCities;
    }
    

    public void setWeightBias(double[] weightBias) {
        this.weightBias = weightBias;
    }
    

    public void setBias(double Bias) {
        this.bias = Bias;
    }
    
    
    public ArrayList<City> recommend(ArrayList<City> cities) {
        for (int i = 0; i < cities.size(); i++) {
            if (sumVector(cities.get(i).getVectorRepresentation()) > 0){
                recCities.add(cities.get(i));
            }
        } 
        for (int i=0; i < recCities.size(); i++) {
    		recCities.get(i).setCityName(recCities.get(i).getCityName().substring(0,1).toUpperCase() + recCities.get(i).getCityName().substring(1).toLowerCase());
    	}
        return recCities;
    }
    
    
    private double sumVector(double[] vectorRepresantation){
        double sum = 0;
        double[] tempMatrix = (double[]) SerializationUtils.clone(vectorRepresantation);
        for (int i = 0; i < tempMatrix.length; i++){
                tempMatrix[i] = tempMatrix[i] * weightBias[i];
                sum += tempMatrix[i];
            }
        return sum += bias;
    }
    

    public ArrayList<City> recommend(ArrayList<City> cities, boolean UpLowCased){
        ArrayList<City> tempCities = recommend(cities);
        for (int i = 0; i < tempCities.size(); i++) {
            if (UpLowCased){
                tempCities.get(i).setCityName(recCities.get(i).getCityName().toUpperCase());
            } else {
                tempCities.get(i).setCityName(recCities.get(i).getCityName().toLowerCase());                
            }    
        }
        return tempCities;
    }
}
