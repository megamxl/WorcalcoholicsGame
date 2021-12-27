package Woralcoholics.game;

public class Score {
    private static int score;


    public Score(int score) {
        this.score = score;
    }
    public static void addScore(int add){
        Score.score += add;
    }

    public static int showScore(){
        return Score.score ;
    }
}
