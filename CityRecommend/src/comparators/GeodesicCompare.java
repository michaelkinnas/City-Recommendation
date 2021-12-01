package comparators;

import java.util.Comparator;

import cityrecommend.City;

public class GeodesicCompare implements Comparator<City> {
	/**
	 * @param o1 Object of City type.
	 * @param o2 Object of City type.
	 * @return A number among 1, -1, 0 depending on the results of the comparisons.
	 */
	@Override
	public int compare(City o1, City o2) {
		if (o1.getVectorRepresentation()[9] > o2.getVectorRepresentation()[9]) {
			return 1;
		}else if (o1.getVectorRepresentation()[9] < o2.getVectorRepresentation()[9]) {
			return -1;
		}else {
			return 0;
		}
	}
	
}