package de.tuebingen.uni.sfs.qta;

import java.io.IOException;
import java.util.ArrayList;
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
            JOptionPane.showMessageDialog(null, "TreeTagger load failed. Proceed without lemmatization.");
        }
    }

    public ArrayList<Word> getLemmas(String[] tokens){    
        if (resource != null){ 
            try {
                resource.process(tokens);
            } catch (TreeTaggerException ex) {
                JOptionPane.showMessageDialog(null, "TreeTagger crashed.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "TreeTagger crashed.");
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
