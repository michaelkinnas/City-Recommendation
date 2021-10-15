/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cityrecommend;

/**
 *
 * @author Michail Kinnas it22046
 */
public class CityRecommend {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double[] testFeatures = {4,5,1,5,6,3,8, 260, 65, 0}; //for now input distance is ignored. It is randomly generated within the City class
        
        City testCity = new City(testFeatures);
        testCity.testPrint();
    }
}
