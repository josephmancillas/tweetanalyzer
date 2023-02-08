package tweet;

import sentimentanalysis.AbstractTweet;
import sentimentanalysis.SentimentAnalyzer;
import sentimentanalysis.TweetHandlerInterface;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.String;

/**
 * @author Joseph Mancillas
 * CS 3354
 * Assignment 2
 */

public class TweetHandler implements TweetHandlerInterface {
    
    static final Scanner usr_in = new Scanner(System.in);

    private List<AbstractTweet> tweetsContainer = new ArrayList<>();
    private List<AbstractTweet> tweetDatabase = new ArrayList<>();

    private File dataBase = new File("TweetDatabase.ser");

    private String filePath = dataBase.getAbsolutePath();

    private int target;
    private int id;
    private Date date;
    private String flag;
    private String user;
    private String tweetbody;

/**
 * Returns Object list of Tweets
 * @param tweetFile taken from user in MainApp.java to location of test
 * data
 * @return List containing new objects
 */

    @Override
    public List<AbstractTweet> loadTweetsFromText(String tweetFile){

        String line;

        File twitterTweets = new File(tweetFile);

        try{
            Scanner scanningTweets = new Scanner(new FileReader
                    (twitterTweets.getAbsoluteFile()));
            while(scanningTweets.hasNextLine()){

                line = scanningTweets.nextLine();
                tweetsContainer.add(parseTweetLine(line));
            }
            scanningTweets.close();
        }catch (FileNotFoundException i){
            System.out.println("ERROR: Your file could not be found." +
                               " Please try again. \n");
        }
        System.out.println("Successfully imported " + tweetsContainer.size()
                            + " tweets. \n" );

        return tweetsContainer;
    }

    /**
     * Driver method to loadTweetsFromText()
     * Asks for user input and loads method with string given
     * and initializes it in loader
     */
    public void loadFile(){
        String filePath = usr_in.next();

        loadTweetsFromText(filePath);
    }

/**
 * Returns/Constructs a single parsed object from data file
 * @param tweetLine A single line read in to parse into variables a tweet
 * @return Object of AbstractTweet type
 */
@Override
public AbstractTweet parseTweetLine(String tweetLine){
     try{
         String[] parts = tweetLine.split("\",\"");

         target = Integer.parseInt(parts[0].replace
                    ("\"", ""));

         id = Integer.parseInt(parts[1].replace
                    ("\"", ""));

         SimpleDateFormat formattedDate = new SimpleDateFormat
                    ("EEE MMM dd HH:mm:ss zzz yyyy");
         date = formattedDate.parse(parts[2]);

         flag = parts[3];
         user = parts[4];
         tweetbody = parts[5];


        }catch(ParseException e){
            System.out.print("ERROR: The was an issue parsing your line. \n");
            e.printStackTrace();
        }catch (NullPointerException o){
            System.out.println("There was an error in the Parse Method. \n");
        }

        Tweet tweet =  new Tweet(target, id, date, flag,
               user, tweetbody);

       return tweet;
    }

/**
 * Returns the sentiment value of a single tweet body
 * @param  tweet object type to gather sentiment from body
 * @return sentiment value of passed in tweets body
 */
    public int classifyTweet(AbstractTweet tweet){

        return SentimentAnalyzer.getParagraphSentiment(tweet.getText());
    }

    /*
     * Driver method for classifyTweet. Iterates tweetDatabase and classifies
     * sentiment of all tweets in database
     */
    public void classifyTweets(){
        int localTarget;
        int correctCount = 0;

        System.out.println("Classifying... ");

        for(AbstractTweet f : tweetDatabase){
            localTarget = classifyTweet(f);
            f.setPredictedPolarity(localTarget);

            if(f.getTarget() == f.getPredictedPolarity()){
                correctCount++;
            }
        }

        System.out.println("Number Correct " + correctCount);
    }

    /**
     * Adds tweets in tweetBuffer to database for storage and checks for
     * possible duplicates being added
     * @param tweetBuffer that is the temp database
     */

    @Override
    public void addTweetsToDB(List<AbstractTweet> tweetBuffer){

        int addedTweets = 0;

        for(AbstractTweet tweet: tweetBuffer) {
            if(!tweetDatabase.equals(tweetBuffer)){

                tweetDatabase.add(tweet);
                addedTweets++;
            }
        }
        if(addedTweets == 0){
            System.out.println("No new tweets were added to the database. \n");
        }

        System.out.println("Added " + addedTweets + " to the database. \n");

    }

