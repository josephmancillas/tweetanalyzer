/*
 * Code created for TxState - CS 3354.
 */

package sentimentanalysis;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author vangelis
 */
public abstract class AbstractTweet implements Serializable {
    // Tweet info fields
    /**
     * The polarity of the tweet (0 = negative, 2 = neutral, 4 = positive).
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
    
    /**
     * The predicted polarity of the tweet (0 = negative, 2 = neutral (default value), 4 = positive).
     */
    private int predictedPolarity;
    
    /**
     *
     * @param target
     * @param id
     * @param date
     * @param flag
     * @param user
     * @param text
     */
    public AbstractTweet(int target, int id, Date date, String flag,
                         String user, String text) {
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
     * @return
     */
    public int getTarget() {
        return target;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     *
     * @return
     */
    public String getFlag() {
        return flag;
    }

    /**
     *
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @return
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
}
