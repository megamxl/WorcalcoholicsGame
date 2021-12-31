package Woralcoholics.game;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// in this class the input management will happen through KeyAdapter

public class KeyInput extends KeyAdapter {
    GameManager gameManager;
    private Game game;
    GameObject player;

    public KeyInput(GameManager gameManager, Game game)
    {
        this.gameManager = gameManager;
        this.game = game;
    }

    @Override
    /**
    Checks if the A, D, W or S keys are getting pressed and sets the according boolean value of
    the gameManager Class to true.
     */
    public void keyPressed(KeyEvent e)
    {
        for (int i = 0; i < gameManager.object.size(); i++) {
            if (gameManager.object.get(i).getId() == ID.Player) {
                player = gameManager.object.get(i);
                break;
            }
        }
        int key = e.getKeyCode();
        switch(key) {
            case KeyEvent.VK_NUMPAD0 -> Game.setState(GameState.STUDIO);
            case KeyEvent.VK_NUMPAD1 -> Game.setState(GameState.TITLE);
            case KeyEvent.VK_NUMPAD2 -> Game.setState(GameState.MAIN_MENU);
            case KeyEvent.VK_NUMPAD3 -> Game.setState(GameState.LEVEL);
            case KeyEvent.VK_NUMPAD4 -> Game.setState(GameState.TUTORIAL);
            case KeyEvent.VK_NUMPAD5 -> Game.setState(GameState.HIGH_SCORES);
            case KeyEvent.VK_NUMPAD6 -> Game.setState(GameState.OPTIONS);
            case KeyEvent.VK_NUMPAD7 -> Game.setState(GameState.PAUSE_MENU);
            case KeyEvent.VK_NUMPAD8 -> Game.setState(GameState.GAME_OVER);

        }
        switch(Game.getState()) {
            case LEVEL, TUTORIAL -> {
                switch (key) {
                    case KeyEvent.VK_A -> gameManager.setLeft(true);
                    case KeyEvent.VK_D -> gameManager.setRight(true);
                    case KeyEvent.VK_W -> gameManager.setUp(true);
                    case KeyEvent.VK_S -> gameManager.setDown(true);
                    case KeyEvent.VK_L -> gameManager.setL(true);
                    case KeyEvent.VK_M -> gameManager.setM(true);
                    case KeyEvent.VK_K -> gameManager.setK(true);
                }
            }
        }
    }

    @Override
    /**
    Checks if the A, D, W or S keys are getting released and sets the according boolean value of
    the gameManager Class to false.
     */
    public void keyReleased(KeyEvent e)
    {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> gameManager.setLeft(false);
            case KeyEvent.VK_D -> gameManager.setRight(false);
            case KeyEvent.VK_W -> gameManager.setUp(false);
            case KeyEvent.VK_S -> gameManager.setDown(false);
            case KeyEvent.VK_L -> gameManager.setL(false);
            case KeyEvent.VK_K -> gameManager.setK(false);
            case KeyEvent.VK_M -> gameManager.setM(false);
        }
    }
}