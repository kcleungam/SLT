/**
 * Created by Luke on 22/12/2015.
 */
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import java.awt.Button;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridLayout;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Panel;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Interface {

    private JFrame frame;
    private final JTextArea txtrName = new JTextArea();
    private JTextField textField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Interface window = new Interface();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Interface() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.getContentPane().setFont(new Font("Arial", Font.PLAIN, 12));
        frame.setBounds(100, 100, 797, 524);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnSetting = new JMenu("Database");
        menuBar.add(mnSetting);

        JMenuItem mntmImport = new JMenuItem("Import");
        mnSetting.add(mntmImport);

        JMenuItem mntmExport = new JMenuItem("Export");
        mnSetting.add(mntmExport);

        JMenuItem mntmReset = new JMenuItem("Reset");
        mnSetting.add(mntmReset);

        JMenuItem mntmInfo = new JMenuItem("Info");
        mnSetting.add(mntmInfo);

        JMenu mnSetting_1 = new JMenu("System");
        menuBar.add(mnSetting_1);

        JMenuItem mntmSetting = new JMenuItem("Setting");
        mntmSetting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Setting nw = new Setting();
                nw.NewScreen();
            }
        });
        mnSetting_1.add(mntmSetting);

        frame.getContentPane().setLayout(null);
        txtrName.setTabSize(6);
        txtrName.setBounds(52, 10, 96, 23);
        frame.getContentPane().add(txtrName);

        Button button = new Button("New");
        button.setBounds(17, 54, 39, 23);
        frame.getContentPane().add(button);

        Button button_1 = new Button("Train");
        button_1.setBounds(89, 54, 42, 23);
        frame.getContentPane().add(button_1);

        JLabel lblNewLabel = new JLabel("Name");
        lblNewLabel.setBounds(10, 15, 46, 15);
        frame.getContentPane().add(lblNewLabel);

        textField = new JTextField();
        textField.setBounds(10, 386, 704, 69);
        frame.getContentPane().add(textField);
        textField.setColumns(10);

        Button button_2 = new Button("Clear");
        button_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        button_2.setBounds(729, 410, 42, 23);
        frame.getContentPane().add(button_2);

        JList list = new JList();
        list.setBounds(10, 94, 138, 271);
        frame.getContentPane().add(list);
        button_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });
    }
}

