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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public class IOUtils {
    
    public static final String ENCODING = "UTF-8";
    
    /**
     * Extracts textual information from a file of a supported type. For text 
     * files only "Unicode (utf-8)" encoding is supported.
     * 
     * @param fileName the name of the file containing text
     * @param type one of the supported types of files
     * @return String with the whole text from the file
     * @throws IOException
     */
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
    
    /**
     * Extracts textual information from a file of a supported type. Type of 
     * the file is guessed based on the file extension. For text files only 
     * "Unicode (utf-8)" encoding is supported. If file seems to have 
     * an unsupported type a IOException is thrown.
     * 
     * @param fileName the name of the file containing text
     * @return String with the whole text from the file
     * @throws IOException 
     */
    public static String getTextFromFile(String fileName) throws IOException {
        if (fileName.endsWith(SupportedFileTypes.DOCX.getExtension()))
            return getTextFromFile(fileName, SupportedFileTypes.DOCX);
        else if (fileName.endsWith(SupportedFileTypes.TXT.getExtension()))
            return getTextFromFile(fileName, SupportedFileTypes.TXT);
        else 
            throw new IOException("Can't handle this type of extension.");
    }
    
    /**
     * Saves information from a table interface element to a file in CSV format.
     * Only information that is actually displayed at the moment is saved.
     * 
     * @param fileName the name of the file
     * @param table table that to save
     * @throws IOException 
     */
    public static void saveTModelToCSV(String fileName, JTable table) throws IOException {
        if (!fileName.endsWith(".csv")) fileName += ".csv";
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
    
    public static void saveTModelToXlsx (String fileName, JTable table) throws IOException {
        if (!fileName.endsWith(".xlsx")) fileName += ".xlsx";
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        TableModel model = table.getModel();
        for( int i = 0; i < table.getRowCount(); i++ ) {
            int index = table.convertRowIndexToModel(i);
            Row row = sheet.createRow((short) i);
            for( int j = 0; j < table.getColumnCount(); j++ ) {
                row.createCell(j).setCellValue(model.getValueAt( index, j ).toString());
            }
        }
        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.close();
    }
}
