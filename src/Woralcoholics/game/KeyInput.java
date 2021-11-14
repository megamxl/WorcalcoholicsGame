package Woralcoholics.game;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// in this class the input management will happen through KeyAdapter

public class KeyInput extends KeyAdapter {
    GameManager gameManger;

    public KeyInput(GameManager gameManger)
    {
        this.gameManger = gameManger;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_A:
                gameManger.setLeft(true);
                break;
            case KeyEvent.VK_D:
                gameManger.setRight(true);
                break;
            case KeyEvent.VK_W:
                gameManger.setUp(true);
                break;
            case KeyEvent.VK_S:
                gameManger.setDown(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_A:
                gameManger.setLeft(false);
                break;
            case KeyEvent.VK_D:
                gameManger.setRight(false);
                break;
            case KeyEvent.VK_W:
                gameManger.setUp(false);
                break;
            case KeyEvent.VK_S:
                gameManger.setDown(false);
                break;
        }
    }
}