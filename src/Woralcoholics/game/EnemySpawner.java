package Woralcoholics.game;

import java.awt.image.BufferedImage;
import java.util.Random;

public class EnemySpawner {

    private final BufferedImage img;

    GameManager handler;
    Animations spritesheet;

    int x;
    int y;


    static Random r = new Random();

    public EnemySpawner(GameManager handler, Animations spritesheet) {
        this.handler = handler;

        img = spritesheet.getImage(1, 4, 32, 32);
    }

    public void Spawner(int Wavesize){
        for(int i = 0; i < Wavesize+1; i++){
            System.out.println("hier");
             x = r.nextInt((63*32-1)-1);
             y = r.nextInt((63*32-1)-1);

             handler.addObject(new Enemy(32,32,ID.Enemy,handler,spritesheet));

        }

    }


}
