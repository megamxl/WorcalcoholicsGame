package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends GameObject {

    GameManager handler;
    Game game;

    private final BufferedImage player_img;

    private final double invincibleTime = 1000;
    private double wait;

    private float diagonalMultiplier = 1;
    private Boolean movingVertical = false;
    private Boolean movingHorizontal = false;

    public Player(int x, int y, ID id, GameManager GameManager, Game game, Animations an) {
        super(x, y, id, an);
        this.handler = GameManager;
        this.game = game;

        player_img = an.getImage(1,3,64,64);

    }

    @Override
    public void update() {

        x += velX;
        y += velY;

        collision();

        if(movingVertical && movingHorizontal) {
            diagonalMultiplier = 0.75f;
        }
        else {
            diagonalMultiplier = 1;
        }

        // Vertical Movement
        if (handler.isUp() && !handler.isDown()) {
            velY = -5 * diagonalMultiplier;
            movingVertical = true;
        }
        if (handler.isDown() && !handler.isUp()) {
            velY = 5 * diagonalMultiplier;
            movingVertical = true;
        }
        if ((handler.isUp() && handler.isDown()) || !handler.isUp() && !handler.isDown()) {
            velY = 0;
            movingVertical = false;
        }
        // Horizontal Movement
        if (handler.isRight() && !handler.isLeft()) {
            velX = 5 * diagonalMultiplier;
            movingHorizontal = true;
        }
        if (handler.isLeft() && !handler.isRight()) {
            velX = -5 * diagonalMultiplier;
            movingHorizontal = true;
        }
        if ((handler.isLeft() && handler.isRight()) || (!handler.isLeft() && !handler.isRight())) {
            velX = 0;
            movingHorizontal = false;
        }
    }

    @Override
    public void render(Graphics g) {
      /*  Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);
        g2d.draw(getBounds());*/
        g.drawImage(player_img,(int)x, (int)y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x+12,(int)y, 40, 62);
    }

    private void collision() {
        for (int i = 0; i < handler.object.size(); i++) {

            GameObject tempobject = handler.object.get(i);

            if (tempobject.getId() == ID.Block) {
                if (getBounds().intersects(tempobject.getBounds())) {
                    // IMPLEMENT DIAGONAL MOVEMENT ON WALL
                    x += velX * -1;
                    y += velY * -1;
                }
            }
            if(tempobject.getId() == ID.Create) {
                if (getBounds().intersects(tempobject.getBounds())) {
                    game.ammo += 20;
                    handler.removeObject(tempobject);
                }
            }

            if(tempobject.getId() == ID.Enemy || tempobject.getId() == ID.EnemyBullet) {    //If Player is hit by Enemy of EnemyBullet
                ID temp = tempobject.getId();
                if (getBounds().intersects(tempobject.getBounds())) {
                    double now = System.currentTimeMillis();
                    if (game.hp > 1 && now > wait) {
                        switch (temp) {
                            case EnemyBullet -> {
                                handler.removeObject(tempobject);   //Remove Enemy Bullet if Player is hit
                                game.hp -= 20;
                            }
                            case Enemy -> game.hp -= 10;
                        }
                        wait = now + invincibleTime;
                    }
                    if (game.hp == 1) {
                        game.hp = game.hp - 1;
                        System.out.println("Game Over!");
                    }
                    if(game.hp == 0){
                        game.hp = 0;
                    }

                }

            }
        }
    }
}
