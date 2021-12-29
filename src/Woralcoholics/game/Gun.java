package Woralcoholics.game;

import java.util.LinkedList;

public class Gun {

    //region VARIABLES
    LinkedList<Gun> guns = new LinkedList<>();
    private String type;
    private boolean locked;
    //endregion
    //region CONSTRUCTOR
    public void Gun(String type, boolean locked) {
        this.type = type;
        this.locked = locked;
    }
    //endregion
    //region METHODS
    public void addObject(Gun tempObject, String type, boolean locked) {
        tempObject.type=type;
        tempObject.locked=locked;
        guns.add(tempObject);
    }
    //endregion
    //region GETTER/SETTER
    public String getType() {
        return type;
    }

    public void setType(String type) {
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