package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ClockApp extends JFrame {

    private JLabel mainClockLabel;
    private JTextField timezoneInput;
    private ClockRunnable currentClock;

    private SimpleDateFormat sdf;
    private String mainTimezone = "GMT+7"; // Mặc định là GMT+7

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClockApp clockApp = new ClockApp();
            clockApp.setVisible(true);
        });
    }

    public ClockApp() {
        setTitle("Đồng hồ chạy liên tục");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(mainTimezone));

        initUI();
    }

    private void initUI() {
        mainClockLabel = new JLabel();
        timezoneInput = new JTextField("7");

        JButton createThreadButton = new JButton("Tạo Mới");
        createThreadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createClockThread();
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(mainClockLabel);
        panel.add(timezoneInput);
        panel.add(createThreadButton);

        add(panel);

        // Bắt đầu đồng hồ chính
        Timer mainTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMainTime();
            }
        });
        mainTimer.start();
    }

    private void createClockThread() {
        try {
            int offset = Integer.parseInt(timezoneInput.getText());
            String timezone = "GMT" + (offset >= 0 ? "+" : "") + offset;

            SwingUtilities.invokeLater(() -> {
                ClockRunnable clockRunnable = new ClockRunnable(timezone);
                currentClock = clockRunnable;
                clockRunnable.start();
            });

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập một số nguyên cho múi giờ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ClockRunnable extends Thread {
        private String threadTimezone;
        private JFrame threadFrame;
        private JLabel threadClockLabel;
        private JTextField threadTimezoneInput;

        public ClockRunnable(String timezone) {
            this.threadTimezone = timezone;
            createUI();
        }

        @Override
        public void run() {
            SwingUtilities.invokeLater(() -> {
                Timer threadTimer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateNewTime();
                    }
                });
                threadTimer.start();

                // Set the location of the new frame relative to the main window
                threadFrame.setLocationRelativeTo(null);
                threadFrame.setVisible(true);
            });
        }

        private void createUI() {
            SwingUtilities.invokeLater(() -> {
                threadFrame = new JFrame("Đồng hồ chạy liên tục - " + threadTimezone);
                threadFrame.setSize(300, 150);
                threadFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                threadClockLabel = new JLabel();
                updateNewTime();

                threadTimezoneInput = new JTextField(threadTimezone);

                JButton createNewClockButton = new JButton("Tạo");
                createNewClockButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        createClockThread();
                    }
                });

                JPanel threadPanel = new JPanel(new GridLayout(3, 1));
                threadPanel.add(threadClockLabel);
                threadPanel.add(threadTimezoneInput);
                threadPanel.add(createNewClockButton);

                threadFrame.add(threadPanel);

                Timer threadTimer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateNewTime();
                    }
                });
                threadTimer.start();
            });
        }

        private void updateNewTime() {
            SwingUtilities.invokeLater(() -> {
                SimpleDateFormat threadSdf = new SimpleDateFormat("HH:mm:ss");
                threadSdf.setTimeZone(TimeZone.getTimeZone(threadTimezone));

                Date now = new Date();
                String currentTime = threadSdf.format(now);
                threadClockLabel.setText(currentTime);
            });
        }
    }

    private void updateMainTime() {
        Date now = new Date();
        String currentTime = sdf.format(now);

        // Sử dụng invokeLater để đảm bảo thay đổi xảy ra trên EDT
        SwingUtilities.invokeLater(() -> mainClockLabel.setText(currentTime));
    }
}
