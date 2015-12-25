package ngordnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeMap;

public class YearlyRecord {
    private TreeMap<String, Integer> wordToCount = new TreeMap<String, Integer>();
    private TreeMap<Integer, HashSet<String>> countToWord = new TreeMap<Integer, HashSet<String>>();
    private TreeMap<String, Integer> wordToRank = new TreeMap<String, Integer>();
    // Got idea From Rachel Zoll to have a boolean to cache rankings
    private boolean isRanked = false;

    public YearlyRecord(java.util.HashMap<java.lang.String, java.lang.Integer> otherCountMap) {
        TreeMap<String, Integer> newWordMap = new TreeMap<String, Integer>(otherCountMap);
        TreeMap<Integer, HashSet<String>> newCountMap = new TreeMap<Integer, HashSet<String>>();
        TreeMap<String, Integer> newRankMap = new TreeMap<String, Integer>();
        for (String stringToIterateOver : newWordMap.keySet()) {
            if (newCountMap.containsKey(newWordMap.get(stringToIterateOver))) {
                newCountMap.get(newWordMap.get(stringToIterateOver)).add(stringToIterateOver);
            } else {
                HashSet<String> allWords = new HashSet<String>();
                allWords.add(stringToIterateOver);
                newCountMap.put(newWordMap.get(stringToIterateOver), allWords);
            }

        }

        this.wordToCount = newWordMap;
        this.countToWord = newCountMap;
        this.wordToRank = newRankMap;
    }

    public YearlyRecord() {
        YearlyRecord newRecord = this;

    }

    public void put(String word, int count) {
        // Reminded by Jacky Lau that the reason
        // I was not passing an ag test was because
        // it failed on the case where I put the same word in
        // but the fix I came up with for that (below) was my
        // own idea
        if (wordToCount.containsKey(word)) {
            countToWord.get(wordToCount.get(word)).remove(word);
        }
        wordToCount.put(word, count);
        if (countToWord.containsKey(count)) {
            countToWord.get(count).add(word);
        } else {
            HashSet<String> words = new HashSet<String>();
            countToWord.put(count, words);
            countToWord.get(count).add(word);

        }
        isRanked = false;
        wordToRank.clear();
    }

    public int size() {
        return wordToCount.size();
    }

    public int rank(String word) {
        // Start out with the default size of the submap, if there are multiple
        // words with the same rank I add that word to that size
        // Then I return size
        if (!isRanked) {
            isRanked = true;
            // Got idea from Jerry Xiao to use an ArrayList to store my words 
            //and use indexOf to grab an index
            List<String> words = new ArrayList<String>(words());
            for (String wordToIterateOver : words()) {
                wordToRank.put(wordToIterateOver, words.size() - words.indexOf(wordToIterateOver));
            }

        }
        return wordToRank.get(word);

    }

    public int count(String word) {
        return wordToCount.get(word);
    }

    public Collection<String> words() {
        // Got idea from StackOverflow to use a LinkedHashSet, applies below
        // as well
        LinkedHashSet<String> allWords = new LinkedHashSet<String>();
        for (Integer stringToIterateOver : countToWord.keySet()) {
            allWords.addAll(countToWord.get(stringToIterateOver));
        }
        return allWords;
    }

    public Collection<Number> counts() {
        LinkedHashSet<Number> allCounts = new LinkedHashSet<Number>();
        for (Integer countToIterateOver : countToWord.keySet()) {
            allCounts.add(countToIterateOver);
        }
        return allCounts;
    }
}
