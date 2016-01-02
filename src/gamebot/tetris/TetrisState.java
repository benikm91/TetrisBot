package gamebot.tetris;

import java.util.Arrays;
import java.util.List;

/**
 * Immutable state.
 * It has four heights which are relevant for this state (to reduce the Tetris state space drastically).
 */
public final class TetrisState {

    private static final int maxHeightDiff = 7;

    /** Offset of this state from play field beginning. */
    public final int offset;
    /** Height of fields after offset*/
    public final int firstHeight;
    public final int secondHeight;
    public final int thirdHeight;
    public final int fourthHeight;
    /** Index of Tetris blockID. */
    public final int blockID;

    public TetrisState(int fih, int sh, int th, int fh, int block, int offset) {
        // The different must not differ more then maxHeightDiff.
        int max = Arrays.asList(fih, sh, th, fh).stream().max(Integer::compareTo).get();
        int min = max - maxHeightDiff;
        this.firstHeight = Math.max(fih, min);
        this.secondHeight = Math.max(sh, min);
        this.thirdHeight = Math.max(th, min);
        this.fourthHeight = Math.max(fh, min);
        this.blockID = block;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "(first=" + firstHeight + "|second=" + secondHeight + "|third=" + thirdHeight + "|fourth=" + fourthHeight
                + "|blockID=" + blockID + "|offset=" + offset + ")";
    }

}
