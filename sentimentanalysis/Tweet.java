package sentimentanalysis;

import java.io.Serializable;
import java.util.Date;

/**
  CS3354.005 Fall 2018 Tweet Class Implementation
  Inherited 6 fields from AbstractTweet
  @author vangelis
  @author jelena
  @author junye
 */
public class Tweet extends AbstractTweet {

    /**
     * Constructor from csv file input: uses data fields use them to set the parameters.
     *
     * @param target  ground truth sentiment label
     * @param id tweetDBID
     * @param date date the tweet was posted
     * @param flag tweet flag
     * @param user userID
     * @param text Tweet text
     *
     */
    public Tweet(int target, int id, Date date, String flag, String user, String text) {

        // Call constructor of the super class
        super(target, id, date, flag, user, text);
    }

    /**
     * Override toString() to format outputs
     *
     * @return Formatted information of the tweet
     */
    @Override
    public String toString() {
        String information = "| ";
        information = information + String.format("%6s", this.getTarget()) + " | ";
        information = information + String.format("%6s", this.getPredictedPolarity()) + " | ";
        information = information + String.format("%6s", this.getId()) + " | ";
        information = information + String.format("%30s", this.getDate()) + " | ";
        information = information + String.format("%10s", this.getFlag()) + " | ";
        information = information + String.format("%15s", this.getUser()) + " | ";
        if (this.getText().length() > 65) {
            information = information + String.format("%70s", this.getText().substring(0,65)+"...") + " |";
        } else {
            information = information + String.format("%70s", this.getText()) + " |";
        }


        return information;
    }

    /**
     * Override equals() to compare two tweets
     * @return true if the id are equal
     */
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Tweet)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Tweet c = (Tweet) o;

        // Compare the data members and return accordingly
        return c.getId() == this.getId();
    }

    /**
     * Override hashCode()
     * @return Id of tweet as hash code
     */
    @Override
    public int hashCode() {
        return this.getId();
    }

}
