package task3;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {
    private JLabel score_label;
    private TetrisModel model;

    public ScorePanel(TetrisModel model) {
        this.model = model;
        score_label = new JLabel("Score: 0");
        setLayout(new BorderLayout());
        add(score_label, BorderLayout.CENTER);
    }

    public void UpdateScore() {
        score_label.setText("Score: " + model.GetScore());
    }
}
