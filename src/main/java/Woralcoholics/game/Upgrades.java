package Woralcoholics.game;

import java.util.Random;

/**
 * @author Moritz Lindner
 */

public class Upgrades {

    private Game game;
    private static int[] randomUpgrades;

    public Upgrades(Game game) {
        this.game = game;
        drawRandomUpgrades(); //draw 3 random upgrades for the first time you can buy upgrades
    }

    public void maxMunition() {
        game.ammo = 50;
    } //max ammo is 50

    public void setMunition(int ammo) {
        if (ammo > 50)
            game.ammo = 50;
        else
            game.ammo = ammo;
    }

    public void ammoBox() {
        game.ammo += 30;
        game.ammoBox--;
    }

    /**
     * @return current Ammunition
     */
    public int getMunition() {
        return game.ammo;
    }

    /**
     * Sets Player health back to standard
     */
    public void maxHP() {
        game.hp = 100;
    } //max hp is 100

    /**
     * sets hap to desired value
     *
     * @param hp
     */
    public void setHP(int hp) {
        if (hp > 100)
            game.hp = 100;
        else
            game.hp = hp;
    }

    public void medKit() {
        game.hp += 50;
        game.medKit--;
    }

    /**
     * sets shield to desired value
     *
     * @param shield
     */
    public void setShield(int shield) {
        if (shield > 40)
            game.shield = 40;
        else
            game.shield = shield;
    }

    public int getShield() {
        return game.shield;
    }

    public void maxShield() {
        game.shield = 40;
    } //max shield should be 40 I guess

    public void shieldShot() {
        game.shield += 20;
        game.shieldShot--;
    }

    public void setArmor(int armor) { //no max shield yet, don't know how op this will be, probably 40-50%
        game.armor = armor;
    }

    public void addArmor(int armor) {
        if (game.armor + armor > 45)
            game.armor = 45;
        else
            game.armor += armor;
    }

    public void addBulletSpeed(float speed) {
        Bullet.bulletSpeed += speed;
    }

    public void setEnemySpeed(float speed) { //it basically adds or subtracts enemy speed
        Enemy.velocity = speed;
    }

    public void addGunnerEnemySpeed(float speed) {
        GunnerEnemy.movementSpeed += speed;
    }

    /**
     * calculates the actual damage with the armor and shield in consideration
     *
     * @param damage
     */
    public void damaged(int damage) {
        damage = (int) (damage * ((100. - (double) game.armor) / 100));
        for (int i = damage; i > 0; i--) {
            if (game.shield == 0)
                game.hp--;
            else
                game.shield--;
        }
    }

    //1 - maxAmmo, 2 - maxHP, 3 - maxShield, 4 - 15% armor, 5 - bullets get faster, 6 enemies slowed down

    /**
     * responsible for creating an array with 3 different, random Upgrades that you can choose after every 3 rounds
     * Is called everytime, when an upgrade has been bought
     */

    public static void drawRandomUpgrades() {
        boolean duplicate = false;
        Random random = new Random();
        int pick;
        int[] upgrades = new int[3];
        for (int i = 0; i < 3; i++) {
            pick = random.nextInt(6) + 1;
            for (int j = 0; j < 3; j++)
                if (upgrades[j] == pick) {
                    i--;
                    duplicate = true;
                    break;
                }
            if (!duplicate) {
                upgrades[i] = pick;
                duplicate = false;
            } else
                duplicate = false;
        }
        randomUpgrades = upgrades;
    }

    public int[] getUpgrades() {
        return this.randomUpgrades;
    }

    /**
     * there are the 6 random upgrades and their corresponding methods for usage that we have implemented so far
     *
     * @param input
     */

    public void getUpgrade(int input) {
        switch (input) {
            case 1:
                this.maxMunition();
                break;
            case 2:
                this.maxHP();
                break;
            case 3:
                this.maxShield();
                break;
            case 4:
                this.addBulletSpeed(10);
                break;
            case 5:
                this.addGunnerEnemySpeed(-1);
                this.setEnemySpeed(-2);
                break;
            case 6:
                this.addArmor(15);
                break;
        }
    }

    public String drawUpgrades(int input) {
        switch (input) {
            case 1:
                return "MAX AMMO";
            case 2:
                return "MAX HP";
            case 3:
                return "MAX SHIELD";
            case 4:
                return "FASTER BULLETS";
            case 5:
                return "SLOWER ENEMIES";
            case 6:
                return "+15% ARMOR";
            default:
                return "";
        }
    }
}