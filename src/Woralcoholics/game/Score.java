package Woralcoholics.game;

public class Score {
    private static int score;


    public Score(int score) {
        this.score = score;
    }
    public void addScore(int add){
        this.score += add;
    }
    public void resetSore(){this.score = 0;}
    public int showScore(){
        return this.score ;
    }
}
