package Woralcoholics.game;

public class Score {
    private static int score;

    /**
     * set the score to specific value
     * @param score
     */
    public Score(int score) {
        this.score = score;
    }

    /**
     * add the give int
     * @param add
     */
    public void addScore(int add){
        this.score += add;
    }

    /**
     * reset the current sore
     */
    public void resetSore(){this.score = 0;}

    /**
     * get the current sore
     */
    public int showScore(){
        return this.score ;
    }
}