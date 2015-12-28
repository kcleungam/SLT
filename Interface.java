/**
 * Created by Luke on 22/12/2015.
 */
import com.leapmotion.leap.Controller;

import java.awt.EventQueue;

import javax.swing.*;
import java.awt.Button;
import java.awt.BorderLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.Panel;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Interface {

    private JFrame frame;
    private final JTextArea txtrName = new JTextArea();
    private JTextArea textArea;
    private static TextAreaPrintStream ps;
    private static JButton btnYes, btnNo;
    private static JLabel label_1, label_2, label_3;

    private static Database db = new Database("Signs", "HK_Signs");

    static boolean recordingMode = false;
    static boolean recording = false;

    static SignBank allSigns;
    SampleListener sampleListener = new SampleListener();
    Controller controller = new Controller();

    /**
     * Launch the application.
     */
    public static void main(String[] args){
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
    public Interface() throws Exception{
        initialize();

        // Add listener, grab data
        controller.addListener(sampleListener);

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
    private void initialize() throws Exception{
        allSigns = new SignBank(db);

        frame = new JFrame();
        frame.getContentPane().setFont(new Font("Arial", Font.PLAIN, 12));
        frame.setBounds(100, 100, 797, 560);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Sign Language Translator");

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
        mntmInfo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                printAllDetails();
            }
        });

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

        JList list = new JList();
        list.setBounds(10, 94, 138, 271);
        frame.getContentPane().add(list);

        DefaultListModel listModel = new DefaultListModel();
        list.setModel(listModel);

        for (String key : allSigns.getAllSigns().keySet()) {
            listModel.addElement(key);
        }

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2){
                    String selectedItem = (String) list.getSelectedValue();
                    Sign sign = allSigns.getSign(selectedItem);

                    ps.println("Sign Name   : " + sign.getName() + " ,   Consist of "
                            + sign.getAllSamples().size() + " Sample");
                    ps.println("Hand Count  : " + sign.getHandCount());
                    ps.println("Hand Type   :" + sign.getHandType());
                    ps.println("Finger Count = " + sign.getFingerCount() + "\n");
                }
            }
        };
        list.addMouseListener(mouseListener);

        mntmReset.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                try{
                    db.removeAllSign();
                    allSigns.removeAllSign();
                }catch(Exception ex){
                    ps.println("Exception caught!");
                }

                listModel.removeAllElements();
            }
        });

        Button new_button = new Button("New");
        new_button.setBounds(10, 54, 54, 23);
        frame.getContentPane().add(new_button);
        new_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sign sign;
                String signName = txtrName.getText();

                if(signName.equals("")){
                    ps.println("Please enter a name!");
                    return;
                }

                if (allSigns.getAllSigns().containsKey(signName)) {
                    ps.println("The name existed in the database.");
                } else {
                    ready();
                    sampleListener.reset();
                    sampleListener.gainFocus();

                    try {
                        while (true) {
                            if (sampleListener.checkFinish()) {
                                if (sampleListener.checkValid()) {
                                    if (savePrompt()) {
                                        //recordSign(sampleListener.returnOneSample(), signName, sign, allsign);
                                        sign = new Sign(signName, new Sample(sampleListener.returnOneSample()));
                                        //ToDo: handle the boolean return value aftter adding the given sign
                                        allSigns.addSign(signName, sign);
                                        db.addSign(sign);
                                        ps.println("New gesture:" + signName);
                                        listModel.addElement(signName);
                                    }
                                } else {
                                    ps.println("The recording is invalid. ");
                                }
                                break;
                            }
                            // The current thread is too fast, will fail to
                            // trace Listener if missing this code
                            Thread.currentThread().sleep(10);
                        }
                    } catch (Exception ex) {
                        ps.println("Exception caught!");
                    }

                    sampleListener.lostFocus();
                }
            }
        });

        Button button_train = new Button("Train");
        button_train.setBounds(81, 54, 54, 23);
        frame.getContentPane().add(button_train);
        button_train.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Sign sign;
                Scanner sc = new Scanner(System.in);

                //printTraining();
                //System.out.println("Please enter the name of the sign you want to train: ");
                String trainName = (String) list.getSelectedValue();

                if (allSigns.getAllSigns().containsKey(trainName)) {
                    ps.println("Sign found, ready to start training");
                    // recordingMode = true;
                    ready();

                    // SampleListener trainListener = new
                    // SampleListener(); //new a listener everytime
                    // controller.addListener(trainListener); // add
                    // listener, grab data
                    sampleListener.reset();
                    sampleListener.gainFocus();
                    /*
                     * keep grabbing the frame from controller, check
                     * whether it is recordable, add to the oneSample if
                     * it is. """"put it as one sample of specific
                     * sign"""
                     */
                    try{
                        while (true) {
                            if (sampleListener.checkFinish()) {
                                if (sampleListener.checkValid()) {
                                    if (savePrompt()) {
                                        //trainOneSign(sampleListener.returnOneSample(), trainName, allsign);
                                        //ToDo: handle the boolean return type after adding sample to the given sign
                                        sign = new Sign(trainName,new Sample(sampleListener.returnOneSample()));
                                        allSigns.addSign(trainName,sign);
                                        db.addSign(sign);
                                        ps.println("Training completed!");
                                    }
                                } else {
                                    ps.println("The recording is invalid!");
                                }
                                break;
                            }
                            // It is necessary, without it, bug occur
                            Thread.currentThread().sleep(10);
                        }
                    }catch(Exception ex){
                        ps.println("Exception caught!");
                    }

                    // controller.removeListener(trainListener);
                    sampleListener.lostFocus();
							/*
							 * while (true) { Frame frame = controller.frame();
							 * if (recordingMode == true) {
							 * trainOneSign(frame,oneSample,trainName,allsign);
							 * //recordingMode = false at the end of
							 * trainOneSign } else { break; } }
							 */
                } else { // sign not found
                    ps.println("Sign not found!");
                    // recordingMode = false;
                }
            }
        });

        Button btnClear = new Button("Clear");
        btnClear.setBounds(152, 460, 61, 23);
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });
        frame.getContentPane().add(btnClear);

        btnYes = new JButton("Yes");
        btnYes.setBounds(10, 460, 61, 23);
        frame.getContentPane().add(btnYes);

        btnNo = new JButton("No");
        btnNo.setBounds(81, 460, 61, 23);
        frame.getContentPane().add(btnNo);

        JLabel lblNewLabel = new JLabel("Name");
        lblNewLabel.setBounds(10, 15, 46, 15);
        frame.getContentPane().add(lblNewLabel);

        textArea = new JTextArea();
        textArea.setBounds(10, 385, 761, 68);
        frame.getContentPane().add(textArea);
        textArea.setColumns(10);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 385, 761, 68);
        frame.add(scrollPane);

        label_1 = new JLabel("1");
        label_1.setFont(new Font("Papyrus", Font.PLAIN, 150));
        label_1.setBounds(420, 121, 274, 177);
        frame.getContentPane().add(label_1);
        label_1.setVisible(false);

        label_2 = new JLabel("2");
        label_2.setFont(new Font("Papyrus", Font.PLAIN, 150));
        label_2.setBounds(420, 121, 274, 177);
        frame.getContentPane().add(label_2);
        label_2.setVisible(false);

        label_3 = new JLabel("3");
        label_3.setFont(new Font("Papyrus", Font.PLAIN, 150));
        label_3.setBounds(420, 121, 274, 177);
        frame.getContentPane().add(label_3);
        label_3.setVisible(false);
    }

    public static void printTraining() {
        System.out.println("There are " + allSigns.getAllSigns().size() + " sign in database:\n");

        for (String key : allSigns.getAllSigns().keySet()) {
            System.out.println("Sign Name:  " + key + "   , Consist of "
                    + allSigns.getAllSigns().get(key).getAllSamples().size() + " Sample");
        }

        System.out.println("");
    }

    /**
     * Print out the basic info of the Sign stored in the trainer.
     */
    public static void printAllDetails() {
        ps.println("There are " + allSigns.getAllSigns().size() + " sign in database:\n");

        for (String key : allSigns.getAllSigns().keySet()) {
            ps.println("Sign Name: " + key + " ,   Consist of "
                    + allSigns.getAllSigns().get(key).getAllSamples().size() + " Sample");
            ps.println("Hand Count: " + allSigns.getAllSigns().get(key).getHandCount());
            ps.println("Hand Type: " + allSigns.getAllSigns().get(key).getHandType());
            ps.println("Finger Count: " + allSigns.getAllSigns().get(key).getFingerCount() + "\n");
        }

        ps.println("All sign are printed");
    }

    /**
     * Provide a 3-second countdown.
     */
    public static void ready() {
        /* GUI countdown
        for (int count = 3; count >= 0; count--) {
            try {
                if(count == 3){
                    label_3.setVisible(true);
                }else if(count == 2){
                    label_3.setVisible(false);
                    label_2.setVisible(true);
                }else if(count == 1){
                    label_2.setVisible(false);
                    label_1.setVisible(true);
                }else{
                    label_1.setVisible(false);
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        */
        for (int count = 3; count >= 0; count--) {
            try {
                System.out.println(count);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Give a save prompt
     */
    public static boolean savePrompt() {
        /* Yes and No Buttons not work yet
        System.out.println("Save this Sample?");

        while (true) {
            if (btnYes.getModel().isPressed()) {
                System.out.println("Done!!!");
                return true;
            } else if (btnNo.getModel().isPressed()) {
                System.out.println("The Sample is not added.");
                return false;
            }

        }*/

        System.out.println("Save this Sample? (Y/N)");
        Scanner sc = new Scanner(System.in);//added
        String signName=new String();//added
        while(true){//while (signName = sc.next()) {
            signName=sc.next();//added
            if (signName.equals("Y")) {
                ps.println("Done!!!");
                return true;
            } else if (signName.equals("N")) {
                ps.println("The Sample is not added.");
                return false;
            } else {
                System.out.println("Please input Y or N.");
                System.out.println("Save this Sample? (Y/N)");
            }
        }
    }

}


