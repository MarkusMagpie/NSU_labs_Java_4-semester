package task3;

import javax.swing.*;
import java.awt.*;

// TetrisView class
// graphical display of game
// here we override paintComponent method so that we can use it to draw
//     game components + field.


public class TetrisView extends JPanel {
    private TetrisModel model;
    private final int cell_size = 30;

    public TetrisView(TetrisModel model) {
        this.model = model;
        setPreferredSize(new Dimension(cell_size * model.GetWidth(), cell_size * model.GetHeight()));
        setBackground(new Color(83, 83, 83));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        DrawBoard(g, model.GetBoard());
        DrawCurrentPiece(g, model.GetCurrentPiece());
    }

    // draw board from TetrisModel class
    // M -> V
    private void DrawBoard(Graphics g, boolean[][] board) {
        for (int y = 0; y < board[0].length; y++) {
            for (int x = 0; x < board.length; x++) {
                if (board[x][y]) {
                    g.setColor(Color.GRAY);
                    g.fillRect(x * cell_size, y * cell_size, cell_size, cell_size);
                    g.setColor(Color.BLACK);
                    g.drawRect(x * cell_size, y * cell_size, cell_size, cell_size);
                } else {
                    g.setColor(Color.BLACK);
                    g.drawRect(x * cell_size, y * cell_size, cell_size, cell_size);
                }
            }
        }
    }

    private void DrawCurrentPiece(Graphics g, TetroMino piece) {
        g.setColor(piece.getColor());
        for (Point p : piece.getCoordinates()) {
            g.fillRect(p.x * cell_size, p.y * cell_size, cell_size, cell_size);
            g.setColor(Color.BLACK);
            g.drawRect(p.x * cell_size, p.y * cell_size, cell_size, cell_size);
        }
    }

    // getter for proper grid creation
    public int GetCellSize() {
        return cell_size;
    }
}
