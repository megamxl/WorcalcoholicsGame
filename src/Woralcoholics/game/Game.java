package Woralcoholics.game;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Canvas implements Runnable {
    /* ------------ Local variables for main game ------------ */
    private static final long serialVersionUID = 1L;

    // Variables
    final int SCREEN_WIDTH = 1024;
    final int SCREEN_HEIGHT = 576;

    private static GameState currentState;
    private GameState previousState;

    private boolean reloaded = true;
    private boolean isRunning;
    protected boolean paused, loaded;
    public static boolean shouldTime = false;
    public static boolean spawn = false;

    private BufferedImage level = null;
    private BufferedImage spritesheet = null;
    private BufferedImage upgradBoarder = null;
    private BufferedImage upgradBoard = null;
    private BufferedImage floor = null;
    private BufferedImage floorDirt1 = null;
    private BufferedImage floorDirt2 = null;
    private BufferedImage floorDirt3 = null;
    private Upgrades upgrades;


    public static List<int[]> wallCords = new ArrayList();

    public int ammo = 50;
    public int hp = 100;
    static Score score = new Score(0);

    public int shield = 0;
    public int armor = 0; //armor is referred to in %, so 10 would make a shield absorbing 10% of damage

    public static int PlayerX = 0;
    public static int PlayerY = 0;
    public static int TimerValue;
    public static int timerAction;
    private boolean wasstopped = false;
    private boolean triggeredonce = false;

    // Classes
    private Thread thread;

    public static GameManager handler;

    private static Animations an;
    private static Animations upgradeBoarderGet;

    private Camera camera;
    private double percent;

    Random r = new Random();
    Thread t1;

    /* ------------- Constructor for Game Class -------------- */

    public Game() throws IOException {
        currentState = previousState = GameState.STUDIO;    //initialize the currentState to STUDIO
        // make the window threw out own window class
        new Window(SCREEN_WIDTH, SCREEN_HEIGHT, "Workalcoholics Work In Progress", this);
        start();

        handler = new GameManager();
        camera = new Camera(0, 0, this);
        // when finished implement the Mouse and Key input
        InputStream path = this.getClass().getClassLoader().getResourceAsStream("level01.png");
        level = ImageIO.read(path);

        BufferedImageLoader loader = new BufferedImageLoader();
        spritesheet = loader.loadImage("/Spritesheet.png");
        an = new Animations(spritesheet);

        upgradBoarder =loader.loadImage("/UpgradeBorder.png");
        upgradeBoarderGet = new  Animations(upgradBoarder);

        //Adding Mouse and Keyboard Input
        MouseInput mouse = new MouseInput(handler, camera, this, an);
        this.addMouseListener(mouse);
        KeyInput keys = new KeyInput(handler, this);
        this.addKeyListener(keys);

        floor = an.getImage(1, 2, 64, 64);
        floorDirt1 = an.getImage(2, 2, 64, 64);
        floorDirt2 = an.getImage(3, 2, 64, 64);
        floorDirt3 = an.getImage(4, 2, 64, 64);
        loadMenu();
        //activate the timer, to show the Studio for 1 sec
        TimerValue = 0;
        shouldTime = true;
        timerAction = 3;
        this.upgrades = new Upgrades(this); //use upgrades.method for upgrade changes in Game and Player class

        fontLoader();
    }


    @Override
    /**
     * this is a well-known game loop also used in minecraft for making no difference how fast or slow you computer performance
     * so that the calculation are made at equal times no matter the computer
     */
    public void run() {
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;

        double delta = 0;
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                update();
                updates++;
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                if (shouldTime && !paused) {
                    timer();
                }
                timer += 1000;
                //System.out.println(updates + " Ticks, Fps " + frames);
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }

    /***
     * in every frame check where player is and update camera position
     */
    public void update() {
        if (currentState != previousState) {     //if there was a state change...
            stateChange();
        }
        if (currentState == GameState.LEVEL && !paused) {    //if we are in level and the game is not paused...
            for (int i = 0; i < handler.object.size(); i++) {
                if (handler.object.get(i).getId() == ID.Player) {
                    camera.update(handler.object.get(i));   //update the camera position to stay focused on the player
                }
            }
            handler.update();   //update every GameObject (camera is NOT a GameObject)
        }
        if (paused && !triggeredonce) {
            handler.backgroundsound.stop();
            wasstopped = true;
            triggeredonce = true;
            //System.out.println("STOP");
        }
        if (!paused && wasstopped) {
            handler.backgroundsound.start();
            wasstopped = false;
            triggeredonce = false;
            //System.out.println("START");
        }
        CalculateReloadingRectangle(handler.wait, (int) handler.del);

        CheckReloaded();

    }

    /***
     * The complete Render functions handles UI and the game rendering every frame
     */
    public void render() {
        // prepares the next 3 Frames to be rendered
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        // assigning the Graphics variables that the BufferStrategy can be used
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        //if we are not in the level, render a menu
        if (currentState != GameState.LEVEL) {
            renderMenu(g);
            handler.enemy.removeAll(handler.enemy);
            //System.out.println("SPAWN" + handler.enemy.size());
        } else {
            renderBackground(g);
            //translates our screen
            g2d.translate(-camera.getX(), -camera.getY());

            handler.render(g);

            g2d.translate(camera.getX(), camera.getY());

            renderUi(g);
        }
        // between this it can be drawn to the screen

        // rendering gets executed in the way it is written top down

        // end of drawing place
        g.dispose();
        bs.show();
    }

    /* ---------- Private functions for game Class ----------- */

    private void stateChange() {
        if (!loaded) {   //unload function (clear the object list)
            while (handler.object.size() > 0) {
                handler.object.remove(0);
            }
        }
        if (currentState == GameState.LEVEL) {   //if we have changed to LEVEL...
            if (!loaded) {                   //...if no level is loaded, load the level
                Enemy.waves = 1;            //reset Enemy waves
                Enemy.enemysAlive = 0;
                hp = 100;                   //reset player specific values
                ammo = 50;                  // max hp = 100, max ammo = 50, max shield = 40, max armor = ...
                shield = 0;
                armor = 0;
                camera.shake = false;       //camera should not shake
                loadLevel(level);           //load the level
            }
            paused = false;  //level is running and not paused (when coming from e.g. PAUSE_MENU or UPGRADE_MENU, where a level is already loaded)
        } else {
            loadMenu();                     //load the menu of currentState
        }
        previousState = currentState;       //the previous state becomes the current state, to again detect a state change
    }

    /***
     * Instructions to render the studio screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderStudio(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("Workalcoholics", SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        g.drawString("presents", SCREEN_WIDTH / 2, SCREEN_HEIGHT * 3 / 4);
    }

    /***
     * Instructions to render the title screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderTitle(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("TITLE", SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        g.drawString("LMB: MAIN MENU", SCREEN_WIDTH / 2, SCREEN_HEIGHT * 3 / 4);
    }

    /***
     * Instructions to render the Main Menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderMainMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.drawString("MAIN MENU", SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        g.drawString("LMB: LEVEL    RMB: OPTIONS", SCREEN_WIDTH / 2, SCREEN_HEIGHT * 3 / 4);
    }

    /***
     * Instructions to render the Options screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderOptions(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("OPTIONS", SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        g.drawString("RMB: MAIN MENU", SCREEN_WIDTH / 2, SCREEN_HEIGHT * 3 / 4);
    }

    /***
     * Instructions to render the Pause menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderPauseMenu(Graphics g) {
        //g.setColor(new Color(0,0,0,127));
        //g.fillRect(0, 0, screenWidth, screenHeight);
        g.setColor(Color.WHITE);
        g.drawString("PAUSE_MENU", SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        g.drawString("RMB: LEVEL", SCREEN_WIDTH / 2, SCREEN_HEIGHT * 3 / 4);
    }

    /***
     * Instructions to render the Upgrade Menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderUpgradeMenu(Graphics g) {
        g.setColor(new Color(0,0,0,127));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        upgradBoard = upgradeBoarderGet.getImage(1,1,320,600);
        g.drawImage(upgradBoard, 137, 30,null);
        g.drawImage(upgradBoard, 377, 30,null);
        g.drawImage(upgradBoard, 617 , 30,null);
        g.setColor(Color.WHITE);
        g.drawString("LMB: BACK TO LEVEL", SCREEN_WIDTH / 2, SCREEN_HEIGHT * 3 / 4);
    }

    /***
     * Instructions to render the Game over screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("GAME OVER", SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        g.drawString("Press LMB to Start again", SCREEN_WIDTH / 2, SCREEN_HEIGHT * 3 / 4);
    }

    /***
     * A switch case functions for determining what screen to render
     * @param g Graphics object
     */
    private void renderMenu(Graphics g) {
        switch (currentState) {
            case STUDIO -> renderStudio(g);
            case TITLE -> renderTitle(g);
            case MAIN_MENU -> renderMainMenu(g);
            case OPTIONS -> renderOptions(g);
            case PAUSE_MENU -> renderPauseMenu(g);
            case UPGRADE_MENU -> renderUpgradeMenu(g);
            case GAME_OVER -> renderGameOver(g);
        }
        if (!loaded) {
            handler.render(g);
        }
    }

    /***
     * just the instructions on how to render the UI
     * @param g Graphics Object
     */
    private void renderUi(Graphics g) {

        g.setColor(Color.gray);
        g.fillRect(5, 5, 200, 16); //hp
        g.fillRect(5, 30, 200, 16); //ammo
        g.fillRect(5, 70, 200, 8); //reload
        if(shield > 0)
            g.fillRect(5, 55, 200, 16); //shield

        if(shield > 0){
            g.setColor(Color.blue);
            g.drawString("SHIELD: " + shield + "/40", 210, 67);
            g.fillRect(5, 55, shield * 5, 16);
        }

        g.setColor(Color.cyan);
        g.drawString("AMMO: " + ammo + "/50", 210, 42);
        g.fillRect(5, 30, ammo * 4, 16);

        g.setColor(Color.orange);
        g.drawString("RELOAD: " + (int)percent + "%", 210, 78);
        g.fillRect(5, 70, (int) percent * 2, 8);

        if (hp >= 70)
            g.setColor(Color.green);
        else if (hp >= 40)
            g.setColor(Color.orange);
        else
            g.setColor(Color.red);
        g.drawString("HP: " + hp + "/100", 210, 17);
        g.fillRect(5, 5, hp * 2, 16);

        g.setColor(Color.black);
        g.drawRect(5, 5, 200, 16); //hp
        g.drawRect(5, 30, 200, 16); //ammo
        if(shield > 0)
            g.drawRect(5, 55, 200, 16);

        g.setColor(Color.MAGENTA);
        //g.drawString("Sound " + handler.soundv, 930, 17);
        g.drawString("Waves " + Enemy.waves, 930, 40);
        g.drawString("Enemies " + Enemy.enemysAlive, 915, 63);
        g.drawString("Score " + score.showScore(), 915, 76);

        if (shouldTime && timerAction == 1) {    //if the timer is active AND the timerAction corresponds to wave-countdown...
            g.setColor(Color.ORANGE);
            //g.setFont(new Font("Future Blood",Font.PLAIN,80));
            g.setFont(new Font("Masked Hero Demo",Font.PLAIN,45));
            g.drawString("Next Wave spawns in " + TimerValue + " s", 50, 250);

        }
    }

    /***
     * as the name states loads all kinds of Menus, which is not the level
     */
    private void loadMenu() {
        switch (currentState) {
            case STUDIO -> System.out.println("STUDIO");
            case TITLE -> {
            }
            case MAIN_MENU -> {
                //System.out.println("MAIN MENU");
                handler.addObject(new UIButton(10, 10, 400, 300, "Level", GameState.LEVEL, ID.UIButton, this, an));
                handler.addObject(new UIButton(510, 10, 400, 300, "Options", GameState.OPTIONS, ID.UIButton, this, an));
            }
            case OPTIONS -> handler.addObject(new UIButton(10, 10, 400, 300, "Main Menu", GameState.MAIN_MENU, ID.UIButton, this, an));//System.out.println("OPTIONS");
            case PAUSE_MENU -> {
                paused = true;                  //we are in PAUSE_MENU, so set paused true
            }
            case UPGRADE_MENU -> {
                System.out.println("UPGRADE_MENU");
                paused = true;      //Pause the game until Player chose an Upgrade
            }
            case GAME_OVER -> {
                System.out.println("GAME OVER");
                score.resetSore();
                handler.backgroundsound.close();
                //System.out.println("SOUND CLOSE");
                loaded = false;                 //the player lost, so the level should unload
            }
        }
    }


    private void fontLoader(){
        try {
            //Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Resource/Future Blood.ttf")).deriveFont(12f);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Resource/Masked Hero.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(customFont);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /***
     * The function to maka a playable level out of a Buffered Image
     * @param image The level Png
     */
    private void loadLevel(BufferedImage image) {
        int h = image.getHeight();
        int w = image.getWidth();
        int i = 0;


        for (int xx = 0; xx < w; xx++) {
            for (int yy = 0; yy < h; yy++) {
                int pixel = image.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 255) {
                    // Creates the new blocks which function as the walls
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block, an, randomNumber(1, 7), 1));
                    wallCords.add(new int[]{xx, yy});
                }
                if (blue == 255 && green == 0) {
                    handler.addObject(new Player(xx * 32, yy * 32, ID.Player, handler, this, camera, an));
                    PlayerX = xx * 32;
                    PlayerY = yy * 32;
                }
                if (green == 255) {
                    handler.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy, handler, an, score));
                }
                /*
                if(green == 255 && blue == 255)
                    handler.addObject(new Create(xx*32, yy*32, ID.Create));

            }*/
            }
        }
        handler.addObject(new GunnerEnemy(500, 500, ID.GunnerEnemy, handler, an, score)); //Test Gunner
        loaded = true;
        playBackgroundSound();
        //System.out.println("NEW GAME");
    }

    /**
     * just a counter that gets decreased thanks to our game loop
     */
    private void timer() {
        if (TimerValue > 0) {     //if the time is not over, decrease TimerValue
            TimerValue--;
        } else {   //if the waiting time is over...
            if (shouldTime) {    //...execute the previously set timerAction
                switch (timerAction) {
                    case 1 -> Enemy.Spawner(Enemy.waves, false, r);      //Spawn the next wave of enemies
                    case 2 -> currentState = GameState.UPGRADE_MENU;     //change state to UPGRADE_MENU (because of rendering)
                    case 3 -> currentState = GameState.TITLE;    //change state to TITLE (from STUDIO, 1 sec wait time)
                }
                shouldTime = false;  //deactivate the timer
            }
        }//System.out.println(TimerValue);
    }

    /***
     * This function always renders the background
     * @param g Graphics object
     */
    private void renderBackground(Graphics g) {
        for (int i = 0; i < 30 * 72; i += 64) {
            for (int j = 0; j < 30 * 72; j += 64) {
                g.drawImage(floor, i, j, null);
                // draws a random floor dirt texture on top of the current floor tile
                switch (randomNumber(1, 4)) {
                    case 1:
                        g.drawImage(floorDirt1, i, j, null);
                        break;
                    case 2:
                        g.drawImage(floorDirt2, i, j, null);
                        break;
                    case 3:
                        g.drawImage(floorDirt3, i, j, null);
                        break;
                    default:
                        // no action
                        break;
                }
            }
        }
    }

    /***
     *     This function is responsible to make a new Thread and set the game to Running
     */
    private void start() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    /***
     * This function stops the current Instance of the game and stops Gameloop
     */
    private void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* ---------- Public functions for game Class ----------- */

    /***
     * Returns a random number between inclusive start and exclusive end.
     * @param start Start value
     * @param end End value
     * @return The Random number
     */
    public Integer randomNumber(int start, int end) {
        return new Random().ints(start, end).findFirst().getAsInt();
    }

    /***
     * A function inside the game calls to spawn the enemy's. it is static that i can be called in other classes
     * @param x X value
     * @param y Y value
     */
    public static void SpawnEnemy(int x, int y) {
        handler.addObject(new Enemy(x, y, ID.Enemy, handler, an, score));
    }

    public static void SpawnGunnerEnemy() {
        handler.addObject(new GunnerEnemy(500, 500, ID.Enemy, handler, an, score));
    }

    /***
     * Function to get the current GameState
     */
    public static GameState getState() {
        return currentState;
    }

    /***
     * Function to set the current GameState
     * @param state State to change to
     */
    public static void setState(GameState state) {
        currentState = state;
    }

    // the main function that runs everything
    public static void main(String[] args) throws IOException {
        try {
            new Game();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Function to run backgroundsound
     */
    private void playBackgroundSound() {
        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handler.playBackgroundSound();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
    }

    private void CalculateReloadingRectangle(double wait, int del) {

        if (reloaded == true) {
            //System.out.println("reloaded");
            percent=100;
        } else {
            //System.out.println("not");
            if(percent==100 && del!=0)
                percent=0;
            if(percent<100)
                if(del==200)
                    percent += 5; // default for 200 del
                else if(del==1000)
                    percent += 1.6;
        }

    }

    private void CheckReloaded() {
        handler.now = System.currentTimeMillis();
        if (handler.now > handler.wait && handler.ammo == true) {
            reloaded = true;
        } else {
            reloaded = false;
        }
    }

}