package task3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel {
    private final JLabel timer_label;
    private final Timer timer;
    private int elapsed_time; // in-game time in secs

    public TimerPanel() {
        timer_label = new JLabel("Time: 0s");
        setLayout(new BorderLayout());

        add(timer_label, BorderLayout.CENTER);

        elapsed_time = 0;

        // start timer when panel is created
        // when any action is performed in game, the timer is updated
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsed_time++;
                timer_label.setText("Time: " + elapsed_time + "s");
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
        elapsed_time = 0;
        timer_label.setText("Time: 0s");
    }

    public int GetElapsedTime() {
        return elapsed_time;
    }
}
