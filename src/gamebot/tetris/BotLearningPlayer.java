package gamebot.tetris;

import gamebot.qlearning.Action;
import gamebot.qlearning.QTetrisLearner;

/**
 * Created by Benjamin on 02.01.2016.
 */
public class BotLearningPlayer implements Player {

    /** Wait for x milliseconds before doing an action. */
    private int waitFor = 0;

    /** Q-Learning instance. */
    private QTetrisLearner qTetrisLearner;
    /** Last done action by the player. */
    private Action lastAction;

    /**
     * @param qTetrisLearner Field value.
     */
    public BotLearningPlayer(final QTetrisLearner qTetrisLearner) {
        this.qTetrisLearner = qTetrisLearner;
    }

    @Override
    public void gameOver() {
        lastAction = null;
        /*if (lastAction != null) {
            qTetrisLearner.updateQ(lastAction.state, lastAction, null);
            lastAction = null;
        }*/
    }

    @Override
    public void doAction(final Tetris game, final TetrisState[] states) {
        if (waitFor > 0) {
            try {
                Thread.sleep(waitFor);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (lastAction != null) {
            qTetrisLearner.updateQ(lastAction.state, lastAction, states[lastAction.state.offset]);
        }
        Action action = qTetrisLearner.getAction(states);
        game.requestMoveAndRotatePiece(action.state.offset + action.pos, action.rot);
        lastAction = action;
    }

    @Override
    public void endGame() {
        // store knowledge
    }

    @Override
    public void changeRandomness(float delta) {
        qTetrisLearner.randomness += delta;
        System.out.println("New randomness: " + qTetrisLearner.randomness);
    }

}
