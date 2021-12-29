package Woralcoholics.game;

public class Upgrades {

    private Game game;

    public Upgrades(Game game) {
        this.game = game;
    }

    public void maxMunition() {
        game.ammo = 50;
    } //max ammo is 50

    public void setMunition(int ammo) {
        game.ammo = ammo;
    }

    public int getMunition(){
        return game.ammo;
    }

    public void maxHP() {
        game.hp = 100;
    } //max hp is 100

    public void setHP(int hp) {
        game.hp = hp;
    }

    public void setShield(int shield) {
        game.shield = shield;
    }

    public void maxShield() {
        game.shield = 40;
    } //max shield should be 40 I guess

    public void setArmor(int armor) { //no max shield yet, don't know how op this will be, probably 40-50%
        game.armor = armor;
    }

    public void setBulletSpeed(float speed){
        Bullet.bulletSpeed = speed;
    }

    public void setEnemySpeed(float speed){ //it basically adds or subtracts enemy speed
        Enemy.velocity = speed;
    }

    public void setGunnerEnemySpeed(float speed){
        GunnerEnemy.movementSpeed = speed;
    }

    public void damaged(int damage) {
        damage = (int) (damage *  ((100. - (double) game.armor) / 100));
        for (int i = damage; i > 0; i--) {
            if (game.shield == 0)
                game.hp--;
            else
                game.shield--;
        }
    }
}
