package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Player extends GameObject {

    GameManager handler;
    Game game;

    private final BufferedImage player_img;

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

        if (handler.isUp()) velY = -5;
        else if (!handler.isDown()) velY = 0;

        if (handler.isDown()) velY = 5;
        else if (!handler.isUp()) velY = 0;

        if (handler.isRight()) velX = 5;
        else if (!handler.isLeft()) velX = 0;

        if (handler.isLeft()) velX = -5;
        else if (!handler.isRight()) velX = 0;

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(player_img,(int)x, (int)y, null);
        /*Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);
        g2d.draw(getBounds());*/

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

            if(tempobject.getId() == ID.Enemy) {
                if (getBounds().intersects(tempobject.getBounds())) {
                    if (game.hp > 1) {
                        game.hp = game.hp - 1;
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
