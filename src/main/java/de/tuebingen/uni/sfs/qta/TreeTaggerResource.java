package de.tuebingen.uni.sfs.qta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public enum TreeTaggerResource {
    INSTANCE;
    
    public static final String TREETAGGER_FOLDER = "/usr/lib/TreeTagger";
    public static final String TREETAGGER_MODEL_DE = "/usr/lib/TreeTagger/lib/german-utf8.par";
    public static final String TREETAGGER_MODEL_RU = "/usr/lib/TreeTagger/lib/russian.par";
    
    private TreeTaggerWrapper<String> resource;
    private final ArrayList<Word> taggerOutput;

    private TreeTaggerResource() {
        taggerOutput = new ArrayList<Word>();
        
        System.out.print("Load TreeTagger: ");
        try {
            System.setProperty("treetagger.home", TREETAGGER_FOLDER);
            TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
            tt.setModel(TREETAGGER_MODEL_RU);
            resource = tt;
            System.out.println(" OK");
            
            resource.setHandler(new TokenHandler<String>() {
                @Override
                public void token(String token, String pos, String lemma) {
                    taggerOutput.add(new Word(lemma, pos));
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(TreeTaggerResource.class.getName()).log(Level.SEVERE, 
                    "TreeTagger load failed. Check the parameter file.");
        }
    }

    public TreeTaggerWrapper<String> getResource() {
        return resource;
    }
    
    /**
     * 
     * @param tokens
     * @return 
     */
    public ArrayList<Word> getLemmas(String[] tokens){        
        try {
            resource.process(tokens);
        } catch (TreeTaggerException ex) {
            Logger.getLogger(TreeTaggerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TreeTaggerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<Word> returnArray = new ArrayList<Word>(taggerOutput);
        taggerOutput.clear();
        return returnArray;
    }
    
    public static void main(String[] args) {
        String sentence = " В середине 1840-х гг. на сцене русской журналистики появляется и вскоре приобретает известность любопытный персонаж.";
        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(sentence);
        ArrayList<Word> lemmas = TreeTaggerResource.INSTANCE.getLemmas(tokens);
        for (Word lemma : lemmas) {
            System.out.println(lemma.getLemma());
        }
    }
}
