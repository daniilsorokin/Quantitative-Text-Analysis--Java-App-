package de.tuebingen.uni.sfs.qta;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;

/**
 *
 * @author dsorokin
 */
public class QTAnalyser {
    public static HashMap<Word, Integer> computeFrequencyList (String text){
        //Normalize
        text = text.replace("—", "-")
                   .replace("…", ".");
        HashMap<Word, Integer> freqs = new HashMap<Word, Integer>();
        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);
        ArrayList<Word> lemmas = TreeTaggerResource.INSTANCE.getLemmas(tokens);
        for (Word lemma : lemmas) {
            if (freqs.containsKey(lemma))
                freqs.put(lemma, freqs.get(lemma) + 1);
            else freqs.put(lemma, 1);
        }
        return freqs;
    }
    
    public static List sortMapByValues(HashMap map){
        List sortedList = Ordering.natural().onResultOf(Functions.forMap(map)).reverse().immutableSortedCopy(map.keySet());
        return sortedList;
    }    
}