    /**
     * Deletes a given tweet object from the database of a user input integer
     * @param id of tweet user would like to delete
     */
    @Override
    public void deleteTweet(int id) {

    // Concurrent modidication error occurs using below commented out method
    //    for (AbstractTweet tweet : tweetDatabase) {
    //        if (tweet.getId() == id) {
    //            tweetsContainer.add(tweetDatabase.get(tweet.getId()));
    //            System.out.println("Tweet matching " + tweet.getId() + " removed. \n" +
    //                    tweet.toString());
    //            break;
    //        }
    //        tweetDatabase.removeAll(tweetsContainer);
    //    }

        Iterator<AbstractTweet> tweetIterator = tweetDatabase.iterator();
        while (tweetIterator.hasNext()){

            AbstractTweet nextTweet = tweetIterator.next();

            if (nextTweet.getId() == id) {
                System.out.println("Tweet matching " + nextTweet.getId() + " removed. \n" +
                                    nextTweet.toString());
                tweetIterator.remove();
                break;
            }

        }
    }

    /**
     * Driver method for deleteTweets()
     * Asks for user input of an ID of a tweet object to delete from the database.
     */

    public void deleteTweets() {
        
        System.out.println("Enter the id of the given Tweet you would like to delete. \n");
        String stringId = usr_in.next();

        int idToDelete = Integer.parseInt(stringId);

        deleteTweet(idToDelete);
    }

    /**
     * Saves contents of tweetBuffer temp array in serialized form
     */
    @Override
    public void saveSerialDB(){

        try{
            FileOutputStream fos = new FileOutputStream(dataBase);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(tweetDatabase);

            fos.close();
        }
        catch(IOException k){
            System.out.println("There was an error processing the Database " +
                    "to the file, retry entry again.\n");
        }
    }

    /**
     * Loads the serialized database for accessing and modifying
     */
    @Override
    public void loadSerialDB(){

        try{
            FileInputStream fis = new FileInputStream(dataBase);
            ObjectInputStream ois = new ObjectInputStream(fis);

            tweetDatabase = (List<AbstractTweet>) ois.readObject();

            System.out.println("\nSuccessfully loaded database containing "
                                + tweetDatabase.size() + " Tweets");
            fis.close();
        }
        catch(FileNotFoundException t){
            System.out.println("No such file exists. ");

            System.out.println("Created " + dataBase + "file to save database to " +
                                "located in  " + filePath + "\n");

        }
        catch(IOException a){
            System.out.println("ERROR loading Tweet Database. \n");
            a.printStackTrace();
        }
        catch(ClassNotFoundException w){
            w.printStackTrace();
        }
        catch(ClassCastException o){
            System.out.println("Error casting list");
        }
    }

    /**
     * Searches for a specific user and returns list of possible matching
     * users
     * @param user A string to be searched for in the database
     * @return List<AbstractTweet> objects containing user
     */
    @Override
    public List<AbstractTweet> searchByUser(String user){

        List<AbstractTweet> userFound = new ArrayList<>();

        String searchedUser;

        for(AbstractTweet k : tweetDatabase){
            searchedUser = k.getUser();
            if(searchedUser.equalsIgnoreCase(user)){
                System.out.println("User(s) found: " + user);
                userFound.add(k);
            }
        }
        return userFound;
    }

    /**
     * Searches for a specific flag and returns list of possible matching
     * flags
     * @param flag A string flag to be searched for in the database
     * @return List<AbstractTweet> objects containing flag
     */

    @Override
    public List<AbstractTweet> searchByFlag(String flag){

        List<AbstractTweet> matchedFlags = new ArrayList<>();

        String localFlag;

        for(AbstractTweet l : tweetDatabase) {
            localFlag = l.getFlag();
            if (localFlag.equalsIgnoreCase(flag)) {
                matchedFlags.add(l);
            }
        }
        return matchedFlags;
    }

    /**
     * Searches for a specific date and returns list of possible matching
     * dates
     * @param date the user would like to search for
     * @return List<AbstractTweet> objects containing date
     */
    @Override
    public List<AbstractTweet> searchByDate(Date date){

        List<AbstractTweet> matchedDates = new ArrayList<>();

        Date localSearchedDate;
        for (AbstractTweet d : tweetDatabase){
            localSearchedDate = d.getDate();
            if(localSearchedDate.equals(date)){
                matchedDates.add(d);
            }

            System.out.println("No matching dates containing " + date );
        }
        return matchedDates;
    }

    /**
     * Searches for substrings inside a tweet body and returns list of possible matching
     * substrings
     * @param substring A substring to be searched for in the database passed in from user input
     * @return List<AbstractTweet> objects containing substring
     */

