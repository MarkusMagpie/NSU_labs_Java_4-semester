package task3;

import java.awt.*;
import java.util.Random;

public class TetroMino {
    private final Point[] coordinates;
    private final Color color;

    public TetroMino(Point[] coordinates, Color color) {
        this.coordinates = coordinates;
        this.color = color;
    }

    public void rotate() {
        Point center = coordinates[1];
        for (Point coordinate : coordinates) {
            // 2d matrix 90 degree counterclockwise rotation matrix
            // | 0 -1| => x = -y
            // | 1  0| => y = x
            int x = coordinate.x - center.x; // offset from center
            int y = coordinate.y - center.y;
            coordinate.x = center.x - y;
            coordinate.y = center.y + x;
        }
    }

    public static TetroMino getRandomTetroMino(Random random) {
        int rand = random.nextInt(7);
        return switch (rand) {
            case 0 -> new TetroMino(new Point[]{
                new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)
            }, Color.CYAN); // createI
            case 1 -> new TetroMino(new Point[]{
                new Point(1, 0), new Point(2, 0), new Point(1, 1), new Point(2, 1)
            }, Color.YELLOW); // createO
            case 2 -> new TetroMino(new Point[]{
                new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)
            }, Color.MAGENTA); // createT
            case 3 -> new TetroMino(new Point[]{
                new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)
            }, Color.GREEN); // createS
            case 4 -> new TetroMino(new Point[]{
                new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)
            }, Color.RED); // createZ
            case 5 -> new TetroMino(new Point[]{
                new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)
            }, Color.BLUE); // createJ
            case 6 -> new TetroMino(new Point[]{
                new Point(2, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)
            }, Color.ORANGE); // createL
            default -> throw new IllegalArgumentException("Unexpected value: " + rand);
        };
    }

//    public static TetroMino createI() {
//        return new TetroMino(new Point[]{
//                new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)
//        }, Color.CYAN);
//    }

    public Point[] getCoordinates() {
        return coordinates;
    }

    public Color getColor() {
        return color;
    }
}
