package de.tuebingen.uni.sfs.qta;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public enum SupportedFileTypes {
    DOCX, TXT;
    
    public String getExtension(){
        return "." + this.name().toLowerCase();
    }
    
    public static String[] getNames(){
        SupportedFileTypes[] classes = SupportedFileTypes.values();
        String[] names = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            names[i] = classes[i].name();
        }
        return names;
    }
}