    @Override
    public List<AbstractTweet> searchBySubstring(String substring){

        String searchedSubstrings;
        List<AbstractTweet> foundSubstrings= new ArrayList<>();
        for (AbstractTweet x : tweetDatabase){
            searchedSubstrings = x.getText();
            try {
                if(searchedSubstrings.contains(substring)){
                    foundSubstrings.add(x);
                }
            }catch(NullPointerException u){
                u.printStackTrace();
            }
        }
        return foundSubstrings;
    }

    /**
     * Adds all tweets in the tweetBuffer/tweetContainer to the database
     * Serializes the database
     * flushes tweetBuffer to load new tweets into
     */
    public void addToDatabase() {

        addTweetsToDB(tweetsContainer);
        saveSerialDB();

        String answer;
        System.out.print("Would you also like to empty the container? " +
                "y for Yes, n for No \n");
                
        answer = usr_in.nextLine();

        switch(answer.toLowerCase()){
            case "y":
                tweetsContainer.clear();
                saveSerialDB();
                break;

            case "n":
                System.out.println("Returning to menu. \n");

        }

    }

    /**
     * Changes the tweet classification of a single tweet object
     * in the tweetBuffer/container
     * @param tempContainer to change
     */
    private void changeTweetClass(List<AbstractTweet> tempContainer){
        System.out.println("Enter the id of the Tweet you would like to change. \n");
        String searchedId = usr_in.next();

        int idToChange = Integer.parseInt(searchedId);


        for (AbstractTweet o : tempContainer) {
            if (idToChange == o.getId()) {
                System.out.println("Is this the tweet you want to change? " + o.toString());
                System.out.println("Press 'y' for Yes and 'n' for No. ");

                String answer = usr_in.next();
                switch (answer) {
                    case "y":
                        System.out.println("What is the new polarity 0 = Negative, " +
                                "2 = neutral, 4 = Positive");
                        String newClass = usr_in.next();
                        int newID = Integer.parseInt(newClass);
                        o.setPredictedPolarity(newID);
                        System.out.print("Class of given tweet changed to " + newID +
                                " Tweet Content " + o.toString() + "\n");
                        break;

                    case "n":
                        return;
                }

            }
        }
    }

    /**
     * Submenu to choose a search function within a tweet from.
     * @return List<AbstractTweet> containing search results
     */
    public List<AbstractTweet> searchTweet(){

        List<AbstractTweet> returnedList = new ArrayList<>();

        System.out.print("What search would you like to do? ");

        System.out.println("Enter a search choice: \n" +
                            "1. User \n" +
                            "2. Date \n" +
                            "3. Flag \n" +
                            "4. Substring \n");

        String selection = usr_in.nextLine();

        int answer = Integer.parseInt(selection);
        switch (answer) {
                case 1:

                    System.out.print("Enter a user to search for: ");
                    String searchedUser = usr_in.next();
                    returnedList = searchByUser(searchedUser);
                    break;

                case 2:

                    try {
                        System.out.print("Enter a date to search for: ");
                        String searchedDate = usr_in.next();
                        userDateFormat(searchedDate);
                        returnedList = searchByDate(userDateFormat(searchedDate));
                        break;

                    } catch (ParseException w) {
                        w.printStackTrace();
                    }
                case 3:

                    System.out.print("Enter a Flag to search for: ");
                    String searchedFlag = usr_in.next();
                    returnedList = searchByFlag(searchedFlag);
                    break;

                case 4:

                    System.out.print("Enter a substring to search for: ");
                    String searchedSub = usr_in.next();
                    returnedList = searchBySubstring(searchedSub);
                    break;
            }
        return returnedList;
    }

    /**
     * Takes a string date from the console and formats using the SimpleDateFormat
     * @param stringDate a string date to parse
     * @return Date A formatted Date data type
     */
    private Date userDateFormat(String stringDate) throws ParseException{
        SimpleDateFormat formattedDate = new SimpleDateFormat
                ("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date = formattedDate.parse(stringDate);

        return date;
    }

    /**
     * Driver method for changing tweets
     */
    public void changeTweets(){
        changeTweetClass(tweetsContainer);
    }

    /**
     * Getter method for the tweet container
     */
    public List<AbstractTweet> getTweetsContainer(){
        return tweetsContainer;
    }

    /**
     * Getter method for the tweet database
     */
    public List<AbstractTweet> getTweetDatabase(){
        return tweetDatabase;
    }

    /**
     * Prints out a formatted version of results to the console
     */
    public void printList(List<AbstractTweet> printedList){
        for (int i =0; i< printedList.size(); i++) {
            System.out.println("Tweet:" + i + " " + printedList.get(i).toString());
        }
    }
}