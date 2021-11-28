package Woralcoholics.game;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// in this class the input management will happen through KeyAdapter

public class KeyInput extends KeyAdapter {
    GameManager gameManager;

    public KeyInput(GameManager gameManger)
    {
        this.gameManager = gameManger;
    }

    @Override
    /*
    Checks if the A, D, W or S keys are getting pressed and sets the according boolean value of
    the gameManager Class to true.
     */
    public void keyPressed(KeyEvent e)
    {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_A:
                gameManager.setLeft(true);
                break;
            case KeyEvent.VK_D:
                gameManager.setRight(true);
                break;
            case KeyEvent.VK_W:
                gameManager.setUp(true);
                break;
            case KeyEvent.VK_S:
                gameManager.setDown(true);
                break;
        }
    }

    @Override
    /*
    Checks if the A, D, W or S keys are getting released and sets the according boolean value of
    the gameManager Class to false.
     */
    public void keyReleased(KeyEvent e)
    {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_A:
                gameManager.setLeft(false);
                break;
            case KeyEvent.VK_D:
                gameManager.setRight(false);
                break;
            case KeyEvent.VK_W:
                gameManager.setUp(false);
                break;
            case KeyEvent.VK_S:
                gameManager.setDown(false);
                break;
        }
    }
}