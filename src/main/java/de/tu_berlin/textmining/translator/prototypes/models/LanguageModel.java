package de.tu_berlin.textmining.translator.prototypes.models;

import java.util.Collection;
import java.util.List;

/**
 * In this class you should implement your language model.  *
 */

public interface  LanguageModel {

    /**
     * This function takes the provided training corpus and estimates the model probabilities by processing it.
     *
     * @param corpus
     */
    void train(Collection<List<String>> corpus);

     /**
     * computes the probability of an individual word in a sentence at position i
     *
     * @param sentence
     * @param index
     * @return
     */
    double getWordProbability(List<String> sentence, int index);


    /**
     * This function returns the probability of a sentence as scored by the language model
     * <br />
     * e.g.:<br /><code>P(I am here) = P(I|&lt;s&gt;)*P(am|I)*P(here|am)P(&lt;/s&gt;|here)</code>
     *
      * @param sentence
     * @return
     */
    double sentenceLogProbability(List<String> sentence);



    /**
     * This function implements the 'shanon game'. A sentence is assembled by randomly sampling from the language model
     * until the stop token has been sampled.
     *
      * @return
     */
    Iterable<String> generateSentence();
    
    double getBiGramProbability(String word1, String word2);
    double getUniGramProbability(String word);

}
