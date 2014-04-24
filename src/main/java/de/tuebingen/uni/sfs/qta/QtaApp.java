package de.tuebingen.uni.sfs.qta;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * @author Daniil Sorokin<daniil.sorokin@uni-tuebingen.de>
 */
public class QtaApp extends JFrame implements ActionListener
{
    public static void main( String[] args )
    {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QtaApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QtaApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QtaApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QtaApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }        
        QtaApp app = new QtaApp();
        app.initGUIComponents();
        app.setVisible(true);
    }
    
    private String filePath;
    private HashMap<Word,Integer> frequencyTable;
    
    private JComboBox fileTypeBox;
    private JTextField fileTextField;
    private DefaultTableModel tableModel;
    private JCheckBox noPunctChBox;
    private JTable resultsTable;
    private JButton btnBrowse, btnStart, btnSave;
    private JFileChooser fc;
    
    private void initGUIComponents() {
        // set frame preferences
        this.setResizable(true);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(600, 200));
        this.setTitle("QTA");
        this.setLocationRelativeTo(null);
        
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
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
        fileTypeBox.setModel(new DefaultComboBoxModel(FileClass.getNames()));
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
        tableModel = new DefaultTableModel( new String [] {"Word lemma", "Part of speech", "Frequency"}, 0 ) {
            @Override
            public Class getColumnClass(int column){
                switch(column){
                    case 2:
                        return Integer.class;
                    case 0:
                    case 1:
                    default:
                        return String.class;
                }
            }
        };
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(tableModel);
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
                    filters.add(RowFilter.regexFilter("(@card@)", 0));
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
            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                filePath = fc.getSelectedFile().getAbsolutePath();
                fileTextField.setText(fc.getSelectedFile().getName());
            }
        } else if (e.getSource() == btnStart) {
            if (filePath != null){
                try {
                    String text = FileReader.getTextFromFile(filePath, FileClass.valueOf(fileTypeBox.getSelectedItem().toString()));
                    frequencyTable = QTAnalyser.computeFrequencyList(text);
                    tableModel.setRowCount(0);
                    
                    for (Word word : frequencyTable.keySet()) {
                        tableModel.addRow(new Object[] {
                            word.getLemma(),
                            word.getPos(),
                            frequencyTable.get(word)
                        });
                    }
                    btnSave.setEnabled(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Can't open the selected file.");
                }
            }
        } else if (e.getSource() == btnSave){
            int returnVal = fc.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String saveTo = fc.getSelectedFile().getAbsolutePath();
                try {
                    FileReader.saveTModelTofile(saveTo, resultsTable);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Can't save to the selected file.");
                }
            }            
        }
    }    
}
