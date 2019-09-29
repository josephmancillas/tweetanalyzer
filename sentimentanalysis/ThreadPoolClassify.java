package sentimentanalysis;

/**
 *   @author Joseph Mancillas
 *   Thread pool classification that creates a threadpool to be implemented by
 *   classify tweets in TweetHandler.java
 */

public class ThreadPoolClassify implements Runnable {

    private AbstractTweet tweet;

    private static final TweetHandler HANDLER = new TweetHandler();

    /**
     *
     * @param tweet is taken into the method and assigned to the AbstractTweet
     * reference.
     */
    public ThreadPoolClassify (AbstractTweet tweet){
        this.tweet = tweet;

    }

    /**
     * Void function that runs the thread created.
     */
    public void run(){
        try {
            tweet.setPredictedPolarity(HANDLER.classifyTweet(tweet));
            Thread.sleep(2000);
            if (tweet.getPredictedPolarity() == tweet.getTarget()) {
                countCorrect++;
            } else {
                countWrong++;
            }

        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static int countWrong = 0;
    public static int countCorrect = 0;
}