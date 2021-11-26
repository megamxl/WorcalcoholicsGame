package Woralcoholics.game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;


public class Enemy extends GameObject {

    private final BufferedImage enemy_img;

    //Handler is for collision detection
    private GameManager manager;
    Random r = new Random();
    int choose = 0;
    int hp = 100;
    int low = -4;
    int high = 4;
    int booleanvalue = 0;
    boolean hittedwall = false;

    static int enemysAlive = 0;
    static int waves=1;

    public Enemy(int x, int y, ID id, GameManager manager, Animations an) {
        super(x, y, id, an);
        this.manager = manager;

        enemy_img = an.getImage(1, 4, 32, 32);
        enemysAlive++;
        //System.out.println("enemy created "+ enemysAlive );
    }

    public void move() {
        x += velX;
        y += velY;
    }

    public void collision() {
        for (int i = 0; i < manager.object.size(); i++) {

            GameObject tmpObject = manager.object.get(i);

            //if it's colliding with a block
            if (tmpObject.getId() == ID.Block) {
                if (getBoundsAround().intersects(tmpObject.getBounds())) {
                    // if it is colliding with wall it goes in the opposite direction
                    x += (velX * 5) * -1;                                       // maybe improve these values(velx*5,velxy*= -1)
                    y += (velY * 5) * -1;
                    velX *= -1;
                    velY *= -1;
                    //enemy cant aim player anymore -> prevents stucking at the wall
                    hittedwall = true;

                } else if (choose == 0) {
                    //moving between 4 and -4 per iterate
                    velX = r.nextInt(high - low) + low;
                    velY = r.nextInt(high - low) + low;
                }
            }

            //if our bullet is colliding with the enemy hp get's -50
            if (tmpObject.getId() == ID.Bullet) {
                if (getBounds().intersects(tmpObject.getBounds())) {
                    System.out.println("hit");
                    hp -= 110;
                    removeWithObject(tmpObject);
                    //System.out.println("es sind " + enemysAlive +" enemys am leben");
                }
                if(enemysAlive <= 0){
                    Spawner(waves++);
                }
            }

            if (tmpObject.getId() == ID.Player) {
                /*if (getBoundsAround().intersects(tmpObject.getBounds())) {

                    System.out.println("DEAD");
                }*/
                if (hittedwall == false) {
                    if (tmpObject.getX() > x) {
                        velX = r.nextInt(4 - 0) + 0;

                    } else if (tmpObject.getX() < x) {

                        velX = r.nextInt(0 - (-4)) + (-4);

                    }
                    if (tmpObject.getY() > y) {
                        velY = r.nextInt(4 - 0) + 0;

                    } else if (tmpObject.getY() < y) {
                        velY = r.nextInt(0 - (-4)) + (-4);
                    }
                } else {
                    // if hitted the wall enemy runs different like before
                    booleanvalue = r.nextInt(100 - 1) + 1; //maybe change

                    if (booleanvalue == 10) {
                        // now enemy aims to player
                        hittedwall = false;
                    }
                }
            }
        }
    }

    private void remove(){
        manager.removeObject(this);
    }
    private void removeWithObject(GameObject tempobject){
        manager.removeObject(tempobject);
        enemysAlive--;
    }

    public void isDead() {
        if (hp <= 0) {
            remove();
        }
    }

    public void update() {
        move();

        choose = r.nextInt(10);

        checkIfGone();

        collision();

        isDead();
    }

    //Enemy is displaying in this Color for now
    public void render(Graphics g) {
        //-> Just for testing Bounding Boxes around the Enemys
        Graphics2D g2d = (Graphics2D) g;
        /*g.setColor(Color.GREEN);
        g2d.draw(getBoundsAround());*/

        g.drawImage(enemy_img, (int) x, (int) y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    public Rectangle getBoundsAround() {
        return new Rectangle((int) x , (int) y , 32, 32);
    }

    private void Spawner(int Wavesize){
        for(int i = 0; i < (Wavesize* 5)+1; i++){
            x = r.nextInt(((55*32)-1)-1);
            y = r.nextInt(((55*32)-1)-1);

            for (int[] c: Game.wallCords) {
                if (c[0]== x || c[1]==y) {
                    //System.out.println("wall, this is i " + i);
                }
            }
            //System.out.println("spawning");
            Game.SpawnEnemy((int)x,(int)y);
        }
    }

    private void checkIfGone(){
        if((y > 1054 || y < 64) ||( x >1900 || x< 0)){
            removeWithObject(this);
        }
    }


}
