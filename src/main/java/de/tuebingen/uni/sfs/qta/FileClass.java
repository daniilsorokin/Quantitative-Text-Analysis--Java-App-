package de.tuebingen.uni.sfs.qta;

/**
 *
 * @author dsorokin
 */
public enum FileClass {
    TXT, DOCX;
    
    public String getExtension(){
        return "." + this.name().toLowerCase();
    }
    
    public static String[] getNames(){
        FileClass[] classes = FileClass.values();
        String[] names = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            names[i] = classes[i].name();
        }
        return names;
    }
}
