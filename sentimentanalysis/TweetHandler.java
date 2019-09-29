package sentimentanalysis;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
  CS3354 Fall 2018 Tweet Handler Sample Implementation
  @author vangelis
  @author jelena
  @author junye
 */
public class TweetHandler implements TweetHandlerInterface {

    /**
     * Loads tweets from a CSV text file.
     * @param filePath The path to the CSV file.
     * @return A list of tweets as objects.
     */
    public List<AbstractTweet> loadTweetsFromText(String filePath) {
        try {
            File file = new File(filePath);
            MainApp.outputArea.append("Reading from file: " + filePath + "\n");
            if (!file.exists()) {
                //File does not exist. Requiring input again.
                MainApp.outputArea.append("File does not exist.\n");
                return null;
            } else {
                //File found.
                List<AbstractTweet> tempList = new ArrayList<AbstractTweet>();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                String readInLine;
                while ((readInLine = bufferedReader.readLine()) != null) {
                    Tweet tempTweet = parseTweetLine(readInLine);
                    tempList.add(tempTweet);
                }
                MainApp.outputArea.append("Done.\n");
                MainApp.outputArea.append(tweetDB.size() + " records in database.\n");
                MainApp.outputArea.append(tempList.size()+" records in temp list.\n");
                return tempList;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Parses a single line from the CSV file and returns a tweet as an object.
     * @param tweetLine A string containing the contents of a single line in the CSV file.
     * @return A tweet as an object.
     */
    public Tweet parseTweetLine(String tweetLine) {
        // CSV splits data with comma but it is also possible that there are commas in the text.
        // Thus we need to use (",") as the separator, not only (,)
        String[] infoFields = tweetLine.split("\",\"");

        // Each data field in csv is surrounded with "", but most of them have been removed.
        // Now we need to deal with the first and last field which are special:
        // The first field has a redundant " as the first character, and the last field has one at the end.
        for (int i = 0; i < infoFields.length; i++) {
            if (i == 0) {
                // Remove the first character of the first field.
                infoFields[i] = infoFields[i].substring(1, infoFields[i].length());
            } else if (i == 5) {
                // Remove the last character of the last field.
                infoFields[i] = infoFields[i].substring(0, infoFields[i].length()-1);
            } else {
                // Do not need to do anything for other fields.
                continue;
            }
        }

        // Possible exception when parsing the date
        try {
            int target = Integer.parseInt(infoFields[0]);
            int id = Integer.parseInt(infoFields[1]);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Date date = formatter.parse(infoFields[2]);
            String flag = infoFields[3];
            String user = infoFields[4];
            String text = infoFields[5];

            // Create the Tweet object with the information
            Tweet tweet = new Tweet(target, id, date, flag, user, text);

            return tweet;

        } catch (ParseException e) {
            MainApp.outputArea.append("Exception: fail to convert the date information.\n");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Classifies a tweet as negative, neutral, or positive by using the text of the tweet.
     * @param tweet A tweet object.
     * @return 0 = negative, 2 = neutral, 4 = positive.
     */
    public int classifyTweet(AbstractTweet tweet) {

        String text = tweet.getText();

        int pred = SentimentAnalyzer.getParagraphSentiment(text);
        tweet.setPredictedPolarity(pred);

        MainApp.outputArea.append("==================================================\n");
        MainApp.outputArea.append("Tweet ID: " + tweet.getId() + "\n");
        MainApp.outputArea.append("Target: " + tweet.getTarget() + ", Prediction:"+ tweet.getPredictedPolarity() + "\n");
        MainApp.outputArea.append("==================================================\n");

        return pred;
    }

    /**
     * Adds a list of new tweets to the existing database.
     * @param tweets A list of tweet objects.
     */
    public void addTweetsToDB(List<AbstractTweet> tweets) {
        int countDup = 0;
        int countDif = 0;
        for (AbstractTweet t : tweets) {
            if (tweetDB.contains(t)) {
                //MainApp.outputArea.append("Duplicate tweet detected.\n");
                countDup++;
                continue;
            } else {
                countDif++;
                tweetDB.add(t);
            }
        }
        MainApp.outputArea.append("Done.\n");
        MainApp.outputArea.append(countDif + " tweets added to database from memory list.\n");
        MainApp.outputArea.append(countDup + " duplicate tweets ignored.\n");
        tweetBuffer.clear();
        MainApp.outputArea.append("Buffered data cleared.\n");
    }

    /**
     * Deletes ad tweet from the database, given its id.
     * @param id The id value of the tweet.
     */
    public void deleteTweet(int id) {
        int targetIndex = -1;
        for (AbstractTweet t : tweetDB) {
            if (t.getId() == id) {
                targetIndex = tweetDB.indexOf(t);
            }
        }
        if (targetIndex == -1) {
            MainApp.outputArea.append("Tweet not found in database list.\n");
        } else {
            tweetDB.remove(targetIndex);
            MainApp.outputArea.append("Tweet deleted in database list.\n");
        }
    }

    
    /**
     * Auxiliary convenience method used to close a file and handle possible
     * exceptions that may occur.
     *
     * @param c The file to be closed
     */
    public void close(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (IOException ex) {
            System.err.println(ex.toString());
            ex.printStackTrace();
        }
    }

    /**
     * Saves the list of tweets as a serialized object.
     */
    public void saveSerialDB() {
        MainApp.outputArea.append("Saving database...\n");
        //serialize the database
        OutputStream file = null;
        OutputStream buffer = null;
        ObjectOutput output = null;
        try {
            file = new FileOutputStream(DATAFILENAME);
            buffer = new BufferedOutputStream(file);
            output = new ObjectOutputStream(buffer);

            output.writeObject(tweetDB);

            output.close();
        } catch (IOException ex) {
            System.err.println(ex.toString());
            ex.printStackTrace();
        } finally {
            close(file);
        }
        MainApp.outputArea.append("Done.\n");
    }

    /**
     * Loads tweet database from a data file.
     */
    @SuppressWarnings("unchecked")
    public void loadSerialDB() {
        MainApp.outputArea.append("Reading database...\n");

        File dataFile = new File(DATAFILENAME);

        // Try to read existing dealership database from a file
        InputStream file = null;
        InputStream buffer = null;
        ObjectInput input = null;
        try {
            if (!dataFile.exists()) {
                MainApp.outputArea.append("Data file does not exist. Creating a new database.\n");
                tweetDB = new ArrayList<AbstractTweet>();
                return;
            }
            file = new FileInputStream(dataFile);
            buffer = new BufferedInputStream(file);
            input = new ObjectInputStream(buffer);

            // Read serialized data
            tweetDB = (ArrayList<AbstractTweet>) input.readObject();
            input.close();
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.toString());
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            MainApp.outputArea.append("Database file not found.\n");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println(ex.toString());
            ex.printStackTrace();
        } finally {
            close(file);
        }
        MainApp.outputArea.append("Done.\n");
        MainApp.outputArea.append(getTweetDB().size() + " records in database.\n");
        MainApp.outputArea.append(getTweetBuffer().size()+" records in temp list.\n");
    }

    /**
     * Searches the tweet database by user name. It returns a list of all tweets
     * matching the given user name.
     * @param user The user name to search for.
     * @return A list of tweet objects.
     */
    public List<AbstractTweet> searchByUser(String user) {
        List<AbstractTweet> resultList = new ArrayList<AbstractTweet>();

        for (AbstractTweet t : tweetDB) {
            if (t.getUser().equalsIgnoreCase(user)) {
                resultList.add(t);
            }
        }
        return resultList;
    }

    /**
     * Searches the tweet database for tweets posted on a given date.
     * @param date The date to search for.
     * @return A list of tweet objects.
     */
    public List<AbstractTweet> searchByDate(Date date) {
        List<AbstractTweet> result_List = new ArrayList<AbstractTweet>();

        for (AbstractTweet t : tweetDB) {
            if (t.getDate().equals(date)) {
                result_List.add(t);
            }
        }
        return result_List;
    }

    /**
     * Searches the tweet database for tweets matching a given flag.
     * @param flag The flag to search for.
     * @return A list of tweet objects.
     */
    public List<AbstractTweet> searchByFlag(String flag) {
        List<AbstractTweet> resultList = new ArrayList<AbstractTweet>();

        for (AbstractTweet t : tweetDB) {
            if (t.getFlag().equalsIgnoreCase(flag)) {
                resultList.add(t);
            }
        }
        return resultList;
    }

    /**
     * Searches the tweet database for tweets matching a given substring.
     * @param substring The substring to search for.
     * @return A list of tweet objects.
     */
    public List<AbstractTweet> searchBySubstring(String substring) {
        List<AbstractTweet> resultList = new ArrayList<AbstractTweet>();

        for (AbstractTweet t : tweetDB) {
            if (t.getText().toLowerCase().contains(substring.toLowerCase())) {
                resultList.add(t);
            }
        }
        return resultList;
    }

    //setters and getters for the class fields
    public List<AbstractTweet> getTweetDB() {
        return tweetDB;
    }

    public void setTweetDB(List<AbstractTweet> tweetDB) {
        this.tweetDB = tweetDB;
    }

    public List<AbstractTweet> getTweetBuffer() {
        return tweetBuffer;
    }

    public void setTweetBuffer(List<AbstractTweet> tweetBuffer) {
        this.tweetBuffer = tweetBuffer;
    }

    public static String getDataFileName() {
        return DATAFILENAME;
    }

    //fields

    // Memory list for Tweet Objects
    private List<AbstractTweet> tweetDB = new ArrayList<AbstractTweet>();
    // Buffer List for Tweet Objects
    private List<AbstractTweet> tweetBuffer = new ArrayList<AbstractTweet>();
    // Database file path
    private final static String DATAFILENAME = "DB.ser";
}
