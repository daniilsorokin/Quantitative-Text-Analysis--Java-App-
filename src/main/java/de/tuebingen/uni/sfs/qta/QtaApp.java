package de.tuebingen.uni.sfs.qta;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.sentdetect.DefaultEndOfSentenceScanner;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import com.google.common.collect.Ordering;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import java.util.Map;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public class QtaApp
{
    public static void main( String[] args )
    {
        String fileName = "nusha_text_short.txt";
        String text = "";
//        ArrayList<String> sentences = new ArrayList<String>();
//        char[] eos = {'.','?','!'};
//        DefaultEndOfSentenceScanner sentScanner = new DefaultEndOfSentenceScanner(eos);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader
                        (new FileInputStream(fileName), "utf-8"));
            String line;
            while((line = in.readLine()) != null ) {
//                line = line.trim();
//                List<Integer> ends = sentScanner.getPositions(line);
//                for (int i = 0; i < ends.size(); i++) {                    
//                }
                text += line.trim() + " ";
            }
            in.close();
            HashMap<Word, Integer> freqs = new HashMap<Word, Integer>();
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
            String[] tokens = tokenizer.tokenize(text);
            ArrayList<Word> lemmas = TreeTaggerResource.INSTANCE.getLemmas(tokens);
            for (Word lemma : lemmas) {
                if (freqs.containsKey(lemma))
                    freqs.put(lemma, freqs.get(lemma) + 1);
                else freqs.put(lemma, 1);
            }
//            TreeMap<Word,Integer> sorted_map = new TreeMap<Word,Integer>();
//            sorted_map.putAll(freqs);
            List<Word> sortedWords = Ordering.natural().onResultOf(Functions.forMap(freqs)).reverse().immutableSortedCopy(freqs.keySet());
            System.out.println(map);
        } catch (IOException ex) {
            Logger.getLogger(QtaApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
