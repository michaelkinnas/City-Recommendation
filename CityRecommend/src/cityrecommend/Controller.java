package cityrecommend;

import java.util.Arrays;

public class Controller {
    private static String appid = "32fc4065e28603f29c061d7064f10147";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {  
        City testCity = new City("Rome", "It", appid);
        System.out.println("Normalized features: " +Arrays.toString(testCity.getNormalizedFeatures()));
    }
}
