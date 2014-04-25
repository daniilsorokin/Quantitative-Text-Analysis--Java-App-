package de.tuebingen.uni.sfs.qta;

import static de.tuebingen.uni.sfs.qta.IOUtils.ENCODING;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public enum TreeTaggerResource {
    INSTANCE;
    
    public static void main( String[] args ) throws IOException{
        HashMap<String, Integer> tokenFreqs = new HashMap<String, Integer>();
        HashMap<String, Integer> lemmaFreqs = new HashMap<String, Integer>();
        BufferedReader in = new BufferedReader(new InputStreamReader
                                        (new FileInputStream("1grams-3.txt"), ENCODING));
        String line;
        while((line = in.readLine()) != null ) {
            line = line.trim();
            String[] splits = line.split("\\t");
            if (splits.length == 2){
                int frequency = Integer.parseInt(splits[0]);
                tokenFreqs.put(splits[1], frequency);
            }
        }
        in.close();
        String[] tokens = tokenFreqs.keySet().toArray(new String[tokenFreqs.size()]);
        ArrayList<Word> lemmas = TreeTaggerResource.INSTANCE.getLemmas(tokens);
        for (int i = 0; i < tokens.length; i++) {
             String lemma = lemmas.get(i).getLemma();
             int frequency = tokenFreqs.get(tokens[i]);
             if (lemmaFreqs.containsKey(lemma))
                 lemmaFreqs.put(lemma, lemmaFreqs.get(lemma) + frequency);
             else
                 lemmaFreqs.put(lemma, frequency);
        }
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ru-lemmas-freq.txt"), ENCODING));
        for (String lemma : lemmaFreqs.keySet()) {
            out.write(lemma + "\t" +  lemmaFreqs.get(lemma));
            out.newLine();
        }
        out.close();
    }
    
    public static final String DEFUALT_TREETAGGER_LOCATION = "TreeTagger/";

    private TreeTaggerWrapper<String> resource;
    private final ArrayList<Word> taggerOutput;

    private TreeTaggerResource() {
        taggerOutput = new ArrayList<Word>();
        if (System.getProperty("treetagger.home") == null)
            System.setProperty("treetagger.home", DEFUALT_TREETAGGER_LOCATION);
        String treeTaggerModelRu = System.getProperty("treetagger.home") + "/lib/russian.par";
        try {
            TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
            tt.setModel(treeTaggerModelRu);
            resource = tt;
            
            resource.setHandler(new TokenHandler<String>() {
                @Override
                public void token(String token, String pos, String lemma) {
                    taggerOutput.add(new Word(lemma, pos));
                }
            });
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "TreeTagger load failed. Proceed without lemmatization.\n"
                    + "Location: " + System.getProperty("treetagger.home"));
            Logger.getLogger(QtaApp.LOGGER_NAME).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Word> getLemmas(String[] tokens){    
        if (resource != null){ 
            try {
                resource.process(tokens);
            } catch (TreeTaggerException ex) {
                JOptionPane.showMessageDialog(null, "TreeTagger crashed.");
                Logger.getLogger(QtaApp.LOGGER_NAME).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "TreeTagger crashed. IO problem.");
                Logger.getLogger(QtaApp.LOGGER_NAME).log(Level.SEVERE, null, ex);
            }
            ArrayList<Word> returnArray = new ArrayList<Word>(taggerOutput);
            taggerOutput.clear();
            return returnArray;
        } else {
            ArrayList<Word> returnArray = new ArrayList<Word>();
            for (String token : tokens) {
                returnArray.add(new Word(token, ""));
            }
            return returnArray;
        }
    }
}
