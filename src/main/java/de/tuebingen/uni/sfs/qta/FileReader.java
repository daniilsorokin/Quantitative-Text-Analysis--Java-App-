package de.tuebingen.uni.sfs.qta;

import com.google.common.base.Charsets;
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
    
    public static void saveTModelTofile(String fileName, JTable table) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), Charsets.UTF_8));
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
