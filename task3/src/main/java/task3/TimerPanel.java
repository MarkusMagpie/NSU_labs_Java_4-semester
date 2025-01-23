package task3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel {
    private JLabel timer_label;
    private Timer timer;
    private int elapsedTime; // in-game time in secs

    public TimerPanel() {
        timer_label = new JLabel("Time: 0");
        setLayout(new BorderLayout());

        add(timer_label, BorderLayout.CENTER);

        elapsedTime = 0;

        // start timer when panel is created
        // when any action is performed in game, the timer is updated
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timer_label.setText("Time: " + elapsedTime);
            }
        });
    }

    public void StartTimer() {
        timer.start();
    }

    public void StopTimer() {
        timer.stop();
    }

    public void ResetTimer() {
        elapsedTime = 0;
        timer_label.setText("Time: 0");
    }

    public int GetElapsedTime() {
        return elapsedTime;
    }
}
