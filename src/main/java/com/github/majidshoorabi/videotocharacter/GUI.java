package com.github.majidshoorabi.videotocharacter;

import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class GUI extends JFrame {

    private JPanel mainPanel;
    private JLabel imageHolder;
    private JButton captureButton;
    private JTextArea textArea;
    private int width = 320;
    private int height = 240;
    Webcam webcam;
    Boolean isRunning;

    public GUI() {
        initComponents();
        this.isRunning = false;
        this.webcam = Webcam.getDefault();
        this.webcam.setViewSize(new Dimension(this.width, this.height));
        webcam.open();


        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // video
                if (!isRunning) {
                    isRunning = true;
                    new GUI.VideoFeedTaker().start();
                } else {
                    isRunning = false;
                }
            }
        });
    }

    private void initComponents() {
        setTitle("Image Capture");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setContentPane(this.mainPanel);
        setSize(this.width, this.height * 3);
//        textArea.setFont(textArea.getFont().deriveFont(3f));
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 3));
    }

    class VideoFeedTaker extends Thread {
        @Override
        public void run() {
            while (true && isRunning) {
                try {
                    Image image = webcam.getImage();
                    imageHolder.setIcon(new ImageIcon(image));

                    FastRGB fastRGB = new FastRGB((BufferedImage) image);
                    String[] rows = new String[height];
                    for (int j = 0; j < height; j++) {
                        String row = "";
                        for (int i = 0; i < width; i++) {
                            short[] rgb = fastRGB.getRGB(i, j);
                            short avg = (short) ((rgb[0] + rgb[1] + rgb[2]) / 3);
                            row = row + mapToChar(avg);
                        }
                        rows[j] = row;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String row : rows) {
                        stringBuilder.append(row + "\n");
                    }
                    textArea.setText(stringBuilder.toString());
                    String str = textArea.getText();


                    /**+
                     * 1000 / 50 = 20 -> 20 frame per a second
                     */
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public char mapToChar(int avg) {

//        char[] density = {' ', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
//        char[] density = {' ', '.', ':', '░', '▒', '▓', '▋', '▉', '█'};
        char[] density = {'█', '▉', '▋', '▓', '▒', '░', ':', '.', ' '};
        char c = ' ';
        int range = 256 / density.length;
        return density[avg / range];
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
    }
}
