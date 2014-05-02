package de.tuebingen.uni.sfs.qta;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public class QtaApp extends JFrame implements ActionListener
{
    
    public static final String LOGGER_NAME = Logger.GLOBAL_LOGGER_NAME + "." + QtaApp.class.getName() + ".Logger";
    private static final Logger logger = Logger.getLogger(LOGGER_NAME);
    private static final String LOGGER_ENCODING = "UTF-8";
    
    public static void main( String[] args )
    {
        try {
            FileHandler fh = new FileHandler("log.xml");
            fh.setEncoding(LOGGER_ENCODING);
            fh.setLevel(Level.ALL);
            logger.addHandler(fh);            
        } catch (IOException ex) {
            Logger.getLogger(QtaApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(QtaApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        QtaApp app = new QtaApp();
        app.initGUIComponents();
        app.setVisible(true);
        try {
            File conf = new File("qta.conf");
            if (conf.exists()) {
                readParametersFromFile(conf);
            } else {
                String altLocation = QtaApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                altLocation = altLocation.substring(0, altLocation.lastIndexOf("/")) + "/qta.conf";
                conf = new File(altLocation);
                if (conf.exists()) readParametersFromFile(conf);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error while reading config file. Proceed with default settings.");
            logger.log(Level.SEVERE, null, ex);
        }
        TreeTaggerResource.INSTANCE.getClass();
    }

    private static void readParametersFromFile(File file) throws IOException, FileNotFoundException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(file);
        props.load(fis);
        if (props.containsKey("treetagger.location")){
            String ttFolder = props.getProperty("treetagger.location");
            System.setProperty("treetagger.home", ttFolder);
        }
    }
    
    private JComboBox fileTypeBox;
    private JTextField fileTextField;
    private JCheckBox noPunctChBox;
    private JTable resultsTable;
    private JButton btnBrowse, btnStart, btnSave;
    private JFileChooser fc;
    private FileFilter ffInput, ffOutput;
    
    private void initGUIComponents() {
        // set frame preferences
        this.setResizable(true);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(600, 200));
        this.setTitle("QTA");
        this.setLocationRelativeTo(null);
        
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        ffInput = new FileNameExtensionFilter("Supported input formats (*.txt, *.docx)", "txt", "docx");
        ffOutput = new FileNameExtensionFilter("Output format (*.csv)", "csv");
        fc.addChoosableFileFilter(ffInput);
        fc.addChoosableFileFilter(ffOutput);
        
        // the main panel
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());
        
        
        JPanel fileSelectionPane = new JPanel();
        fileSelectionPane.setLayout(new BoxLayout(fileSelectionPane,BoxLayout.Y_AXIS));
        JPanel fileSelectionInnerPane = new JPanel();
        fileSelectionPane.add(fileSelectionInnerPane);
        
        btnBrowse = new JButton("Browse");
        btnBrowse.setPreferredSize(new Dimension(100, 30));
        fileSelectionInnerPane.add(btnBrowse);
        
        fileTextField = new JTextField("Select file by clicking \"Browse\".");
        fileTextField.setPreferredSize(new Dimension(250, 30));
        fileSelectionInnerPane.add(fileTextField);
        
        
        btnBrowse.addActionListener(this);
        
        fileTypeBox = new JComboBox();
        fileTypeBox.setPreferredSize(new Dimension(100, 30));
        fileTypeBox.setModel(new DefaultComboBoxModel(SupportedFileTypes.getNames()));
        fileSelectionInnerPane.add(fileTypeBox);
        
        btnStart = new JButton("Start");
        btnStart.setPreferredSize(new Dimension(100, 30));
        fileSelectionInnerPane.add(btnStart);
        btnStart.addActionListener(this);
        
        noPunctChBox = new JCheckBox("Leave out punctuation and numbers.");
        JPanel optionsPane = new JPanel();
        optionsPane.add(noPunctChBox);
        btnSave = new JButton("Save table to file");
        btnSave.addActionListener(this);
        btnSave.setEnabled(false);
        optionsPane.add(btnSave);

        fileSelectionPane.add(optionsPane);
                
        JScrollPane tableScrollPanel = new JScrollPane();
        resultsTable = new JTable();
        QtaTableModel tableModel = new QtaTableModel( new String [] {"Word lemma", "Part of speech", "Frequency", "Normalized frequency"}, 0 );
        TableRowSorter<QtaTableModel> sorter = new TableRowSorter<QtaTableModel>(tableModel);
        resultsTable.setModel(tableModel);
        resultsTable.setRowSorter(sorter);
        sorter.toggleSortOrder(2); 
        sorter.toggleSortOrder(2); //Reverse order
        
        noPunctChBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (noPunctChBox.isSelected()) {
                    ArrayList<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>();
                    filters.add(RowFilter.regexFilter("(SENT|PUNCT)", 1));
                    filters.add(RowFilter.regexFilter("^(@card@|\\p{Punct}+|[•§])$", 0));
                    ((TableRowSorter) resultsTable.getRowSorter())
                            .setRowFilter(RowFilter.notFilter(RowFilter.orFilter(filters)));
                } else {
                    ((TableRowSorter) resultsTable.getRowSorter()).setRowFilter(null);
                }
            }
        });        

        tableScrollPanel.setViewportView(resultsTable);
        contentPane.add(fileSelectionPane, BorderLayout.NORTH);
        contentPane.add(tableScrollPanel, BorderLayout.CENTER);
        this.pack();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBrowse){
            fc.setFileFilter(ffInput);
            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileTextField.setText(fc.getSelectedFile().getName());
            }
        } else if (e.getSource() == btnStart) {
            String filePath = fc.getSelectedFile().getAbsolutePath();
            if (filePath != null){
                try {
                    String text = IOUtils.getTextFromFile(filePath, SupportedFileTypes.valueOf(fileTypeBox.getSelectedItem().toString()));
                    HashMap<Word,Integer> frequencyTable = QTAnalyser.INSTANCE.computeFrequencyList(text);
                    HashMap<Word,Double> normFrequencyTable = QTAnalyser.INSTANCE.computeNormalizedFrequency(frequencyTable);
                    QtaTableModel tableModel = (QtaTableModel) resultsTable.getModel();
                    tableModel.setRowCount(0);
                    for (Word word : frequencyTable.keySet()) {
                        tableModel.addRow(new Object[] {
                            word.getLemma(),
                            word.getPos(),
                            frequencyTable.get(word),
                            normFrequencyTable.get(word)
                        });
                    }
                    btnSave.setEnabled(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Can't open the selected file.");
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        } else if (e.getSource() == btnSave){
            fc.setFileFilter(ffOutput);
            int returnVal = fc.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String saveTo = fc.getSelectedFile().getAbsolutePath();
                try {
                    IOUtils.saveTModelToCSV(saveTo, resultsTable);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Can't save to the selected file.");
                    logger.log(Level.SEVERE, null, ex);
                }
            }            
        }
    }    
    
    private class QtaTableModel extends DefaultTableModel {
        
        public QtaTableModel(String[] colnames, int numRows){
            super(colnames, numRows);
        }
        
        @Override
        public Class getColumnClass(int column){
            switch(column){
                case 2:
                    return Integer.class;
                case 3:
                    return Double.class;
                case 0:
                case 1:
                default:
                    return String.class;
            }
        }
    }
}
