package gamebot.tetris;

import gamebot.qlearning.QTetrisLearner;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Tetris game is from: TODO
 */
public class Tetris extends JPanel {

    private long highScore = 0;

    private long wait = 0;

    private boolean endGame = false;

    private static final long serialVersionUID = -8715353373678321308L;

    /** Color which represents an empty field. */
    private static final Color EMPTY_COLOR = Color.BLACK;

    /** Size of a Tetris square. */
    private final int squareSize = 20;
    /** Size of square with its border. */
    private final int squareSizeWithBorder = squareSize + 1;

    /** Well width. */
    private final int width = 12;
    /** Well height. */
    private final int height = 24;
    /** Well is the play field and the walls as well as the ground. */
    private Color[][] well;

    /** Location in {@link #well} of {@link #currentPiece}. */
    private Point pieceOrigin;
    /** Current piece falling down. */
    private Piece currentPiece;
    /** Rotation of {@link #currentPiece}. */
    private int rotation;

    /** Current game score. */
    private long score;
    /** True => game is paused. False otherwise. */
    private boolean paused = false;

    private Player bot;

    public Tetris() {
        this(null);
    }

    /**
     * Init well and first piece.
     */
    public Tetris(final Player bot) {
        this.bot = bot;
        restartGame();
    }

    public synchronized void restartGame() {
        initWell();
        score = 0;
        rotation = 0;
        currentPiece = null;
        pieceOrigin = null;
        paused = false;
        newPiece();
    }

