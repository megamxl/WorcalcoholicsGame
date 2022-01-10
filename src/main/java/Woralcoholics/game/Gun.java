package Woralcoholics.game;

import java.util.LinkedList;

/**
 * The Gun Class
 *
 * @author Christoph Oprawill
 */

public class Gun {

    //region INSTANCE VARIABLES
    public static LinkedList<Gun> guns = new LinkedList<>();
    private Enum type;
    private boolean locked;

    //endregion
    //region CONSTRUCTOR
    public void Gun(Enum type, boolean locked) {
        this.type = type;
        this.locked = locked;
    }

    //endregion
    //region METHODS
    public void addObject(Gun tempObject, Enum type, boolean locked) {
        tempObject.type = type;
        tempObject.locked = locked;
        guns.add(tempObject);
    }

    public void manipulteList(int index, Gun tempObject, Enum type, boolean locked) {
        tempObject.type = type;
        tempObject.locked = locked;
        guns.set(index, tempObject);
    }

    public int getIndex(GunType gunType) {
        int indexGun = 0;
        int i = 0;

        //boolean breakOut=false;
        for (Gun g : guns) {
            if (g.getType() == gunType) {
                indexGun = i;
                break;
            }
            i++;
        }
        return indexGun;
    }

    //endregion
    //region GETTER/SETTER
    public Enum getType() {
        return type;
    }

    public void setType(Enum type) {
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