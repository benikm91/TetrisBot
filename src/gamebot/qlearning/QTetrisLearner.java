package gamebot.qlearning;

import gamebot.tetris.TetrisState;

/**
 * Created by P on 01.01.2016.
 */
public class QTetrisLearner {
    private final int offset;

    public QTetrisLearner(int offset){
        this.offset = offset;
    }


    /////////////////////////////////////////////////////////
    //Q array Indexing stuff
    private final int nBlockIDs = 7;
    private final int nHeights = 7;
    private final int nRotations = 4;
    private final int nPositions = 4;
    private final int rotBase = 1;
    private final int posBase = nRotations * rotBase;
    private final int blockBase = nPositions* posBase;
    private final int fourthBase = nBlockIDs * blockBase;
    private final int thirdBase = nHeights * fourthBase;
    private final int secondBase = nHeights * thirdBase;
    private final int firstBase = nHeights * secondBase;
    private final int qSize =((int)Math.pow(nHeights,4))*nBlockIDs*nPositions*nRotations;

    public int getIndex(int firstHeight, int secHeight, int thiHeight, int fouHeight, int blockID, int pos, int rot) {
        int index = firstHeight * firstBase + secHeight * secondBase + thiHeight * thirdBase + fouHeight * fourthBase + blockID * blockBase + pos * posBase + rot * rotBase;
        assert index < qSize && index >= 0;
        return index;
    }

    public int getIndex(TetrisState s, Action a) {
        return getIndex(s.firstHeight, s.secondHeight, s.thirdHeight, s.fourthHeight, s.blockID, a.pos, a.rot);

    }

    public int getPosStartIndex(TetrisState s) {
        return getPosStartIndex(s, s.blockID);
    }

    public int getPosStartIndex(TetrisState s, int blockID) {
        return getIndex(s.firstHeight, s.secondHeight, s.thirdHeight, s.fourthHeight, blockID, 0, 0);
    }

    public int getPosEndIndex(TetrisState s) {
        return getPosEndIndex(s, s.blockID);
    }

    /**
     * inclusive
     * @param s
     * @param blockID
     * @return
     */
    public int getPosEndIndex(TetrisState s, int blockID) {
        return getIndex(s.firstHeight, s.secondHeight, s.thirdHeight, s.fourthHeight, blockID, 3, 3);
    }

    /////////////////////////////////////////////////////////
    // Q iteration algorithm

    /**
     * image like multidimensional array
     * [height][height][height][height][blockID][position][rotation]
     *
     * height is in range [0..nHeights[
     * blockID is in range [0..nBlockids[
     * position is in range [0..nPositions[
     * rotation is in range [0..nRotations[
     * keep in mind some rotations are obsolete for certain blocks eg. box only has one
     */
    private final float[] Q = new float[qSize];


    private final float learningRate = 1;
    private final float discountFactor = 1;
    public void updateQ(TetrisState s, Action a, TetrisState post_s, float reward) {
        int index = getIndex(s, a);

        float knownValueOfLastAction = Q[index];
        float estimatedValue = learningRate * (reward + maxValueOfFutureAction(post_s) - knownValueOfLastAction);
        //Qt+1 = Qt + learnedvalue - oldvalue
        Q[index] += estimatedValue;
    }
    /**
     * return the average of the best actions for each of the blocks that might appear in the future
     *
     * @param post_s
     * @return
     */
    private float maxValueOfFutureAction(TetrisState post_s) {
        float average = 0;

        for (int blockID = 0; blockID <= nBlockIDs; blockID++) {
            float highest = Float.NEGATIVE_INFINITY;
            for (int i = getPosStartIndex(post_s, blockID); i <= getPosEndIndex(post_s,blockID); i++) {
                if (Float.compare(Q[i], highest) > 0) {
                    highest = Q[i];
                }
            }
            average += highest;
        }
        return average / nBlockIDs;
    }
    public Action getAction(TetrisState s) {
        final int startIndex = getPosStartIndex(s);
        float highest = Float.NEGATIVE_INFINITY;
        int pos = 0, rot = 0;

        for (int i = startIndex; i <= getPosEndIndex(s); i++) {
            if (Float.compare(Q[i], highest) > 0) {
                highest = Q[i];
                pos = (i - startIndex) / 4;
                rot = (i - startIndex) % 4;
            }
        }
        return new Action(pos, rot, highest);
    }

    /////////////////////////////////////////////////////////
    // debugging stuffs

    public static void main(String[] args) {
        QTetrisLearner debug = new QTetrisLearner(0);

        System.out.println(debug.getIndex(6, 6, 6, 6, 6, 3, 3));
        System.out.println(268912 ==debug.qSize);
    }

    public int getOffset() {
        return offset;
    }
}
