package gamebot.qlearning;

import gamebot.tetris.TetrisState;

/**
 * Created by P on 01.01.2016.
 */
public class EnsembleTetrisLearning {
    private final int tetrisWidth = 10;
    private final int learnerWidth = 4;

    private int[] offsets = new int[tetrisWidth-learnerWidth+1];
    private QTetrisLearner[] ensemble;

    private void initOffsets(){
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] = i;
        }
    }

    public void init(){
        ensemble = new QTetrisLearner[offsets.length];
        for (int offset = 0; offset < offsets.length; offset++) {
            ensemble[offset] = new QTetrisLearner(offset);
        }
    }

    /**
     * for play mode
     * @param s gamestate before the action
     * @return the best action available
     */
    public Action getRecommendedAction(TetrisState s){
        Action best = new Action(0,0,Float.NEGATIVE_INFINITY);
        int offset = 0;

        for (QTetrisLearner qTetrisLearner : ensemble) {
            Action curr = qTetrisLearner.getAction(s);
            if(Float.compare(curr.score,best.score)>0){
                best = curr;
                offset = qTetrisLearner.getOffset();
            }
        }
        return new Action(best.pos+offset,best.rot, best.score); // :D :D
    }

}
