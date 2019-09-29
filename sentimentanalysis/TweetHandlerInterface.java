package sentimentanalysis;

import java.util.Date;
import java.util.List;

/**
 * CS3354 Fall 2018 Tweet Handler Interface specification
 * @author vangelis
 * @author jelena
 * @author junye
 */
public interface TweetHandlerInterface {

    /**
     * Loads tweets from CSV file
     * @param filePath The path to the CSV file.
     * @return A list of tweets as objects.
     */
    List<AbstractTweet> loadTweetsFromText(String filePath);

    /**
     * Parses a single line from the CSV file and returns a tweet as an object.
     * @param tweetLine A string containing the contents of a single line in the CSV file.
     * @return A tweet as an object.
     */
    AbstractTweet parseTweetLine(String tweetLine);

    /**
     * Classifies a tweet as negative, neutral, or positive by using the text of the tweet.
     * @param tweet A tweet object.
     * @return 0 = negative, 2 = neutral, 4 = positive.
     */
    int classifyTweet(AbstractTweet tweet);

    /**
     * Adds a list of new tweets to the existing database.
     * @param tweets A list of tweet objects.
     */
    void addTweetsToDB(List<AbstractTweet> tweets);

    /**
     * Deletes a tweet from the database, given its id.
     * @param id The id value of the tweet.
     */
    void deleteTweet(int id);

    /**
     * Saves the database in the working directory as a serialized object.
     */
    void saveSerialDB();

    /**
     * Loads tweet database.
     */
    void loadSerialDB();

    /**
     * Searches the tweet database by user name.
     * @param user The user name to search for.
     * @return A list of tweets matching the username
     */
    List<AbstractTweet> searchByUser(String user);

    /**
     * Searches the tweet database for tweets posted on a given date.
     * @param date The date to search for.
     * @return A list of tweet objects posted on a given date.
     */
    List<AbstractTweet> searchByDate(Date date);

    /**
     * Searches the tweet database for tweets matching a given flag.
     * @param flag The flag to search for.
     * @return A list of tweet objects with a specified flag. 
     */
    List<AbstractTweet> searchByFlag(String flag);

    /**
     * Searches the tweet database for tweets matching a given substring.
     * @param substring The substring to search for.
     * @return A list of tweet objects.
     */
    List<AbstractTweet> searchBySubstring(String substring);
}
