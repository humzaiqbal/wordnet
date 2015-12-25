package ngordnet;

import java.util.Arrays;

import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdIn;

/**
 * Provides a simple user interface for exploring WordNet and NGram data.
 * 
 * @author [Humza Iqbal basedgod]
 */
public class NgordnetUI {
    public static void main(String[] args) throws Exception {
        String allData = "test";
        In in = new In("./ngordnet/ngordnetui.config");
        System.out.println("Reading ngordnetui.config...");
        String wordFile = in.readString();
        String countFile = in.readString();
        String synsetFile = in.readString();
        String hyponymFile = in.readString();
        System.out.println("\nBased on ngordnetui.config, using the following: " + wordFile + ", "
                + countFile + ", " + synsetFile + ", and " + hyponymFile + ".");

        System.out.println("\nFor tips on implementing NgordnetUI, see ExampleUI.java.");
        NGramMap map = new NGramMap(wordFile, countFile);
        WordNet net = new WordNet(synsetFile, hyponymFile);
        WordLengthProcessor wlp = new WordLengthProcessor();
        // Got idea from Rachel Zoll to use make a new In
        // To traverse my file to get the start and end date initially
        // previously I hardcoded dates in there
        In countsReader = new In(countFile);
        String firstCount = countsReader.readLine();
        String[] allStrings = firstCount.split(", *");
        int startDate = Integer.parseInt(allStrings[0]);
        while (countsReader.hasNextLine()) {
            allData = countsReader.readLine();
        }
        String[] lastStrings = allData.split(", *");
        int endDate = Integer.parseInt(lastStrings[0]);
        while (true) {
            try {
                System.out.print("> ");
                String line = StdIn.readLine();
                String[] rawTokens = line.split(" ");
                String command = rawTokens[0];
                String[] tokens = new String[rawTokens.length - 1];
                System.arraycopy(rawTokens, 1, tokens, 0, rawTokens.length - 1);
                switch (command) {
                case "quit":
                    return;
                case "help":
                    In help = new In("help.txt");
                    String helpStr = help.readAll();
                    System.out.println(helpStr);
                    break;
                case "range":
                    startDate = Integer.parseInt(tokens[0]);
                    endDate = Integer.parseInt(tokens[1]);
                    break;

                case "hyponyms":
                    String word = tokens[0];
                    System.out.println(Arrays.toString(net.hyponyms(word).toArray()));
                    break;
                case "count":
                    String wordTolookAt = tokens[0];
                    int year = Integer.parseInt(tokens[1]);
                    System.out.println(map.countInYear(wordTolookAt, year));
                    break;

                case "history":
                    if (tokens.length > 1) {
                        Plotter.plotAllWords(map, tokens, startDate, endDate);
                    } else {
                        Plotter.plotWeightHistory(map, tokens[0], startDate, endDate);
                    }

                    break;

                case "hypohist":
                    Plotter.plotCategoryWeights(map, net, tokens, startDate, endDate);
                    break;

                case "zipf":
                    int year1 = Integer.parseInt(tokens[0]);
                    Plotter.plotZipfsLaw(map, year1);
                    break;

                case "wordlength":
                    Plotter.plotProcessedHistory(map, startDate, endDate, wlp);
                    break;

                default:
                    System.out.println("Invalid command.");
                    break;
                }
            // Got idea from Artem Ivanov to do a try-catch so that UI
            // Does not crash with invalid inputs
            } catch (NullPointerException e) {
                System.out.println("invalid");
            } catch (IllegalArgumentException e) {
                System.out.println("invalid");
            }

        }

    }
}

