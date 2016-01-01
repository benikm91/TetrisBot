package gamebot.qlearning;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Tetris game is from: TODO
 */
public class Tetris extends JPanel {

    private static final long serialVersionUID = -8715353373678321308L;

    private final Point[][][] Tetraminos = {
            // I-Piece
            {
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) }
            },

            // J-Piece
            {
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0) }
            },

            // L-Piece
            {
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0) }
            },

            // O-Piece
            {
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) }
            },

            // S-Piece
            {
                    { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                    { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) }
            },

            // T-Piece
            {
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) }
            },

            // Z-Piece
            {
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) },
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) }
            }
    };

    private final Color[] tetraminoColors = {
            Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
    };

    private final static Color EMPTY_COLOR = Color.BLACK;
    private final int height = 24;

    private Point pieceOrigin;
    private int currentPiece;
    private int rotation;
    private ArrayList<Integer> nextPieces = new ArrayList<Integer>();

    private long score;
    private Color[][] well;
    private boolean paused = false;

    // Creates a border around the well and initializes the dropping piece
    private void init() {
        well = new Color[12][height];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < height - 1; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = EMPTY_COLOR;
                }
            }
        }
        newPiece();
    }

    // Put a new, random piece into the dropping position
    public void newPiece() {
        pieceOrigin = new Point(5, 2);
        rotation = 0;
        if (nextPieces.isEmpty()) {
            Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(nextPieces);
        }
        currentPiece = nextPieces.get(0);
        nextPieces.remove(0);
    }

    // Collision test for the dropping piece
    private boolean collidesAt(int x, int y, int rotation) {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != EMPTY_COLOR) {
                return true;
            }
        }
        return false;
    }

    // Rotate the piece clockwise or counterclockwise
    public void rotate(int i) {
        if (paused) return;
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
            rotation = newRotation;
        }
        repaint();
    }

    // Move the piece left or right
    public void move(int i) {
        if (paused) return;
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
        }
        repaint();
    }

    /**
     * Drops the piece one line or fixes it to the well if it can't drop
     * @return True if piece hit something, false otherwise.
     */
    public boolean dropDown() {
        if (paused) return false;
        boolean hitSomething;
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
            hitSomething = false;
        } else {
            fixToWell();
            hitSomething = true;
        }
        repaint();
        return hitSomething;
    }

    // Make the dropping piece part of the well, so it is available for
    // collision detection.
    public void fixToWell() {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
        }
        clearRows();
        newPiece();
    }

    public void deleteRow(int row) {
        for (int j = row-1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j+1] = well[i][j];
            }
        }
    }

    // Clear completed rows from the field and award score according to
    // the number of simultaneously cleared rows.
    public void clearRows() {
        boolean gap;
        int numClears = 0;

        for (int j = 21; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == EMPTY_COLOR) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(j);
                j += 1;
                numClears += 1;
            }
        }

        switch (numClears) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }
    }

    // Draw the falling piece
    private void drawPiece(Graphics g) {
        g.setColor(tetraminoColors[currentPiece]);
        for (Point p : Tetraminos[currentPiece][rotation]) {
            g.fillRect((p.x + pieceOrigin.x) * 26,
                    (p.y + pieceOrigin.y) * 26,
                    25, 25);
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        // Paint the well
        g.fillRect(0, 0, 26*12, 26*23);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < height - 1; j++) {
                g.setColor(well[i][j]);
                g.fillRect(26*i, 26*j, 25, 25);
            }
        }

        // Display the score
        g.setColor(Color.WHITE);
        g.drawString("" + score, 19*12, 25);

        // Draw the currently falling piece
        drawPiece(g);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Tetris");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(12*26+10, 26*23+25);
        f.setVisible(true);

        final Tetris game = new Tetris();
        game.init();
        f.add(game);

        // Keyboard controls
        f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        game.rotate(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        game.rotate(+1);
                        break;
                    case KeyEvent.VK_LEFT:
                        game.move(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.move(+1);
                        break;
                    case KeyEvent.VK_SPACE:
                        while (!game.dropDown());
                        game.score += 1;
                        break;
                    case KeyEvent.VK_1:
                    case KeyEvent.VK_2:
                    case KeyEvent.VK_3:
                    case KeyEvent.VK_4:
                    case KeyEvent.VK_5:
                    case KeyEvent.VK_6:
                    case KeyEvent.VK_7:
                    case KeyEvent.VK_8:
                    case KeyEvent.VK_9:
                        game.moveAndRotatePiece(e.getKeyCode() - KeyEvent.VK_0, e.getKeyCode() - KeyEvent.VK_0);
                        break;
                    case KeyEvent.VK_P:
                        game.paused = !game.paused;
                        System.out.println(Arrays.toString(game.getCurrentStates()));
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        // Make the falling piece drop every second
        new Thread() {
            @Override public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        game.dropDown();
                    } catch ( InterruptedException e ) {}
                }
            }
        }.start();
    }

    /**
     * Executes following methods in given order:
     * <ul>
     *     <li>{@link #requestMovePiece(int)}
     *     <li>{@link #requestRotatePiece(int)}
     *     <li>{@link #requestDropPiece()}
     * </ul>
     * @param newX {@link #requestMovePiece(int)}
     * @param newRotation {@link #requestRotatePiece(int)}
     */
    public void moveAndRotatePiece(int newX, int newRotation) {
        requestMovePiece(newX);
        requestRotatePiece(newRotation);
        requestDropPiece();
    }

    /**
     * Simulate a drop piece action.
     */
    public void requestDropPiece() {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.keyRelease(KeyEvent.VK_SPACE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Simulate as many key presses as needed to reach desired x coordinate.
     * @param newX Desired x coordinate.
     */
    public void requestMovePiece(int newX) {
        try {
            Robot robot = new Robot();
            int fieldsToGo = Math.abs(newX - this.pieceOrigin.x);
            int keyDirection = (newX > this.pieceOrigin.x) ? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT;
            for (int i = 0; i < fieldsToGo; i++) {
                robot.keyPress(keyDirection);
                robot.keyRelease(keyDirection);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Simulate as many key presses as needed to reach desired rotation.
     * @param newRotation Desired rotation.
     */
    public void requestRotatePiece(int newRotation) {
        try {
            Robot robot = new Robot();
            int fieldsToGo = Math.abs(newRotation - this.rotation);
            int keyDirection = (newRotation > this.rotation) ? KeyEvent.VK_DOWN : KeyEvent.VK_UP;
            for (int i = 0; i < fieldsToGo; i++) {
                robot.keyPress(keyDirection);
                robot.keyRelease(keyDirection);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The y coordinate of the lowest empty square counting from bottom to top.
     */
    protected int getCurrentMinDepth() {
        return IntStream.rangeClosed(1, 10).map(this::getDepthAt).min().getAsInt();
    }

    /**
     * @param fieldIndex The index in the play field. 0 represents the first column from the play field.
     * @return The y coordinate of the first empty square in the column fieldIndex counting from bottom to top.
    */
    protected int getDepthAt(int fieldIndex) {
        assert 0 <= fieldIndex && fieldIndex <= 10 : "field index is out of range";
        int wellIndex = fieldIndex + 1; // ignore left wall.
        for (int i = 0; i < height; i++) {
            if (well[wellIndex][height - 1 - i] == EMPTY_COLOR) return i;
        }
        return height;
    }

    /**
     * @param fieldIndex The x coordinate on the play field.
     * @param currentMinDepth The y coordinate of the lowest empty square on the play field.
     * @return The relative y coordinate of the first empty square in the column fieldIndex counting from bottom to top.
     */
    protected int getRelativeDepth(int fieldIndex, int currentMinDepth) {
        return getDepthAt(fieldIndex) - currentMinDepth;
    }

    /**
     * @return All states.
     */
    public TetrisState[] getCurrentStates() {
        TetrisState[] states = new TetrisState[7];
        int minDepth = getCurrentMinDepth();
        System.out.println(minDepth);
        for (int i = 0; i < states.length; i++) {
            states[i] = new TetrisState(
                            getRelativeDepth(i, minDepth),
                            getRelativeDepth(i + 1, minDepth),
                            getRelativeDepth(i + 2, minDepth),
                            getRelativeDepth(i + 3, minDepth),
                            this.currentPiece,
                            i);
        }
        return states;
    }
}