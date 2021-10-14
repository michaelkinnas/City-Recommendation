/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cityrecommend;

import java.util.Arrays;
/**
 *
 * @author Michail Kinnas it22046
 */
public class City {
    private int hardFeatureMax = 10; //THIS WILL CHANGE
    private int hardFeatureMin = 0; //THIS WILL CHANGE
    
    private int hardTempMax = 331;
    private int hardTempMin = 184;
    
    private int maxDist = 15326;    //distance between Athens and Sydney
    
    private double[] features;

    public City(double[] features) {
        this.features = this.normalizedFeatures(features);
    }
    
    private double[] normalizedFeatures(double[] features){       
        double[] normalizedArray = new double[10];
        
        for (int i = 0; i <= 6; i++) {
            normalizedArray[i] = featureNormalizer(features[i], hardFeatureMax, hardFeatureMin); //case of city features  
        }
        
        normalizedArray[7] = featureNormalizer(features[7], hardTempMax, hardTempMin);          //case of temperature         
        normalizedArray[8] = features[8] / 100;                                                 //case of cloud coverage         
        normalizedArray[9] = geodesicNormalizer(features[9], maxDist);                        //case of cities distance
        
        return normalizedArray;
    }    
    
    private double featureNormalizer(double feature, int max, int min) {
        return (feature - min) / (max - min);
    }
    
    private double geodesicNormalizer(double distance, int max) {        
        return distance / max;        
        
    }
    
    public void testPrint() {
        System.out.println(Arrays.toString(features));
    }
}
