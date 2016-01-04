package gamebot.tetris;

import java.awt.*;

/**
 * Enum with all pieces of the Tetris game.
 */
public enum Piece {

    I(
        Color.cyan,
        new Point[][]{
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
        }
    ),

    J(
        Color.blue,
        new Point[][]{
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)}
        }
    ),

    L(
        Color.orange,
        new Point[][]{
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)}
        }
    ),

    O(
        Color.yellow,
        new Point[][]{
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}
        }
    ),

    S(
        Color.green,
        new Point[][]{
            {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
            {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
        }
    ),

    T(
        Color.pink,
        new Point[][]{
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2)}
        }
    ),

    Z(
        Color.red,
        new Point[][]{
            {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)},
            {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)}
        }
    );

    /** Rotations of the pievce. */
    private final Point[][] rotations;

    /** Color of the piece. */
    public final Color color;

    /**
     * @param color Field value.
     * @param rotations Field value.
     */
    Piece(final Color color, final Point[][] rotations) {
        this.color = color;
        this.rotations = rotations;
    }

    /**
     * @param rotation Rotation index.
     * @return The relative points for the given rotation.
     */
    public Point[] getRotation(final int rotation) {
        return this.rotations[rotation];
    }

    /**
     * @return {@link #values()}
     */
    public static Piece[] getPieces() {
        return Piece.values();
    }

    /**
     * @return Number of pieces which can appear.
     */
    public static int getNumOfPieces() {
        return getPieces().length;
    }

    /**
     * @return A random piece of the piece pool.
     */
    public static Piece getRandom() {
        return getPieces()[(int) (Math.random() * getNumOfPieces())];
    }

}
