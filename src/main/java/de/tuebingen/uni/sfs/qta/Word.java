package de.tuebingen.uni.sfs.qta;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public class Word implements Comparable<Word>{
    private String lemma;
    private String pos;

    public Word(String lemma, String pos) {
        this.lemma = lemma;
        this.pos = pos;
    }

    public String getLemma() {
        return lemma;
    }

    public String getPos() {
        return pos;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.lemma != null ? this.lemma.hashCode() : 0);
        hash = 59 * hash + (this.pos != null ? this.pos.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Word other = (Word) obj;
        if ((this.lemma == null) ? (other.lemma != null) : !this.lemma.equals(other.lemma)) {
            return false;
        }
        if ((this.pos == null) ? (other.pos != null) : !this.pos.equals(other.pos)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Word{" + "lemma=" + lemma + ", pos=" + pos + '}';
    }

    @Override
    public int compareTo(Word t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
