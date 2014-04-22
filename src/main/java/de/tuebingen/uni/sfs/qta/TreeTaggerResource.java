package de.tuebingen.uni.sfs.qta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;

/**
 *
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public enum TreeTaggerResource {
    INSTANCE;
    
    public static final String TREETAGGER_FOLDER = "/usr/lib/TreeTagger";
    public static final String TREETAGGER_MODEL_DE = "/usr/lib/TreeTagger/lib/german-utf8.par";
    public static final String TREETAGGER_MODEL_RU = "/usr/lib/TreeTagger/lib/russian.par";
    
    private TreeTaggerWrapper<String> resource;

    private TreeTaggerResource() {
        System.out.print("Load TreeTagger: ");
        try {
            System.setProperty("treetagger.home", TREETAGGER_FOLDER);
            TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
            tt.setModel(TREETAGGER_MODEL_RU);
            resource = tt;
            System.out.println(" OK");
        } catch (IOException ex) {
            Logger.getLogger(TreeTaggerResource.class.getName()).log(Level.SEVERE, 
                    "TreeTagger load failed. Check the parameter file.");
        }
    }

    public TreeTaggerWrapper<String> getResource() {
        return resource;
    }
    
    public ArrayList<String> getLemmas(String[] tokens){
        final ArrayList<String> taggerLemmaOutput = new ArrayList<String>();
        resource.setHandler(new TokenHandler<String>() {
            @Override
            public void token(String token, String pos, String lemma) {
                taggerLemmaOutput.add(lemma);
            }
        });
        try {
            resource.process(tokens);
        } catch (TreeTaggerException ex) {
            Logger.getLogger(TreeTaggerResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TreeTaggerResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<String>(taggerLemmaOutput);
    }
    
    public static void main(String[] args) {
        String sentence = " В середине 1840-х гг. на сцене русской журналистики появляется и вскоре приобретает известность любопытный персонаж.";
        String[] tokens = sentence.replaceAll("[\\.,!?:;\'\'\"\"]", "").split("\\s+");
        ArrayList<String> lemmas = TreeTaggerResource.INSTANCE.getLemmas(tokens);
        for (String lemma : lemmas) {
            System.out.println(lemma);
        }
    }
}
