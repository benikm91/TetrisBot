package gamebot;

/**
 * Immutable state.
 * It has four heights which are relevant for this state (to reduce the Tetris state space drastically).
 */
public final class TetrisState {

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
        this.firstHeight = fih;
        this.secondHeight = sh;
        this.thirdHeight = th;
        this.fourthHeight = fh;
        this.blockID = block;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "(first=" + firstHeight + "|second=" + secondHeight + "|third=" + thirdHeight + "|fourth=" + fourthHeight
                + "|blockID=" + blockID + "|offset=" + offset + ")";
    }

}
