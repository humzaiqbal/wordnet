package ngordnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

public class TimeSeries<T extends Number> extends TreeMap<Integer, T> {

    /** Constructs a new empty TimeSeries. */
    public TimeSeries() {
        // Got idea from Jeffrey Zhang to use super(), since that calls the TreeMap constructor
        super();
    }

    /**
     * Returns the years in which this time series is valid. Doesn't really need
     * to be a NavigableSet.
     */
    private Set<Integer> validYears(int startYear, int endYear) {
        // make a set of all the years
        HashSet<Integer> validYears = new HashSet<Integer>();
        // put each year into my time series if it is valid
        for (Integer currentKey : this.keySet()) {
            if (startYear <= currentKey && currentKey <= endYear) {
                validYears.add(currentKey);
            }
        }
        return validYears;

    }

    /** Creates a copy of TS, but only between STARTYEAR and ENDYEAR. */
    public TimeSeries(TimeSeries<T> ts, int startYear, int endYear) {
        this.clear();
        int temp = startYear;
        while (temp <= endYear) {
            if (ts.containsKey(temp)) {
                this.put(temp, ts.get(temp));
            }
            temp++;
        }

    }

    /** Creates a copy of TS. */
    public TimeSeries(TimeSeries<T> ts) {
        this.clear();
        // Enhanced for loop over all the keys and put in each key and value
        for (Integer currentKey : ts.keySet()) {
            this.put(currentKey, ts.get(currentKey));
        }

    }

    /**
     * Returns the quotient of this time series divided by the relevant value in
     * ts. If ts is missing a key in this time series, return an
     * IllegalArgumentException.
     */
    public TimeSeries<Double> dividedBy(TimeSeries<? extends Number> ts) {
        TimeSeries<Double> newSeries = new TimeSeries<Double>();
        // Determines whether the sizes of the two time series are the same, if
        // they are not I raise an exception
        for (Integer keyToLoopOver : this.keySet()) {
            if (ts.containsKey(keyToLoopOver)) {
                double quotient = (double) this.get(keyToLoopOver)
                        .doubleValue()
                        / (double) ts.get(keyToLoopOver).doubleValue();
                newSeries.put(keyToLoopOver, quotient);
            } else {
                throw new IllegalArgumentException();
            }
        }
        return newSeries;

    }

    /**
     * Returns the sum of this time series with the given ts. The result is a a
     * Double time series (for simplicity).
     */
    public TimeSeries<Double> plus(TimeSeries<? extends Number> ts) {
        TimeSeries<Double> newSeries = new TimeSeries<Double>();
        // Loop over my local series and check to see if my given series has the
        // same keys
        // If so I add the results otherwise I just use the value of the given
        // series

        for (Integer keyToLoopOver : this.keySet()) {
            if (ts.containsKey(keyToLoopOver)) {
                // Got idea from StackOverFlow to do doubleValue(), applies for all useages of it
                double sum = this.get(keyToLoopOver).doubleValue()
                        + ts.get(keyToLoopOver).doubleValue();
                newSeries.put(keyToLoopOver, sum);
            } else {
                newSeries.put(keyToLoopOver, this.get(keyToLoopOver)
                        .doubleValue());
            }
        }
        for (Integer keyToLoopOver : ts.keySet()) {
            // Do the same thing as above but with my passed in series, but
            // first I check to see if
            // My returned series already has the given key, in that case I
            // ignore it
            if (!newSeries.containsKey(keyToLoopOver)) {
                newSeries.put(keyToLoopOver, ts.get(keyToLoopOver)
                        .doubleValue());
            }
        }
        return newSeries;

    }

    /** Returns all years for this time series (in any order). */
    public Collection<Number> years() {
        // Make a hashset of the years
        LinkedHashSet<Number> allYears = new LinkedHashSet<Number>();
        for (Integer currentYear : this.keySet()) {
            allYears.add(currentYear);
        }
        return allYears;

    }

    /** Returns all data for this time series (in any order). */
    public Collection<Number> data() {
        // Got idea from Riley Murray via Piazza to use an ArrayList
        // Rather than a Set since the same data could appear
        // Multiple times
        ArrayList<Number> allData = new ArrayList<Number>();
        for (Integer currentYear : this.keySet()) {
            allData.add(this.get(currentYear));
        }
        return allData;
    }

}
