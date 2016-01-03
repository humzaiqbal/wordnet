package ngordnet;

import java.util.Collection;

public class WordLengthProcessor implements YearlyRecordProcessor {
	public double process(YearlyRecord yearlyRecord) {
		double total = 0;
		double length = 0;
		double average = 0;

		for (String string_to_iterate_over : yearlyRecord.words()) {
			length += string_to_iterate_over.length()
					* yearlyRecord.count(string_to_iterate_over);
			total += yearlyRecord.count(string_to_iterate_over);
		}
		average = length / total;

		return average;
	}
}
