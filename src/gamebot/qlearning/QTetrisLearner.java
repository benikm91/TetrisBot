package gamebot.qlearning;

import gamebot.tetris.TetrisState;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by P on 01.01.2016.
 */
public class QTetrisLearner {

    /////////////////////////////////////////////////////////
    //Q array Indexing stuff
    private final int nBlockIDs = 7;
    private final int nHeights = 7;
    private final int nRotations = 4;
    private final int nPositions = 4;
    private final int rotBase = 1;
    private final int posBase = nRotations * rotBase;
    private final int blockBase = nPositions * posBase;
    private final int fourthBase = nBlockIDs * blockBase;
    private final int thirdBase = nHeights * fourthBase;
    private final int secondBase = nHeights * thirdBase;
    private final int firstBase = nHeights * secondBase;
    private final int qSize = ((int) Math.pow(nHeights, 4)) * nBlockIDs * nPositions * nRotations;

    public int getIndex(int firstHeight, int secHeight, int thiHeight, int fouHeight, int blockID, int pos, int rot) {
        assert 0 <= firstHeight && firstHeight < nHeights;
        assert 0 <= secHeight && secHeight < nHeights;
        assert 0 <= thiHeight && thiHeight < nHeights;
        assert 0 <= fouHeight && fouHeight < nHeights;
        assert 0 <= blockID && blockID < nBlockIDs;
        assert 0 <= pos && pos < nPositions;
        assert 0 <= rot && rot < nRotations;
        int index = firstHeight * firstBase + secHeight * secondBase + thiHeight * thirdBase + fouHeight * fourthBase
                    + blockID * blockBase + pos * posBase + rot * rotBase;
        assert 0 <= index && index < qSize;
        return index;
    }

    public float randomness = .2f;

    public int getIndex(TetrisState s, Action a) {
        return getIndex(s.height[0], s.height[1], s.height[2], s.height[3], s.blockID, a.pos, a.rot);

    }

    public int getPosStartIndex(TetrisState s) {
        return getPosStartIndex(s, s.blockID);
    }

    public int getPosStartIndex(TetrisState s, int blockID) {
        return getIndex(s.height[0], s.height[1], s.height[2], s.height[3], blockID, 0, 0);
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
    public  int getPosEndIndex(TetrisState s, int blockID) {
        return getIndex(s.height[0], s.height[1], s.height[2], s.height[3], blockID, 3, 3);
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

    /** Q-learning learning rate. */
    private final float learningRate = 1;

    /** Q-learning discout factor. */
    private final float discountFactor = 1;

    /**
     * @param preState State before an action.
     * @param postState State after an action.
     * @return Reward for state change. Can be positiv or negativ.
     */
    public float getReward(TetrisState preState, TetrisState postState) {
        return  - (postState.numOfBlocked - preState.numOfBlocked) * 5
                - (postState.maxHeight - preState.maxHeight) * 2;
    }

    /**
     * Gets reward by {@link #getReward(TetrisState, TetrisState)}
     * and calls {@link #updateQ(TetrisState, Action, TetrisState, float)}
     * @param preState {@see #updateQ(TetrisState, Action, TetrisState, float)}
     * @param action {@see #updateQ(TetrisState, Action, TetrisState, float)}
     * @param postState {@see #updateQ(TetrisState, Action, TetrisState, float)}
     */
    public void updateQ(TetrisState preState, Action action, TetrisState postState) {
        this.updateQ(preState, action, postState, getReward(preState, postState));
    }

    /**
     * Update knowledge Q.
     * @param preState State before action.
     * @param action Done action.
     * @param postState State after action.
     * @param reward Reward of action.
     */
    public void updateQ(TetrisState preState, Action action, TetrisState postState, float reward) {
        int index = getIndex(preState, action);
        //if (Q[index] != 0) System.out.println("I know shit");
        //Qt+1 = Qt + learnedvalue - oldvalue
        Q[index] += learningRate * (reward + discountFactor * maxValueOfFutureAction(postState) - Q[index]);
    }
    /**
     * @param state State to check actions.
     * @return The average of the best actions for each of the blocks that might appear in the future.
     */
    private float maxValueOfFutureAction(TetrisState state) {
        float futureActionSum = 0;

        for (int blockID = 0; blockID < nBlockIDs; blockID++) {
            float highest = Float.NEGATIVE_INFINITY;
            final int startIndex = getPosStartIndex(state, blockID);
            final int endIndex = getPosEndIndex(state, blockID);
            int pos, rot;
            for (int i = startIndex; i <= endIndex; i++) {
                if (Q[i] > highest) {
                    pos = (i - startIndex) / nPositions;
                    rot = (i - startIndex) % nRotations;
                    // check if values are possible in state without overshooting state space.
                    if (!state.possibleValues(pos, rot)) continue;
                    highest = Q[i];
                }
            }
            futureActionSum += highest;
        }
        return futureActionSum / nBlockIDs;
    }

    /**
     * Must not return best action.
     * @param states TetrisStates.
     * @return Action to do.
     */
    public Action getAction(TetrisState[] states) {
        // shuffle array so random gets taken if two same states.
        List<TetrisState> temp = Arrays.asList(states);
        Collections.shuffle(temp);
        states = (TetrisState[]) temp.toArray();
        if (Math.random() > randomness)  {
            // return best action
            return Arrays.stream(states).map(this::getBestAction).max((a, b) -> Float.compare(a.score, b.score)).get();
        } else {
            // return random action (explore new options)
            return Arrays.stream(states).map(this::getBestAction).max((a, b) -> new Random().nextInt(3) - 1).get();
        }
    }

    /**
     * @param state A TetrisState
     * @return Best Action for this state.
     */
    public Action getBestAction(TetrisState state) {
        final int startIndex = getPosStartIndex(state);
        final int endIndex = getPosEndIndex(state);
        assert endIndex - startIndex == nPositions * nRotations - 1;
        float highest = Float.NEGATIVE_INFINITY;
        int pos = 0, rot = 0;

        for (int i = startIndex; i <= endIndex; i++) {
            if (Q[i] > highest) {
                pos = (i - startIndex) / nPositions;
                rot = (i - startIndex) % nRotations;
                // check if values are possible in state without overshooting state space.
                if (!state.possibleValues(pos, rot)) continue;
                highest = Q[i];
            }
        }
        return new Action(state, pos, rot, highest);
    }

    /////////////////////////////////////////////////////////
    // debugging stuffs

    public static void main(String[] args) {
        QTetrisLearner debug = new QTetrisLearner();

        System.out.println(debug.getIndex(6, 6, 6, 6, 6, 3, 3));
        System.out.println(268912 == debug.qSize);
    }

}
