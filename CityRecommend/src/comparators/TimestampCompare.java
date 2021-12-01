package comparators;

import java.util.Comparator;

import cityrecommend.City;

public class TimestampCompare implements Comparator<City> {
	/**
	 * @param o1 Object of City type.
	 * @param o2 Object of City type.
	 * @return A number among 1, -1, 0 depending on the results of the comparisons.
	 */
	@Override
	public int compare(City o1, City o2) {
		if (o1.getTimestamp() > o2.getTimestamp()) {
			return 1;
		}else if (o1.getTimestamp() < o2.getTimestamp()) {
			return -1;
		}else {
			return 0;
		}
	}
	
}