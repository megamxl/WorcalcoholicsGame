package Woralcoholics.game;

public class Score {
    private static int score;


    public Score(int score) {
        this.score = score;
    }
    public void addScore(int add){
        this.score += add;
    }

    public int showScore(){
        return this.score ;
    }
}
