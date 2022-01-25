package Woralcoholics.game;

public class Money {
    private int money;

    public Money(){
        this.money = 0;
    }

    public void setMoney(int amount){
        this.money = amount;
    }

    public void addMoney(int amount){
        this.money += amount;
    }

    public int getMoney(){
        return this.money;
    }

    public void purchase(int amount){
        this.money -= amount;
    }

    public void resetMoney(){
        this.money = 0;
    }
}
