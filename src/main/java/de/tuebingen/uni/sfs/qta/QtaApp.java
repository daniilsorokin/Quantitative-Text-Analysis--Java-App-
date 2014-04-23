package de.tuebingen.uni.sfs.qta;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class QtaApp extends JFrame
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
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Test.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    
    private void initGUIComponents() {
        // set frame preferences
        this.setResizable(true);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(600, 200));
        this.setTitle("QTA");
        this.setLocationRelativeTo(null);
        
        // the main panel
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());
        
        
        JPanel fileSelectionPane = new JPanel();
        fileSelectionPane.setLayout(new BoxLayout(fileSelectionPane,BoxLayout.Y_AXIS));
        JPanel fileSelectionInnerPane = new JPanel();
        fileSelectionPane.add(fileSelectionInnerPane);
        
        JButton btnBrowse = new JButton("Browse");
        btnBrowse.setPreferredSize(new Dimension(100, 30));
        fileSelectionInnerPane.add(btnBrowse);
        
        fileTextField = new JTextField("Select file by clicking \"Browse\".");
        fileTextField.setPreferredSize(new Dimension(250, 30));
        fileSelectionInnerPane.add(fileTextField);
        
        
        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    filePath = fc.getSelectedFile().getAbsolutePath();
                    fileTextField.setText(fc.getSelectedFile().getName());
                }
            }
        });
        
        fileTypeBox = new JComboBox();
        fileTypeBox.setPreferredSize(new Dimension(100, 30));
        fileTypeBox.setModel(new DefaultComboBoxModel(FileClass.getNames()));
        fileSelectionInnerPane.add(fileTypeBox);
        
        JButton btnStart = new JButton("Start");
        btnStart.setPreferredSize(new Dimension(100, 30));
        fileSelectionInnerPane.add(btnStart);
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (filePath != null){
                    try {
                        String text = FileReader.getTextFromFile(filePath, FileClass.valueOf(fileTypeBox.getSelectedItem().toString()));
                        frequencyTable = QTAnalyser.computeFrequencyList(text);
                        List<Word> ordered = QTAnalyser.sortMapByValues(frequencyTable);
                        for (Word word : ordered) {
                            tableModel.addRow(new Object[] {
                                word.getLemma(), 
                                word.getPos(), 
                                frequencyTable.get(word)
                            });
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Can't open the selected file.");
                    }
                }
            }
        });
        
        noPunctChBox = new JCheckBox("Leave out punctuation and numbers.");
        JPanel optionsPane = new JPanel();
        optionsPane.add(noPunctChBox);
        JButton btnSave = new JButton("Save table to file");
        btnStart.setPreferredSize(new Dimension(100, 30));
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
        resultsTable.setModel(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(tableModel);
        resultsTable.setRowSorter(sorter);
        
        noPunctChBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (noPunctChBox.isSelected()) {
                    ArrayList<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>();
                    filters.add(RowFilter.regexFilter("(SENT|PUNCT)", 1));
                    filters.add(RowFilter.regexFilter("(@card@)", 0));
                    ((TableRowSorter) resultsTable.getRowSorter())
                            .setRowFilter(RowFilter.notFilter( 
                                    RowFilter.orFilter(filters)
                                    )
                            );
                } else 
                    ((TableRowSorter) resultsTable.getRowSorter()).setRowFilter(null);
            }
        });        

        tableScrollPanel.setViewportView(resultsTable);
        contentPane.add(fileSelectionPane, BorderLayout.NORTH);
//        contentPane.add(optionsPane, BorderLayout.SOUTH);
        contentPane.add(tableScrollPanel, BorderLayout.CENTER);
        this.pack();
    }
}
