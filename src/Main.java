package src;
/**
 * Guide for MainApp
 */

import src.sentimentanalysis.AbstractTweet;
import src.tweet.TweetHandler;
import java.util.List;
import java.util.Scanner;



public class Main {

    // Used to read from System's standard input
    private static final Scanner CONSOLE_INPUT = new Scanner(System.in);
    // Database data for Tweets

    /**
     * Main method demonstrates an example of how to iterate through a set of sentences
     * that have been annotated by the Stanford NLP library.
     * @param args the command line arguments
     */
    public static void main(String[] args){

        TweetHandler handler = new TweetHandler();


        // Let user input the path of database file.
        //System.out.println("Welcome to the Sentiment Analyzer application. Please input the path of database file.");
        //DATA_FILE_NAME = CONSOLE_INPUT.nextLine();

        // Load the database first.
        handler.loadSerialDB();

        String welcomeMessage = "\nChoose one of the following functions:\n\n"
                + "\t 0. Exit program.\n"
                + "\t 1. Load new tweet text file.\n"
                + "\t 2. Classify tweets using NLP library and report accuracy.\n"
                + "\t 3. Manually change tweet class label.\n"
                + "\t 4. Add new tweets to database.\n"
                + "\t 5. Delete tweet from database (given its id).\n"
                + "\t 6. Search tweets by user, date, flag, or a matching substring.\n";

        System.out.println(handler.getTweetsContainer().size() + " tweets in temporary buffer.");
        System.out.println(handler.getTweetDatabase().size() + " tweets in database.");
        System.out.println(welcomeMessage);
        String selection = CONSOLE_INPUT.nextLine();

        while (!selection.equals("0")) {

            switch (selection) {
                case "1":
                    // 1. Load new tweet text file.
                    System.out.println("Please input absolute or relative path path to tweet csv file.");
                    System.out.println("(Example: Data/testdata.manual.2009.06.14.csv)");

                    handler.loadFile();
                    break;
                case "2":
                    // 2. Classify tweets using NLP library and report accuracy.
                    handler.classifyTweets();
                    break;
                case "3":
                    // 3. Manually change tweet class label.
                    handler.changeTweets();
                    break;
                case "4":
                    // 4. Add new tweets to database.
                    handler.addToDatabase();
                    break;
                case "5":
                    // 5. Delete tweet from database (given its id).
                    handler.deleteTweets();
                    break;
                case "6":
                    // 6. Search tweets by user, date, flag, or a matching substring.
                        List<AbstractTweet> search_Results = handler.searchTweet();

                    if (search_Results.isEmpty()) {
                        System.out.println("0 result found.");
                    } else {
                        System.out.println(search_Results.size() + " result found.");
                        handler.printList(search_Results);
                    }
                    break;
                case "h":
                    System.out.println(welcomeMessage);
                    break;
                default:
                    System.out.println("That is not a recognized command." +
                            " Please enter another command or 'h' to list the commands.");
                    break;

            }

            System.out.println(handler.getTweetsContainer().size() + " tweets in temporary buffer.");
            System.out.println(handler.getTweetDatabase().size() + " tweets in database.");
            System.out.println("Please enter another command or 'h' to list the commands.\n");

            selection = CONSOLE_INPUT.nextLine();
        }

        // Save the database when exit.
        handler.saveSerialDB();
        System.out.println("See you!");
    }
}
