package Tweet;

import sentimentanalysis.AbstractTweet;
import java.util.Date;
import java.util.Objects;

/**
 * Creates Tweet object using a twitter data file's 5 categories
 * (target, id, date, user, text, and flag)
 * Extends Abstract Tweet Class
 * @author Joseph Mancillas
 */
public class Tweet extends AbstractTweet
{
    /*
     * Tweet called to create object type of Abstract Tweet
     */
    Tweet (int target,int id, Date date,
           String flag, String user,String text) {

        super(target, id, date, flag, user, text);
    }

    /*
     * Overridden method to print object to console formatted.
     */

    @Override
    public String toString() {

        return "Predicted Target: " +this.getPredictedPolarity() +
                ", Target: " + getTarget() + ", ID:"
                +this.getId()+", Date: " +this.getDate()+", Flag: "
                +this.getFlag() + ", User: " +this.getUser()+", Text: "
                +this.getText()+"";
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Tweet)){
            return false;
        }

        Tweet aTweet = (Tweet) o;

        return this.getTarget() == aTweet.getTarget() &&
                this.getId() == aTweet.getId() &&
                this.getDate() == aTweet.getDate() &&
                this.getFlag().equals(aTweet.getFlag()) &&
                this.getUser().equals(aTweet.getUser()) &&
                this.getText().equals(aTweet.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTarget(), getId(), getDate(),
                getFlag(), getUser(), getText());
    }
}
