package gamebot.qlearning;

/**
 * Created by P on 01.01.2016.
 */
public class Action {
    final int pos;
    final int rot;
    final float score;

    /**
     *
     * @param position range [0..3]
     * @param rotation range [0..3]
     */
    public Action(int position,int rotation, float score){
        assert position <= 3 && position >= 0;
        assert rotation <= 3 && rotation >= 0;

        this.pos = position;
        this.rot = rotation;
        this.score = score;
    }


}
