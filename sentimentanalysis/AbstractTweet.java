package sentimentanalysis;

import java.io.Serializable;
import java.util.Date;

/**
  CS3354 Fall 2018 Abstract Tweet Class Implementation
  @author vangelis
  @author jelena
  @author junye
 */
public abstract class AbstractTweet implements Serializable {

    /**
      @param target
      @param id
      @param date
      @param flag
      @param user
      @param text
     */
    public AbstractTweet(int target, int id, Date date, String flag, String user, String text) {
        this.target = target;
        this.id = id;
        this.date = date;
        this.flag = flag;
        this.user = user;
        this.text = text;
        this.predictedPolarity = 2;
    }

    /**
     *
     * @return Tweet sentiment ground truth field
     */
    public int getTarget() {
        return target;
    }

    /**
     *
     * @return Tweet id field
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return Tweet date field
     */
    public Date getDate() {
        return date;
    }

    /**
     *
     * @return sentiment ground truth field, if available
     */
    public String getFlag() {
        return flag;
    }

    /**
     *
     * @return Tweet user field
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @return Tweet text field
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @return predictedPolarity field
     */
    public int getPredictedPolarity() {
        return predictedPolarity;
    }

    /**
     *
     * @param predictedPolarity
     */
    public void setPredictedPolarity(int predictedPolarity) {
        this.predictedPolarity = predictedPolarity;
    }

    /**
     * The predicted polarity of the tweet (0 = negative, 2 = neutral (default value), 4 = positive).
     */
    private int predictedPolarity;
    /**
     * The ground truth polarity of the tweet (0 = negative, 2 = neutral, 4 = positive).
     */
    private final int target;
    /**
     * The id of the tweet (e.g. 2087).
     */
    private final int id;
    /**
     *  The date of the tweet (Sat May 16 23:58:44 UTC 2009).
     */
    private final Date date;
    /**
     *  Flag.
     */
    private final String flag;
    /**
     *  The user that tweeted (robotickilldozr).
     */
    private final String user;
    /**
     *  The text of the tweet.
     */
    private final String text;


}
