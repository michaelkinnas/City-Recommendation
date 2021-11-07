package cityrecommend;

import java.util.ArrayList;

public class PerceptronYoungTraveller implements PerceptronTraveller{
    double[] weightBias = {0.8,0.8,0.0,-0.9,0.1,0.6,-0.8,0.8,-0.7,-0.7};//TO BE IMPLEMENTED, must be a real number between -1 and 1
    double Bias = 0.7;
    ArrayList<City> recCities = new ArrayList<>();

    public double[] getWeightBias() {
        return weightBias;
    }

    public double getBias() {
        return Bias;
    }

    public ArrayList<City> getRecCities() {
        return recCities;
    }

    public void setWeightBias(double[] weightBias) {
        this.weightBias = weightBias;
    }

    public void setBias(double Bias) {
        this.Bias = Bias;
    }
    
    public ArrayList<City> recommend(ArrayList<City> cities) {
        
        int Size = cities.get(1).getNormalizedFeatures().length;
        
        for (int i = 0; i < cities.size(); i++) {
            
            double[] tempMatrix = new double[Size];
            double sum = 0;
            tempMatrix = cities.get(i).getNormalizedFeatures();
            for (int j = 0; j < Size; j++){
                tempMatrix[j] = tempMatrix[j] * weightBias[j];
                sum += tempMatrix[j];
            }
            sum += Bias;
            if (sum > 0) {
                recCities.add(cities.get(i));    
            }
        } 
        return recCities;
    }

    public ArrayList<City> recommend(ArrayList<City> cities, boolean UpLowCased){
        
        
        ArrayList<City> tempCities = recommend(cities);
        
        if (UpLowCased = true){
            for (int i = 0; i < tempCities.size(); i++) {
                tempCities.get(i).setCityName(recCities.get(i).getCityName().toUpperCase());
            }
        }else{
            for (int j = 0; j < tempCities.size(); j++) {
                tempCities.get(j).setCityName(recCities.get(j).getCityName().toLowerCase());
            }
        }
        
        return tempCities;
    }
}
