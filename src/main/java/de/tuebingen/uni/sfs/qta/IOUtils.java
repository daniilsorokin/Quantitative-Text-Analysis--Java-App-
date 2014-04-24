package de.tuebingen.uni.sfs.qta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public class IOUtils {
    
    public static final String ENCODING = "UTF-8";
    
    public static String getTextFromFile(String fileName, SupportedFileTypes type) throws IOException {
        switch(type){
            case DOCX:
                InputStream is = new FileInputStream (fileName);
                XWPFDocument doc = new XWPFDocument(is);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                return extractor.getText();
            default:
            case TXT:
                BufferedReader in = new BufferedReader(new InputStreamReader
                                (new FileInputStream(fileName), ENCODING));
                String line, text = "";
                while((line = in.readLine()) != null ) {
                    text += line.trim() + " ";
                }
                in.close();
                return text;
        }
    }
    
    public static String getTextFromFile(String fileName) throws IOException {
        if (fileName.endsWith(SupportedFileTypes.DOCX.getExtension()))
            return getTextFromFile(fileName, SupportedFileTypes.DOCX);
        else if (fileName.endsWith(SupportedFileTypes.TXT.getExtension()))
            return getTextFromFile(fileName, SupportedFileTypes.TXT);
        else 
            throw new IOException("Can't handle this type of extension.");
    }
    
    public static void saveTModelTofile(String fileName, JTable table) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), ENCODING));
        TableModel model = table.getModel();
        for( int i = 0; i < table.getRowCount(); i++ ) {
            int index = table.convertRowIndexToModel(i);
            for( int j = 0; j < table.getColumnCount(); j++ ) {
                if (model.getColumnClass(j).equals(String.class))
                    out.write("\"" + model.getValueAt( index, j ).toString()+ "\"");
                else
                    out.write(model.getValueAt( index, j ).toString());
                if (j != model.getColumnCount()-1)
                    out.write(",");
            }
            out.newLine();
        }
        out.close();
    }
}
