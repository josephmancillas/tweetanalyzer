/*
 * Code created for TxState - CS 3354. 
 */
package sentimentanalysis;

import java.util.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.*;
import java.io.OutputStream;
import java.io.PrintStream;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * This class uses the Stanford Natural Language Processing library to perform
 * Sentiment Analysis on a give text input. It depends on the libraries
 * ejml-0.23.jar, stanford-corenlp-3.9.1.jar, and
 * stanford-corenlp-3.9.1-models.jar
 *
 * @author vmetsis
 */
public class SentimentAnalyzer {

    /**
     * A StanfordCoreNLP object needs to be initialized to perform processing.
     * This object has been defined as static since we only need one instance of
     * it to perform all classification tasks.
     */
    private final static StanfordCoreNLP pipeline;
    // Initialize StanfordCoreNLP library as a static object

    static {
        //Disable System.err output to prevent showing library messages.
        PrintStream err = System.err;
        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }));

        // Specify the properties of the NLP tool to be applied to the input text
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

        pipeline = new StanfordCoreNLP(props);

        // Restore System.err
        System.setErr(err);
    }

    /**
     * Accepts a text (multiple sentences) as input and returns the sentiment of
     * each sentence in that text.
     *
     * @param text The input text.
     * @return A list of sentences and their sentiment as CoreMap objects.
     */
    public static List<CoreMap> getSentencesSentiment(String text) {

        // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
        Annotation annotation = new Annotation(text);

        // Run all the selected Annotators on this text
        pipeline.annotate(annotation);

        // This prints out the results of sentence analysis to file(s) in good formats
        //pipeline.prettyPrint(annotation, System.out);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        //CoreMap sentence = sentences.get(0);

        return sentences;
    }

    /**
     * Accepts a input paragraph, which might consist of one or more sentences
     * and returns a sentiment classification label for the whole paragraph.
     * 
     * Use this method to classify a whole Tweet.
     *
     * @param text The input paragraph.
     * @return The class label (0 = negative, 2 = neutral, 4 = positive).
     */
    public static int getParagraphSentiment(String text) {

        int count_pos = 0;
        int count_neg = 0;

        List<CoreMap> sentences = getSentencesSentiment(text);
        //CoreMap sentence = sentences.get(0);

        for (CoreMap sentence : sentences) {
            final Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

            //Print versbose details about classification outcome
           final SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
           final String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
           System.out.println("sentence:  " + sentence);
           System.out.println("sentiment: " + sentiment + "\n");
           //System.out.println("matrix:    " + sm);

            // Count positive and negative sentences.
            String s = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            if (s.toLowerCase().contains("positive")) {
                count_pos++;
            } else if (s.toLowerCase().contains("negative")) {
                count_neg++;
            }
            System.out.println(sentence.get(SentimentCoreAnnotations.SentimentClass.class) + ": " +sentence.get(CoreAnnotations.TextAnnotation.class));
        }

        int pred = -1;
        // Return predicted polarity
        if (count_pos == 0 && count_neg == 0) {
            pred = 2;
        } else if (count_pos >= count_neg) {
            pred = 4;
        } else {
            pred = 0;
        }

        return pred;
    }
}
