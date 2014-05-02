package de.tuebingen.uni.sfs.qta;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public enum QTAnalyser {
    INSTANCE;
    
    public static final String ENCODING = "UTF-8";
    
    private static final boolean NO_PUNCT = true;            
    private HashMap<String, Integer> lemmaFreqs;
    
    private QTAnalyser(){        
        try {
            lemmaFreqs = new HashMap<String, Integer>();
            BufferedReader in = new BufferedReader(new InputStreamReader
                                                (new FileInputStream("lists/ru-lemmas-freq.csv"), ENCODING));
            String line;
            while((line = in.readLine()) != null ) {
                line = line.trim();
                String[] splits = line.split("\\t");
                if (splits.length == 2){
                    int frequency = Integer.parseInt(splits[1]);
                    lemmaFreqs.put(splits[0], frequency);
                }
            }
            in.close();
        } catch (IOException ex){
            JOptionPane.showMessageDialog(null, "Can't load frequecy lists.");
            Logger.getLogger(QtaApp.LOGGER_NAME).log(Level.SEVERE, null, ex);
            lemmaFreqs = null;
        }
    }
    
    private String normalize (String text) {
        text = text.replace("—", "-")
                   .replace("…", ".");
        return text;
    }
    
    public HashMap<Word, Integer> computeFrequencyList (String text){
        text = normalize(text);
        HashMap<Word, Integer> freqs = new HashMap<Word, Integer>();
        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);
        ArrayList<Word> lemmas = TreeTaggerResource.INSTANCE.getLemmas(tokens);
        for (Word lemma : lemmas) {
            if (!NO_PUNCT || !(lemma.getLemma().equals("@card@") ||
                    lemma.getLemma().matches("[^\\pL]+"))) {
                if (freqs.containsKey(lemma))
                    freqs.put(lemma, freqs.get(lemma) + 1);
                else freqs.put(lemma, 1);
            }
        }
        return freqs;
    }   
    
    public HashMap<Word, Double> computeLogFrequency (HashMap<Word, Integer> freqs){
        HashMap<Word, Double> logFreqs = new HashMap<Word, Double>();
        for (Word word : freqs.keySet()) {
            int freq = freqs.get(word);
            double logFreq = Math.log(freq + 1.0);
            logFreqs.put(word, logFreq);
        }
        return logFreqs;
    }

    public HashMap<Word, Double> computeAugFrequency (HashMap<Word, Integer> freqs){
        HashMap<Word, Double> augFreqs = new HashMap<Word, Double>();
        int maxFreq = 0;
        for (Integer freq : freqs.values()) {
            if (freq > maxFreq) maxFreq = freq;
        }
        for (Word word : freqs.keySet()) {
            int freq = freqs.get(word);
            double augFreq = ((freq * 0.5) / maxFreq) + 0.5;
            augFreqs.put(word, augFreq);
        }
        return augFreqs;
    }
    
    public HashMap<Word, Double> computeNormalizedFrequency (HashMap<Word, Integer> freqs){
        HashMap<Word, Double> normFreqs = new HashMap<Word, Double>();
        if (lemmaFreqs != null) {
            double maxFreq = 0;
            for (Integer freq : lemmaFreqs.values()) {
                if (freq > maxFreq) maxFreq = freq;
            }
            for (Word word : freqs.keySet()) {
                double freq = freqs.get(word);
                double corpusFreq = lemmaFreqs.containsKey(word.getLemma()) ? lemmaFreqs.get(word.getLemma()) : 0;
                double logFreq = Math.log(freq + 1);
                double logCorpusInverseFreq = Math.log(maxFreq / (corpusFreq + 1) );
                normFreqs.put(word, (logFreq * logCorpusInverseFreq));
            }
        } else {
            for (Word word : freqs.keySet()) {
                normFreqs.put(word, 0.0);
            }
        }
        return normFreqs;
    }    
}
