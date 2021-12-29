package Woralcoholics.game;

import java.util.LinkedList;

public class Gun {

    //region INSTANCE VARIABLES
    LinkedList<Gun> guns = new LinkedList<>();
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
        tempObject.type=type;
        tempObject.locked=locked;
        guns.add(tempObject);
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