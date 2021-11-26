package Woralcoholics.game;

import java.awt.image.BufferedImage;
import java.util.Random;

public class EnemySpawner {

    private final BufferedImage img;

    GameManager handler;
    Animations an;


    int x;
    int y;


    static Random r = new Random();

    public EnemySpawner(GameManager handler, BufferedImage img, Animations an) {
        this.handler = handler;

        this.img =img;

    }



}
