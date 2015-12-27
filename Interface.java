/**
 * Created by Luke on 22/12/2015.
 */
import java.awt.EventQueue;

import javax.swing.*;
import java.awt.Button;
import java.awt.BorderLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Panel;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;

public class Interface {

    private JFrame frame;
    private final JTextArea txtrName = new JTextArea();
    private JTextArea textArea;
    private TextAreaPrintStream ps;

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

        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        };

        ps = new TextAreaPrintStream(textArea, out);
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

        JMenu mnSystem = new JMenu("System");
        menuBar.add(mnSystem);

        JMenuItem mntmSetting = new JMenuItem("Setting");
        mntmSetting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Setting nw = new Setting();
                nw.NewScreen();
            }
        });
        mnSystem.add(mntmSetting);

        frame.getContentPane().setLayout(null);
        txtrName.setTabSize(6);
        txtrName.setBounds(52, 10, 96, 23);
        frame.getContentPane().add(txtrName);

        Button new_button = new Button("New");
        new_button.setBounds(17, 54, 39, 23);
        frame.getContentPane().add(new_button);
        new_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ps.println("New gesture");
            }
        });

        Button button_train = new Button("Train");
        button_train.setBounds(89, 54, 42, 23);
        frame.getContentPane().add(button_train);
        button_train.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                ps.println("Train");
            }
        });

        JLabel lblNewLabel = new JLabel("Name");
        lblNewLabel.setBounds(10, 15, 46, 15);
        frame.getContentPane().add(lblNewLabel);

        textArea = new JTextArea();
        textArea.setBounds(10, 386, 704, 69);
        frame.getContentPane().add(textArea);
        textArea.setColumns(10);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 386, 704, 69);
        frame.add(scrollPane);

        Button button_clear = new Button("Clear");
        button_clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });
        button_clear.setBounds(729, 410, 42, 23);
        frame.getContentPane().add(button_clear);

        JList list = new JList();
        list.setBounds(10, 94, 138, 271);
        frame.getContentPane().add(list);
    }
}

