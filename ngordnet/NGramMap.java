package ngordnet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import edu.princeton.cs.introcs.In;

public class NGramMap {
    private HashMap<String, TimeSeries<Integer>> wcoun = new HashMap<String, TimeSeries<Integer>>();
    private TimeSeries<Long> frequencyPeryear = new TimeSeries<Long>();

    /** Constructs an NGramMap from WORDSFILENAME and countsFileName. */
    public NGramMap(String wordsFilename, String countsFileName) {
        // Makes a new TimeSeries
        TimeSeries<Long> dummySeries = new TimeSeries<Long>();
        // Makes a hashtable of hashtables so that I can access the word, the
        // year, and the occurences
        HashMap<String, TimeSeries<Integer>> wordsCoun = new HashMap<String, TimeSeries<Integer>>();
        // Make an In to read the file
        In words = new In(wordsFilename);
        // Put all the lines into an array
        String[] wordString = words.readAllLines();
        // Loop over each line
        for (String line : wordString) {
            // Separate by tabs
            // Got the idea to do line.split from Jeffrey Zhang
            // Otherwise my code would give me an exception when I submitted
            // To the autograder
            String[] stringOfWords = line.split("\\s");
            // Get the word
            String word = stringOfWords[0];
            if (wordsCoun.containsKey(word)) {
                Integer year = Integer.parseInt(stringOfWords[1]);
                Integer count = Integer.parseInt(stringOfWords[2]);
                wordsCoun.get(word).put(year, count);
            } else {
                // Make a time series which maps from the year to Count
                TimeSeries<Integer> yearToCount = new TimeSeries<Integer>();
                // I put the above Time Series as a value to my word
                wordsCoun.put(word, yearToCount);
                // This integer will be the year
                Integer year = Integer.parseInt(stringOfWords[1]);
                // This integer will be the number of occurences
                Integer count = Integer.parseInt(stringOfWords[2]);
                // I put the year and the number of words into my Time Series
                // which I put int
                // my hash table
                wordsCoun.get(word).put(year, count);

            }
        }

        // Sets the instance hashtable to be the one that we just constructed
        this.wcoun = wordsCoun;

        // Makes an In so that we can read our csvs into a Time Series
        In countsIn = new In(countsFileName);
        while (!countsIn.isEmpty()) {
            String tracker = countsIn.readLine();
            Scanner allNumber = new Scanner(tracker).useDelimiter(", *");
            Integer year = allNumber.nextInt();
            Long count = allNumber.nextLong();
            dummySeries.put(year, count);
        }
        this.frequencyPeryear = dummySeries;

    }

    /**
     * Returns the absolute Count of WORD in the given YEAR. If the word did not
     * appear in the given year, return 0.
     */
    public int countInYear(String word, int year) {
        for (String wordToIterate : wcoun.keySet()) {
            if (wordToIterate.equals(word)) {
                if (wcoun.get(word).containsKey(year)) {
                    return wcoun.get(word).get(year).intValue();
                }
            }

        }
        return 0;
    }

    /** Returns a defensive copy of the YearlyRecord of WORD. */
    public YearlyRecord getRecord(int year) {
        YearlyRecord thisYear = new YearlyRecord();
        for (String wordToIterate : wcoun.keySet()) {
            if (wcoun.get(wordToIterate).containsKey(year)) {
                thisYear.put(wordToIterate, wcoun.get(wordToIterate).get(year).intValue());
            }
        }
        return thisYear;

    }

    /** Returns the total number of words recorded in all volumes. */
    public TimeSeries<Long> totalCountHistory() {
        return frequencyPeryear;
    }

    /** Provides the history of WORD between STARTYEAR and ENDYEAR. */
    public TimeSeries<Integer> countHistory(String word, int startYear, int endYear) {
        if (!wcoun.containsKey(word)) {
            return null;
        } else {
            return new TimeSeries<Integer>(wcoun.get(word), startYear, endYear);
        }

    }

    /** Provides a defensive copy of the history of WORD. */
    public TimeSeries<Integer> countHistory(String word) {
        // Makes a new Time Series and then I loop over
        // My hash table and check if I find my input word
        // If so I loop through every year associated with the
        // word and put that in along with its value to my Time Series
        if (!wcoun.containsKey(word)) {
            return null;
        } else {
            return new TimeSeries<Integer>(wcoun.get(word));
        }

    }

    /** Provides the relative frequency of WORD between STARTYEAR and ENDYEAR. */
    public TimeSeries<Double> weightHistory(String word, int startYear, int endYear) {
        if (!wcoun.containsKey(word)) {
            return null;
        } else {
            TimeSeries<Long> partialSeries = new TimeSeries<Long>(frequencyPeryear, startYear,
                    endYear);
            TimeSeries<Integer> partialHistory = new TimeSeries<Integer>(countHistory(word,
                    startYear, endYear));
            return partialHistory.dividedBy(partialSeries);

        }

    }

    /** Provides the relative frequency of WORD. */
    public TimeSeries<Double> weightHistory(String word) {
        if (!wcoun.containsKey(word)) {
            return null;
        } else {
            return countHistory(word).dividedBy(frequencyPeryear);
        }

    }

    /**
     * Provides the summed relative frequency of all WORDS between STARTYEAR and
     * ENDYEAR.
     */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words, int startYear,
            int endYear) {
        TimeSeries<Double> sumSeries = new TimeSeries<Double>();
        for (String wordToIterate : words) {
            TimeSeries<Double> history = weightHistory(wordToIterate, startYear, endYear);
            // Got idea from Brandon Lin and Jeffrey Zhang to determine whether the 
            //time series is null
            // This also applies for the method below
            if (history != null) {
                 // Got reminded by Jacky Lau that I can't simply do sumSeries.plus(stuff)
                sumSeries = sumSeries.plus(weightHistory(wordToIterate, startYear, endYear));
            }

        }
        return sumSeries;
    }

    /** Returns the summed relative frequency of all WORDS. */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words) {
        TimeSeries<Double> sumSeries = new TimeSeries<Double>();
        for (String wordToiterate : words) {
            TimeSeries<Double> history = weightHistory(wordToiterate);
            if (history != null) {
                sumSeries = sumSeries.plus(weightHistory(wordToiterate));
            }

        }
        return sumSeries;

    }

    /**
     * Provides processed history of all words between STARTYEAR and ENDYEAR as
     * processed by YRP.
     */
    public TimeSeries<Double> processedHistory(int start, int end, YearlyRecordProcessor yrp) {
        //  Got idea from Jary Xiao to loop over my Time Series
        // Which keeps track of the counts file, that way the
        // word length graph is not disjoint like it was before
        // This also applies for the method below.
        TimeSeries<Double> history = new TimeSeries<Double>();
        for (Integer yearToLookAt : frequencyPeryear.keySet()) {
            if (yearToLookAt >= start) {
                if (yearToLookAt <= end) {
                    YearlyRecord recordOfyear = getRecord(yearToLookAt);
                    history.put(yearToLookAt, yrp.process(recordOfyear));
                } else {
                    break;
                }

            }
        }
        return history;
    }

    //
    // /** Provides processed history of all words ever as processed by YRP. */
    public TimeSeries<Double> processedHistory(YearlyRecordProcessor yrp) {
        TimeSeries<Double> history = new TimeSeries<Double>();
        for (Integer yearToLookAt : frequencyPeryear.keySet()) {
            YearlyRecord recordOfyear = getRecord(yearToLookAt);
            history.put(yearToLookAt, yrp.process(recordOfyear));
        }
        return history;
    }
}
