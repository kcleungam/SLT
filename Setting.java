/**
 * Created by Luke on 22/12/2015.
 */
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class Setting {

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
    public static void NewScreen() {
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

        JLabel lblMiniumGestureVelocity = new JLabel("Minium gesture velocity");
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