    public void initWell() {
        this.well = new Color[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height - 1; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = EMPTY_COLOR;
                }
            }
        }
    }

    /**
     * Put a new, random piece into the dropping position
     */
    public final void newPiece() {
        pieceOrigin = new Point(5, 2);
        rotation = 0;
        currentPiece = Piece.getRandom();
        // if piece collides => game over
        if (this.collidesAt(pieceOrigin.x, pieceOrigin.y, rotation)) {
            gameOver();
        }
    }

    /**
     * Collision test for the dropping piece
     * @param x Position to check
     * @param y Position to check
     * @param rotation Rotation of current piece. TODO why not use this.rotation?
     * @return True => If currentPiece collides with well. False otherwise.
     */
    private boolean collidesAt(int x, int y, int rotation) {
        assert x >= 0;
        assert y >= 0;
        for (Point p : currentPiece.getRotation(rotation)) {
             if (well[p.x + x][p.y + y] != EMPTY_COLOR) {
                  return true;
             }
        }
        return false;
    }

    /**
     * Rotate the currentPiece, if it doesn't collide with well afterwards.
     * @param i How many times to rotate. Clockwise (positiv) or counterclockwise (negativ).
       */
    public final void rotate(final int i) {
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

    public void gameOver() {
        if (highScore < score) {
            highScore = score;
              System.out.println( "New Highscore: " + score);
        }
        if (this.bot != null) bot.gameOver();
        restartGame();
     }

    // Mo ve the piece left or right
    public synchronized final void move(final int i) {
        if (paused) return;
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
                 pieceOrigin.x += i;
        }
        repaint ();
    }

    /**
     * Drops the piece one line or fixes it to the well if it can't drop
     * @return True if piece hit something, false otherwise.
     */
    public synchronized final boolean dropDown() {
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

    /**
     * Make the dropping piece part of the well, so it is available for
     * collision detection.
     */
    public final void fixToWell() {
        for (Point p : currentPiece.getRotation(rotation)) {
            well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = currentPiece.color;
        }
        clearRows();
        newPiece();
    }

    /**
     * Delete row and move all above rows down.
     * @param row Row to delete.
     */
    public final void deleteRow(final int row) {
        for (int j = row - 1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j+1] = well[i][j];
            }
        }
    }

    /**
     * Clear completed rows from the field and award score according to
     * the number of simultaneously cleared rows.
     */
    public final void clearRows() {
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

    /**
     * Draw the falling piece.
     * @param g Graphics to draw.
     */
    private void drawPiece(final Graphics g) {
        g.setColor(currentPiece.color);
        for (Point p : currentPiece.getRotation(rotation)) {
            g.fillRect((p.x + pieceOrigin.x) * squareSizeWithBorder,
                    (p.y + pieceOrigin.y) * squareSizeWithBorder,
                    squareSize, squareSize);
        }
        g.setColor(Color.red);
        g.fillRect((pieceOrigin.x) * squareSizeWithBorder,
                (pieceOrigin.y) * squareSizeWithBorder,
                squareSize, squareSize);
    }

    @Override
    public synchronized void paintComponent(final Graphics g) {
        // Paint the well
        g.fillRect(0,  0, squareSizeWithBorder * width, squareSizeWithBorder * height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height - 1; j++) {
                g.setColor(well[i][j]);
                g.fillRect(squareSizeWithBorder * i, squareSizeWithBorder * j, squareSize, squareSize);
            }
        }

        // Display the score
        g.setColor(Color.WHITE);
        g.drawString("" + score, 19*12, 25);

        // Draw the currently falling piece
        drawPiece(g);
    }

    /**
     * Main.
     * @param args Main args.
     */
    public static void main(final String[] args) throws InterruptedException {
        final Tetris game = new Tetris(new BotLearningPlayer(new QTetrisLearner()));
        final JFrame f = new JFrame("Tetris");

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(game.getFullWidth(), game.getFullHeight());
        f.setVisible(true);

        f.add(game);

        // Keyboard controls
        f.addKeyListener(new KeyListener() {
            public void keyTyped(final KeyEvent e) {
            }

            public void keyPressed(final KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_N:
                        game.wait -= 1000;
                        break;
                    case KeyEvent.VK_M:
                        game.wait += 1000;
                        break;
                    case KeyEvent.VK_R:
                        game.wait = 0;
                        break;
                    case KeyEvent.VK_Q:
                        game.bot.changeRandomness(-0.1f);
                        break;
                    case KeyEvent.VK_W:
                        game.bot.changeRandomness(+0.1f);
                        break;
                    case KeyEvent.VK_S:
                        game.bot.endGame();
                        game.endGame = true;
                        break;
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
                        game.requestMoveAndRotatePiece(e.getKeyCode() - KeyEvent.VK_0, e.getKeyCode() - KeyEvent.VK_0);
                        break;
                    case KeyEvent.VK_P:
                        game.paused = !game.paused;
                        System.out.println(game.getNumOfBlocked());
                        System.out.println(Arrays.toString(game.getCurrentStates()));
                        break;
                }
            }

            public void keyReleased(final KeyEvent e) {
            }
        });

        // Make the falling piece drop every second
        new Thread() {
            @Override public void run() {
                while (!game.endGame) {
                    try {
                        Thread.sleep(1000);
                        game.dropDown();
                    } catch ( InterruptedException e ) {}
                }
            }
        }.start();

        while (!game.endGame) {
            if (game.bot != null) {
                if (game.wait > 0) Thread.sleep((long) game.wait);
                game.bot.doAction(game, game.getCurrentStates());
            }
        }

    }

    /**
     * @return Full width in pixel
     */
    public int getFullWidth() {
        return width * squareSizeWithBorder + 10;
    }

    /**
     *
     * @return Full height in pixel
     */
    public final int getFullHeight() {
        return height * squareSizeWithBorder + 25;
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
    public final void requestMoveAndRotatePiece(int newX, int newRotation) {
        requestMovePiece(newX);
        requestRotatePiece(newRotation);
        requestDropPiece();
    }

    /**
     * Simulate a drop piece action.
     */
    public final void requestDropPiece() {
        while (!this.dropDown());
        this.score += 1;
    }

    /**
     * Simulate as many key presses as needed to reach desired x coordinate.
     * @param newX Desired x coordinate.
     */
    public void requestMovePiece(int newX) {
        int fieldsToGo = Math.abs(newX - this.pieceOrigin.x);
        int dir = (newX > this.pieceOrigin.x) ? +1 : -1;
        for (int i = 0; i < fieldsToGo; i++) {
            this.move(dir);
        }
    }

    /**
     * Simulate as many key presses as needed to reach desired rotation.
     * @param newRotation Desired rotation.
     */
    public final void requestRotatePiece(final int newRotation) {
        int fieldsToGo = Math.abs(newRotation - this.rotation);
        int dir = (newRotation > this.rotation) ? +1 : -1;
        for (int i = 0; i < fieldsToGo; i++) {
            this.rotate(dir);
        }
    }

    /**
     * @return The y coordinate of the lowest empty square counting from bottom to top.
     */
    private int getCurrentMinHeight() {
        return IntStream.rangeClosed(1, 10).map(this::getHeightAt).min().getAsInt();
    }

    /**
     * @param columnIndex The index in the play field. 0 represents the first column from the play field.
     * @return The y coordinate of the first empty square in the column columnIndex counting from bottom to top.
    */
    private int getHeightAt(int columnIndex) {
        return getRowIndexOfLastColoredField(columnIndex) + 1;
    }

    /**
     * @param columnIndex The x coordinate on the play field.
     * @param currentMinHeight The y coordinate of the lowest empty square on the play field.
     * @return The relative y coordinate of the first empty square in the column columnIndex counting from bottom to top.
     */
    private int getRelativeHeight(int columnIndex, int currentMinHeight) {
        return getHeightAt(columnIndex) - currentMinHeight;
    }

    /**
     * @return All states.
     */
    public final TetrisState[] getCurrentStates() {
        TetrisState[] states = new TetrisState[7];
        int minHeight = getCurrentMinHeight();
        for (int i = 0; i < states.length; i++) {
            states[i] = new TetrisState(
                            this.score,
                            i,
                            this.currentPiece.ordinal(),
                            this.getNumOfBlocked(),
                            getRelativeHeight(i, minHeight),
                            getRelativeHeight(i + 1, minHeight),
                            getRelativeHeight(i + 2, minHeight),
                            getRelativeHeight(i + 3, minHeight));
        }
        return states;
    }

    /**
     * @param columnIndex Column to search in.
     * @return .
     */
    private int getRowIndexOfLastColoredField(final int columnIndex) {
        assert 0 <= columnIndex && columnIndex <= width - 2 : "columnIndex is out of range of play field.";
        int wellIndex = columnIndex + 1; // ignore left wall.
        for (int i = 0; i < height; i++) {
            if (well[wellIndex][i] != EMPTY_COLOR) return height - i;
        }
        return height;
    }

    /**
     * @return Number of empty squares that have a filled square above and below itself.
     */
    public final int getNumOfBlocked() {
        int result = 0;
        for (int x = 1; x < width; x++) {
            int emptyFields = 0;
            boolean lastEmpty = false;
            for (int y = 1; y < height - 1; y++) {
                if (well[x][height - y] == EMPTY_COLOR) {
                    emptyFields++;
                    lastEmpty = true;
                } else {
                    if (lastEmpty) {
                        result += emptyFields;
                        emptyFields = 0;
                    }
                    lastEmpty = false;
                }
            }
        }
        return result;
    }
}
