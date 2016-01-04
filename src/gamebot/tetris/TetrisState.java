package gamebot.tetris;

import java.util.Arrays;

/**
 * Immutable Tetris state.
 * To reduce the state space drastically from all possible Tetris states,
 * this class only has {@link #STATE_WIDTH} many columns - instead of the full play field.
 */
public final class TetrisState {

    /** Defines how many columns are in a Tetris state. */
    public static final int STATE_WIDTH = 4;

    /** Offset of this state from play field beginning. */
    public final int offset;

    /**
     * The heights for the state.
     * A height is the number of Tetris blocks stacked up.
     * Index represents the x coordinate after the {@link #offset} and is in range [0, {@link #STATE_WIDTH}[.
     * Value represents the height on the board at this location.
     * */
    public final int[] height = new int[STATE_WIDTH];

    /** Index of Tetris blockID. */
    public final int blockID;

    /** Number of blocked squares. */
    public final int numOfBlocked;

    /** Max height before norming heights to range [ 0, 6] */
    public final int maxHeight;

    public final long score;

    /**
     * @param offset Field value.
     * @param blockID Is in range [0, 6].
     * @param numOfBlocked Field value.
     * @param height Height values.
     */
    public TetrisState(final long score, final int offset, final int blockID, final int numOfBlocked, final int... height) {
        if (!(0 <= blockID && blockID <= 6)) throw new IllegalArgumentException("Illegal blockID");
        if (height.length != STATE_WIDTH) throw new IllegalArgumentException("Height length must have state width.");
        this.score = score;
        this.offset = offset;
        this.blockID = blockID;
        this.numOfBlocked = numOfBlocked;
        this.maxHeight = Arrays.stream(height).max().getAsInt();
        for (int i = 0; i < height.length; i++) {
            // norm height.
            this.height[i] = Math.max(normHeight(height[i], maxHeight), 0);
            assert 0 <= this.height[i] && this.height[i] <= 6 : "Illegal height";
        }
    }

    public int normHeight(int height, int maxHeight) {
         if (maxHeight <= 6) return height; // nothing to norm.
        return height - maxHeight + 6;
    }

    @Override
    public String toString() {
        return "(height=" + Arrays.toString(height)
                + "|blockID=" + blockID + "|offset=" + offset + ")";
    }

    /**
     * Checks if future position and future rotation is even possible.
     * @param pos Future position.
     * @param rot Future rotation.
     * @return True, if possible. False, otherwise.
     */
    public boolean possibleValues(int pos, int rot) {
        Piece[] pieces = Piece.values();
        return Arrays.stream(pieces[blockID].getRotation(rot)).noneMatch(p -> (pos + p.x) > 4);
    }

 }