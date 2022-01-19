package cityrecommend;

import java.util.ArrayList;
import java.util.Collections;

import comparators.GeodesicCompare;

public class PerceptronYoungTraveller implements PerceptronTraveller{
	private double[] weightBias = {0.8,0.8,0.0,-0.9,0.1,0.6,-0.8,0.8,-0.7,-0.7};
	//private double bias = -0.56;
	private double bias = 0;
	private ArrayList<City> recCities = new ArrayList<>();
	
	
	
	/**
	 * Retrieves an Array.
	 * @return  weightBias Array.
	 */
	public double[] getWeightBias() {
		return weightBias;
	}

	/**
	 * Retrieves a variable.
	 * @return Variable bias.
	 */
	public double getBias() {
		return bias;
	}

	/**
	 * Retrieves an ArrayList.
	 * @return ArrayList with City Objects.
	 */
	public ArrayList<City> getRecCities() {
		return recCities;
	}

	/**
	 * Sets the value of a variable.
	 * @param Array with weights.
	 */
	public void setWeightBias(double[] weightBias) {
		this.weightBias = weightBias;
	}

	/**
	 * Sets the value of a variable.
	 * @param Variable Bias.
	 */
	public void setBias(double Bias) {
		this.bias = Bias;
	}

	/**
	 * Differentiates the Objects of an ArrayList and puts the Objects needed on another ArrayList.
	 * @param An ArraList of City Objects.
	 * @return An ArraList of City Objects.
	 */
	public ArrayList<City> recommend(ArrayList<City> cities) {
		ArrayList<City> tempArray = new ArrayList<>();
		double score = 0;
		for (int i = 0; i < cities.size(); i++) {
			tempArray.add(new City(cities.get(i)));
		}
		for (int i = 0; i < tempArray.size(); i++) {
			score = sumVector(tempArray.get(i).getVectorRepresentation());
			if (score > 0){
				tempArray.get(i).setScore(score);		
				recCities.add(tempArray.get(i));				
			}
		}
		return recCities;
	}

	/**
	 * Multiplies two Arrays with each other and adds the results of the multiplication  .
	 * @param  Array with data of City objects.
	 * @return A variable with the result of some mathematical operations.
	 */
	private double sumVector(double[] vectorRepresantation){
		double sum = 0;
		double[] tempMatrix = new double[vectorRepresantation.length];
		for (int i = 0; i < vectorRepresantation.length; i++ ) {
			tempMatrix[i] = vectorRepresantation[i];
		}	
		for (int i = 0; i < tempMatrix.length; i++){
			tempMatrix[i] = tempMatrix[i] * weightBias[i];
			sum += tempMatrix[i];
		}
		
		return sum += bias;
	}

	/**
	 * Turns the name attribute of objects in given ArrayList to lowercase or uppercase.
	 * @param An ArrayList of City Objects.
	 * @param Variable signifying type(lowercase or upercase) of letterform.
	 * @return An ArrayList of City Objects.
	 */
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
	/**
	 * Sorts an ArrayList using a Comparator class.
	 * @param An ArrayList of City Objects.
	 * @return An ArrayList of City Objects.
	 */
	public ArrayList<City> sortRecommendations(ArrayList<City> recCities) {
		ArrayList<City> tempArray = new ArrayList<>();
		for (int i = 0; i < recCities.size(); i++) {
			tempArray.add(new City(recCities.get(i)));
		}
		GeodesicCompare geodesicCompare = new GeodesicCompare();
		Collections.sort(tempArray, geodesicCompare);
		return tempArray;
	}	
}
