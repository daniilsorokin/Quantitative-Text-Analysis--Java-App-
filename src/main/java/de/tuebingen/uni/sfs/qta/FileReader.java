package de.tuebingen.uni.sfs.qta;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author dsorokin
 */
public class FileReader {
    
    public static String getTextFromFile(String fileName, FileClass type) throws IOException {
        switch(type){
            case DOCX:
                InputStream is = new FileInputStream (fileName);
                XWPFDocument doc = new XWPFDocument(is);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                return extractor.getText();
            default:
            case TXT:
                BufferedReader in = new BufferedReader(new InputStreamReader
                                (new FileInputStream(fileName), Charsets.UTF_8));
                String line, text = "";
                while((line = in.readLine()) != null ) {
                    text += line.trim() + " ";
                }
                in.close();
                return text;
        }
    }
    
    public static String getTextFromFile(String fileName) throws IOException {
        if (fileName.endsWith(FileClass.DOCX.getExtension()))
            return getTextFromFile(fileName, FileClass.DOCX);
        else if (fileName.endsWith(FileClass.TXT.getExtension()))
            return getTextFromFile(fileName, FileClass.TXT);
        else 
            throw new IOException("Can't handle this type of extension.");
    }
}
