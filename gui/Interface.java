package gui; /**
 * Created by Luke on 22/12/2015.
 */
import com.leapmotion.leap.Controller;
import main.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class Interface {
    /* Setting window */
    class Setting {

        private JFrame frame;
        private JTextField textField;
        private JTextField textField_1;
        private JTextField textField_2;
        private JTextField textField_3;
        private JLabel lblMaximumPoseVelocity;
        private JTextField textField_4;
        private JLabel lblMiniumGestureFrames;
        private JLabel lblMiniumPoseFrames;
        private JTextField textField_5;
        private JTextField textField_6;
        private JTextField textField_7;
        private JTextField textField_8;
        private JLabel lblHitThreshold;
        private JLabel lblTrainingGestures;
        private JLabel lblConvolutionFactor;
        private JTextField textField_9;
        private JLabel lblDownTime;
        private JTextField textField_10;

        /**
         * Launch the application.
         */
        public void NewScreen() {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        Setting window = new Setting();
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
        public Setting() {
            initialize();
        }

        /**
         * Initialize the contents of the frame.
         */
        private void initialize() {
            frame = new JFrame();
            frame.setBounds(100, 100, 299, 443);
            //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(null);
            frame.setTitle("Setting");

            JLabel lblNewLabel = new JLabel("Recording Trigger");
            lblNewLabel.setBounds(10, 10, 89, 15);
            frame.getContentPane().add(lblNewLabel);

            textField = new JTextField();
            textField.setBounds(163, 10, 96, 21);
            frame.getContentPane().add(textField);
            textField.setColumns(10);

            JLabel lblNewLabel_1 = new JLabel("Gesture encoding");
            lblNewLabel_1.setBounds(10, 35, 89, 15);
            frame.getContentPane().add(lblNewLabel_1);

            textField_1 = new JTextField();
            textField_1.setBounds(163, 35, 96, 21);
            frame.getContentPane().add(textField_1);
            textField_1.setColumns(10);

            JLabel label = new JLabel("Gesture encoding");
            label.setBounds(10, 60, 89, 15);
            frame.getContentPane().add(label);

            JLabel lblMiniumGestureVelocity = new JLabel("Minimum gesture velocity");
            lblMiniumGestureVelocity.setBounds(10, 104, 121, 15);
            frame.getContentPane().add(lblMiniumGestureVelocity);

            textField_2 = new JTextField();
            textField_2.setColumns(10);
            textField_2.setBounds(163, 60, 96, 21);
            frame.getContentPane().add(textField_2);

            textField_3 = new JTextField();
            textField_3.setColumns(10);
            textField_3.setBounds(163, 104, 96, 21);
            frame.getContentPane().add(textField_3);

            lblMaximumPoseVelocity = new JLabel("Maximum pose velocity");
            lblMaximumPoseVelocity.setBounds(10, 129, 121, 15);
            frame.getContentPane().add(lblMaximumPoseVelocity);

            textField_4 = new JTextField();
            textField_4.setColumns(10);
            textField_4.setBounds(163, 132, 96, 21);
            frame.getContentPane().add(textField_4);

            lblMiniumGestureFrames = new JLabel("Minium gesture frames");
            lblMiniumGestureFrames.setBounds(10, 154, 121, 15);
            frame.getContentPane().add(lblMiniumGestureFrames);

            lblMiniumPoseFrames = new JLabel("Minium pose frames");
            lblMiniumPoseFrames.setBounds(10, 179, 121, 15);
            frame.getContentPane().add(lblMiniumPoseFrames);

            textField_5 = new JTextField();
            textField_5.setColumns(10);
            textField_5.setBounds(163, 157, 96, 21);
            frame.getContentPane().add(textField_5);

            textField_6 = new JTextField();
            textField_6.setColumns(10);
            textField_6.setBounds(163, 182, 96, 21);
            frame.getContentPane().add(textField_6);

            textField_7 = new JTextField();
            textField_7.setColumns(10);
            textField_7.setBounds(163, 207, 96, 21);
            frame.getContentPane().add(textField_7);

            textField_8 = new JTextField();
            textField_8.setColumns(10);
            textField_8.setBounds(163, 238, 96, 21);
            frame.getContentPane().add(textField_8);

            lblHitThreshold = new JLabel("Hit threshold");
            lblHitThreshold.setBounds(10, 204, 121, 15);
            frame.getContentPane().add(lblHitThreshold);

            lblTrainingGestures = new JLabel("Training gestures");
            lblTrainingGestures.setBounds(10, 238, 121, 15);
            frame.getContentPane().add(lblTrainingGestures);

            lblConvolutionFactor = new JLabel("Convolution factor");
            lblConvolutionFactor.setBounds(10, 266, 121, 15);
            frame.getContentPane().add(lblConvolutionFactor);

            textField_9 = new JTextField();
            textField_9.setColumns(10);
            textField_9.setBounds(163, 266, 96, 21);
            frame.getContentPane().add(textField_9);

            lblDownTime = new JLabel("Down time");
            lblDownTime.setBounds(10, 291, 121, 15);
            frame.getContentPane().add(lblDownTime);

            textField_10 = new JTextField();
            textField_10.setColumns(10);
            textField_10.setBounds(163, 291, 96, 21);
            frame.getContentPane().add(textField_10);

            JButton btnSave = new JButton("Save");
            btnSave.setBounds(96, 345, 87, 23);
            frame.getContentPane().add(btnSave);
        }
    }

    private JFrame frame;
    private final JTextArea txtrName = new JTextArea();
    private static JTextArea textArea;
    private static JScrollPane textScrollPane;
    private static TextAreaPrintStream ps;
    private static JLabel label_1, label_2, label_3, label_start;

    //private static boolean recordingMode = false;
    private static boolean recording = false;
    private volatile static boolean yes = false, no = false;

    private static Database db = new Database("Signs", "HK_Signs");
    private static SignBank allSigns;
    private SampleListener sampleListener = new SampleListener();
    private Controller controller = new Controller();
    private DTW dtw = new DTW();

    // the gui.visualizer applet
    private Visualizer visualizer = new Visualizer();


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
        frame.setBounds(10, 10, 690, 700);
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
                autoScrollDown();
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

        final JList list = new JList();
        list.setBounds(10, 94, 138, 290);
        frame.getContentPane().add(list);

        JScrollPane listScrollPane = new JScrollPane(list);
        listScrollPane.setBounds(10, 94, 138, 390);
        frame.add(listScrollPane);

        final DefaultListModel listModel = new DefaultListModel();
        list.setModel(listModel);

        for (String key : allSigns.getAllSigns().keySet()) {
            listModel.addElement(key);
        }

        visualizer.setBounds(160, 10, 500, 500);
        frame.getContentPane().add(visualizer);
        Runnable liveHand = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        visualizer.traceLM(controller.frame());
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread livevisual = new Thread(liveHand);
        livevisual.start();

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                if (e.getClickCount() == 2){
                    String selectedItem = (String) list.getSelectedValue();
                    Sign sign = allSigns.getSign(selectedItem);

                    ps.println("Sign Name   : " + sign.getName() + " ,   Consist of "
                            + sign.getAllSamples().size() + " Sample");
                    ps.println("Initial Palm Count  : " + sign.getInitialPalmCount());
                    ps.println("Initial Hand Type   :" + sign.getInitialHandType());
                    ps.println("Initial Finger Count = " + sign.getInitialFingerCount() + "\n");

                    autoScrollDown();

                    // TODO: mod this..
                    //gui.visualizer.traceLM(controller.frame());
                }
            }
        };
        list.addMouseListener(mouseListener);

        mntmReset.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                // Reset the variables
                yes = false;
                no = false;

                Runnable resetRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ps.println("Reset database?");
                        autoScrollDown();

                        while (true) {
                            if (no) {
                                ps.println("Operation cancelled!");
                                autoScrollDown();

                                return;
                            } else if (yes) {
                                break;
                            }
                        }

                        try{
                            db.removeAllSign();
                            allSigns.removeAllSign();
                            listModel.removeAllElements();
                            ps.println("Database has been reset!");
                        }catch(Exception ex){
                            ps.println("Exception caught!");
                            autoScrollDown();
                        }
                    }
                };

                Thread thread = new Thread(resetRunnable);
                thread.start();
            }
        });

        //TODO  --------   New

        Button new_button = new Button("New");
        new_button.setBounds(10, 54, 39, 23);
        frame.getContentPane().add(new_button);
        new_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!recording){
                    recording = true;
                    Runnable NewRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Sign sign;
                            String signName = txtrName.getText();

                            if(signName.equals("")){
                                ps.println("Please enter a name!");
                                autoScrollDown();
                                recording = false;
                                return;
                            }

                            if (allSigns.getAllSigns().containsKey(signName)) {
                                ps.println("This name is already existed in the database.");
                                autoScrollDown();
                            } else {
                                ready();
                                sampleListener.reset();
                                sampleListener.gainFocus();

                                try {
                                    while (true) {
                                        if (sampleListener.checkFinish()) {
                                            if (sampleListener.checkValid()) {
                                                System.out.println(recording);
                                                if (savePrompt()) {
                                                    //recordSign(sampleListener.returnOneSample(), signName, sign, allsign);
                                                    sign = new Sign(signName, new Sample(sampleListener.returnOneSample()));
                                                    //ToDo: handle the boolean return value aftter adding the given sign
                                                    allSigns.addSign(signName, sign);
                                                    db.addSign(sign);
                                                    ps.println("New gesture: " + signName);
                                                    autoScrollDown();
                                                    listModel.addElement(signName);
                                                }
                                            } else {
                                                ps.println("The recording is invalid.");
                                                autoScrollDown();
                                            }
                                            recording = false;
                                            break;
                                        }
                                        // The current thread is too fast, will fail to
                                        // trace Listener if missing this code
                                        Thread.currentThread().sleep(10);
                                    }
                                } catch (Exception ex) {
                                    ps.println("Exception caught!");
                                    autoScrollDown();
                                    recording = false;
                                }

                                recording = false;
                                sampleListener.lostFocus();
                            }
                        }
                    };

                    Thread thread = new Thread(NewRunnable);
                    thread.start();
                }
            }
        });

        //TODO  --------   Train

        Button button_train = new Button("Train");
        button_train.setBounds(58, 54, 42, 23);
        frame.getContentPane().add(button_train);
        button_train.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(!recording){
                    recording = true;

                    Runnable TrainRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Sign sign;

                            //printTraining();
                            //System.out.println("Please enter the name of the sign you want to train: ");
                            String trainName = (String) list.getSelectedValue();

                            if (allSigns.getAllSigns().containsKey(trainName)) {
                                ps.println("Sign found, ready to start training");
                                autoScrollDown();

                                // recordingMode = true;
                                ready();

                                // SampleListener trainListener = new
                                // SampleListener(); //new a listener everytime
                                // controller.addListener(trainListener); // add
                                // listener, grab data
                                sampleListener.reset();
                                sampleListener.gainFocus();
                                /**
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
                                                    autoScrollDown();
                                                }
                                            } else {
                                                ps.println("The recording is invalid!");
                                                autoScrollDown();
                                            }
                                            recording = false;
                                            break;
                                        }
                                        // It is necessary, without it, bug occur
                                        Thread.currentThread().sleep(10);
                                    }
                                }catch(Exception ex){
                                    ps.println("Exception caught!");
                                    autoScrollDown();

                                    recording = false;
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
                                ps.println("Please choose a sign!");
                                autoScrollDown();

                                recording = false;
                            }
                            recording = false;
                            return;
                        }
                    };

                    Thread thread = new Thread(TrainRunnable);
                    thread.start();
                }

            }
        });

        Button btnInfo = new Button("Info");
        btnInfo.setBounds(106, 54, 42, 23);
        frame.getContentPane().add(btnInfo);
        btnInfo.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 String selectedItem = (String) list.getSelectedValue();
                 Sign sign = allSigns.getSign(selectedItem);

                 ps.println("Sign Name   : " + sign.getName() + " ,   Consist of "
                         + sign.getAllSamples().size() + " Sample");
                 ps.println("Initial Palm Count  : " + sign.getInitialPalmCount());
                 ps.println("Initial Hand Type   :" + sign.getInitialHandType());
                 ps.println("Initial Finger Count = " + sign.getInitialFingerCount());

                 autoScrollDown();
             }
        });

        //TODO  --------   DTW

        Runnable DTWRunnable = new Runnable() {
            @Override
            public void run() {
                Boolean validRec = false;
                while (true) {
                    if (!recording) {
                        sampleListener.reset();
                        sampleListener.gainFocus();

                        Sample rSample=null;

                        try {
                            while (true) {
                                if (!recording) {
                                    if (sampleListener.checkFinish()) {
                                        if (sampleListener.checkValid()) {

                                            rSample = new Sample(sampleListener.returnOneSample());
                                            validRec = true;

                                        } else {
                                            ps.println("The recording is invalid. ");
                                            validRec = false;
                                            autoScrollDown();
                                            Thread.currentThread().sleep(10);
                                        }
                                        break;
                                    }
                                    // The current thread is too fast, will fail to
                                    // trace Listener if missing this code
                                    Thread.currentThread().sleep(10);
                                }else{
                                    Thread.currentThread().sleep(10);       // !!!!!!Remember to add else and add sleep, I spend a lot of time to find this bug!!!!!
                                }
                            }

                            sampleListener.lostFocus();
                            if (!recording) {
                                if (validRec == true) {
                                    dtw.setRSample(rSample);

                                    int tolerance=1;

                                    // Retrieve Sign with finger count and hand type with fingerCount tolerance
                                    /* @Depreciate
                                    HashMap<String, Sign> signByBoth = db.getSignsByBoth(rSample.initialFingerCount, rSample.initialHandType);
                                    signByBoth.putAll(db.getSignsByBoth(rSample.initialFingerCount - 1, rSample.initialHandType));
                                    signByBoth.putAll(db.getSignsByBoth(rSample.initialFingerCount + 1, rSample.initialHandType));*/
                                    HashMap<String, Sign> signByBoth=db.getSignsByBoth(rSample.getInitialFingerCount(), rSample.getInitialHandType(),tolerance);

                                    for (Sign storedSign : signByBoth.values()) {
                                        dtw.setStoredSign(storedSign);
                                        dtw.calDTW();
                                    }

                                    dtw.printResult();
                                    if (dtw.result.equals("Unknown Gesture !")) {
                                        ps.println("Unknown Gesture !");
                                        autoScrollDown();
                                    } else {
                                        ps.println("The most similar gesture is " + dtw.result);
                                        Speech.play(dtw.result, Speech.LANGUAGE.ENGLISH);
                                        autoScrollDown();
                                        ps.println("The minimum cost of DTW is " + dtw.bestMatch);
                                        autoScrollDown();
                                    }

                                    dtw.reset();
                                    Thread.currentThread().sleep(20);
                                } else {
                                    dtw.reset();
                                    Thread.currentThread().sleep(20);
                                }        //   do nothing
                            }
                        }catch(Exception e){
                            ps.println("Exception caught!");
                            autoScrollDown();
                        }

                    }else{
                        try {
                            Thread.currentThread().sleep(10);
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        };

        Thread dtwThread = new Thread(DTWRunnable);
        dtwThread.start();



        Button btnRemove = new Button("Remove");
        btnRemove.setBounds(48, 490, 61, 23);
        frame.getContentPane().add(btnRemove);
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the corresponding sign name
                final String signName = (String) list.getSelectedValue();
                final int index = list.getSelectedIndex();

                if(signName == null){
                    ps.println("Please select a sign.");
                    autoScrollDown();

                    return;
                }

                // Reset the variables
                yes = false;
                no = false;

                Runnable removeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        ps.println("Remove Sign '" + signName + "'?");
                        autoScrollDown();

                        while (true) {
                            if (no) {
                                ps.println("Operation cancelled!");
                                autoScrollDown();

                                return;
                            } else if (yes) {
                                break;
                            }
                        }

                        try{
                            Sign sign = db.getSignsByName(signName);
                            db.removeSign(sign);
                            allSigns.removeSign(signName);
                            listModel.removeElementAt(index);
                            ps.println("Sign '" + signName + "' is removed.");
                            autoScrollDown();
                        }catch(Exception ex){
                            ps.println("Exception caught!");
                            autoScrollDown();
                        }
                    }
                };

                Thread thread = new Thread(removeRunnable);
                thread.start();

            }
        });

        Button btnClear = new Button("Clear");
        btnClear.setBounds(152, 600, 61, 23);
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });
        frame.getContentPane().add(btnClear);

        Button btnYes = new Button("Yes");
        btnYes.setBounds(10, 600, 61, 23);
        frame.getContentPane().add(btnYes);
        btnYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yes = true;
            }
        });

        Button btnNo = new Button("No");
        btnNo.setBounds(81, 600, 61, 23);
        frame.getContentPane().add(btnNo);
        btnNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                no = true;
            }
        });

        JLabel lblNewLabel = new JLabel("Name");
        lblNewLabel.setBounds(10, 15, 46, 15);
        frame.getContentPane().add(lblNewLabel);

        textArea = new JTextArea();
        textArea.setBounds(10, 385, 761, 65);
        frame.getContentPane().add(textArea);
        textArea.setColumns(10);

        textScrollPane = new JScrollPane(textArea);
        textScrollPane.setBounds(10, 520, 655, 65);
        frame.add(textScrollPane);

        label_1 = new JLabel("1");
        label_1.setForeground(Color.RED);
        label_1.setFont(new Font("Papyrus", Font.PLAIN, 150));
        label_1.setBounds(420, 121, 274, 177);
        frame.getContentPane().add(label_1);
        label_1.setVisible(false);

        label_2 = new JLabel("2");
        label_2.setForeground(Color.RED);
        label_2.setFont(new Font("Papyrus", Font.PLAIN, 150));
        label_2.setBounds(420, 121, 274, 177);
        frame.getContentPane().add(label_2);
        label_2.setVisible(false);

        label_3 = new JLabel("3");
        label_3.setForeground(Color.RED);
        label_3.setFont(new Font("Papyrus", Font.PLAIN, 150));
        label_3.setBounds(420, 121, 274, 177);
        frame.getContentPane().add(label_3);
        label_3.setVisible(false);

        label_start = new JLabel("Start");
        label_start.setForeground(Color.RED);
        label_start.setFont(new Font("Papyrus", Font.PLAIN, 100));
        label_start.setBounds(313, 121, 274, 177);
        frame.getContentPane().add(label_start);
        label_start.setVisible(false);
    }

    /**
     * Print out the basic info of the Sign stored in the trainer.
     */
    public static void printAllDetails() {
        ps.println("There are " + allSigns.getAllSigns().size() + " sign in database:\n");

        for (String key : allSigns.getAllSigns().keySet()) {
            ps.println("Sign Name: " + key + " ,   Consist of "
                    + allSigns.getAllSigns().get(key).getAllSamples().size() + " Sample");
            ps.println("Initial Palm Count: " + allSigns.getAllSigns().get(key).getInitialPalmCount());
            ps.println("Initial Hand Type: " + allSigns.getAllSigns().get(key).getInitialHandType());
            ps.println("Initial Finger Count: " + allSigns.getAllSigns().get(key).getInitialFingerCount());
        }

        ps.println("All sign are printed");
    }

    /**
     * Provide a 3-second countdown.
     */
    public static void ready() {
        ps.println("Countdown!");
        autoScrollDown();

        for (int count = 3; count >= 0; count--) {
            try {
                String countStr = "" + count;
                ps.println(countStr);
                autoScrollDown();
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
        // Reset the variables
        yes = false;
        no = false;

        ps.println("Save this Sample?");
        autoScrollDown();

        while (true) {
            if (yes) {
                ps.println("The recording is done!");
                autoScrollDown();
                return true;
            } else if (no) {
                ps.println("The Sample is not added.");
                autoScrollDown();
                return false;
            }
        }
    }

    /**
     *  Auto Scroll down
     */
    public static void autoScrollDown() {
        int height = (int)textArea.getPreferredSize().getHeight();
        textScrollPane.getVerticalScrollBar().setValue(height);
    }

}


