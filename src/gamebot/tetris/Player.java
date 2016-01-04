package gamebot.tetris;

/**
 * Created by Benjamin on 02.01.2016.
 */
public interface Player {

    /**
     * Do an action with a given state.
     * @param game Game instance.
     * @param states Current states.
     */
    void doAction(final Tetris game, final TetrisState[] states);

    /**
     * A game instance has ended.
     */
    void gameOver();

    /**
     * Shut down application. No new game will get started.
     */
    void endGame();

    /** TODO BETTER */
    void changeRandomness(float delta);

}
