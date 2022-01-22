package Woralcoholics.game;

import java.util.LinkedList;

/**
 * The Weapon Class
 *
 * @author Christoph Oprawill
 */

public class Weapon {

    //region INSTANCE VARIABLES
    public static LinkedList<Weapon> weapons = new LinkedList<>();
    private WeaponType type;
    private boolean locked;

    //endregion
    //region CONSTRUCTOR
    public void Weapon(WeaponType type, boolean locked) {
        this.type = type;
        this.locked = locked;
    }

    //endregion
    //region METHODS
    public void addObject(Weapon tempObject, WeaponType type, boolean locked) {
        tempObject.type = type;
        tempObject.locked = locked;
        weapons.add(tempObject);
    }

    public void manipulteList(int index, Weapon tempObject, WeaponType type, boolean locked) {
        tempObject.type = type;
        tempObject.locked = locked;
        weapons.set(index, tempObject);
    }

    public int getIndex(WeaponType weaponType) {
        int indexWeapon = 0;
        int i = 0;

        //boolean breakOut=false;
        for (Weapon w : weapons) {
            if (w.getType() == weaponType) {
                indexWeapon = i;
                break;
            }
            i++;
        }
        return indexWeapon;
    }

    //endregion
    //region GETTER/SETTER
    public WeaponType getType() {
        return type;
    }

    public void setType(WeaponType type) {
        this.type = type;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    //endregion
}