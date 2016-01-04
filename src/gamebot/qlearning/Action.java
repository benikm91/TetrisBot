package gamebot.qlearning;

import gamebot.tetris.TetrisState;

/**
 * Created by P on 01.01.2016.
 */
public class Action {
    /** State before the action. */
    public final TetrisState state;
    /** Piece position in state. */
    public final int pos;
    /** Piece rotation. */
    public final int rot;
    /** Score given to this action from Q-learning. */
    public final float score;

    /**
     *
     * @param state .
     * @param position range [0..3]
     * @param rotation range [0..3]
     */
    public Action(final TetrisState state, final int position, final int rotation, final float score){
        assert 0 <= position && position <= 3;
        assert 0 <= rotation && rotation <= 3;

        this.state = state;
        this.pos = position;
        this.rot = rotation;
        this.score = score;
    }

    @Override
    public String toString() {
        return String.format("(pos=%d;rot=%d;score=%f", pos, rot, score);
    }

}
