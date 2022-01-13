package Woralcoholics.game;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Woralcoholics.game.FillTutorialArray.fill;
import static Woralcoholics.game.FillTutorialArray.tutorialTexts;

/**
 * First Inspiration for Game class
 * https://www.youtube.com/watch?v=e9jRfgjV4FQ&t=1s
 *
 * @author Maximilian Nowak
 * @author Christoph Oprawill
 * @author Gustavo Podzuweit
 */

public class Game extends Canvas implements Runnable {
    /* ------------ Local variables for main game ------------ */
    private static final long serialVersionUID = 1L;

    // Variables
    final int SCREEN_WIDTH = 1024;
    final int SCREEN_HEIGHT = 576;

    public static GameState currentState;
    private GameState previousState, checkState;
    private int menuCount;
    public static int lastScore = 0;

    private boolean isRunning;
    protected static boolean paused, loaded;
    public static boolean inTutorial = false;
    public static boolean inFirstTutorialZone = false;
    public static boolean inSecondTutorialZone = false;
    public static boolean inThirdTutorialZone = false;
    public static boolean shouldTime = false;
    public static boolean spawn = false;

    private Graphics g;

    private BufferedImage level;
    private BufferedImage tutorialLevel;
    private BufferedImage spritesheet;
    private BufferedImage playerWalkCycleImg;
    private BufferedImage playerIdleCycleImg;
    private BufferedImage enemyCycleImg;
    private BufferedImage enemyBloodImg;
    private BufferedImage upgradeButtonImg;
    private BufferedImage tutorialBoarder;
    private BufferedImage bloodScreen;
    private BufferedImage upgradBoard = null;
    private BufferedImage UIButtonImg;
    private BufferedImage gameOverUIButtonImg;
    private BufferedImage floor;
    private BufferedImage floorDirt1;
    private BufferedImage floorDirt2;
    private BufferedImage floorDirt3;
    private BufferedImage imgOver;
    private BufferedImage imgStudio;
    private BufferedImage imgTitle;
    private BufferedImage currGun = null;
    private Upgrades upgrades;

    public static BufferedImage[] playerWalkingLeft = new BufferedImage[10];
    public static BufferedImage[] playerWalkingRight = new BufferedImage[10];
    public static BufferedImage[] playerIdle = new BufferedImage[4];
    public static BufferedImage[] enemyDeadShadow = new BufferedImage[10];
    public static BufferedImage[] enemy = new BufferedImage[13];



    public static List<int[]> wallCords = new ArrayList();
    public static List<Enemy> enemyPool = new ArrayList();
    public static List<GunnerEnemy> enemyGunnerPool = new ArrayList();
    public static List<EnemyShadow> enemyShadowPool = new ArrayList();

    public int ammo = 50;
    public int hp = 100;
    static Score score = new Score(0);

    public static String playerName = null;
    private String levelDecision;

    public int shield = 0;
    public int armor = 0; //armor is referred to in %, so 10 would make a shield absorbing 10% of damage

    public static int PlayerX = 0;
    public static int PlayerY = 0;
    public static int TimerValue;
    public static int timerAction;
    public static int curentTutorialscore = 0;
    public static int curentAmmo = 0;
    public static boolean isDead = false;
    public static boolean takesDamage = false;
    private boolean wasstopped = false;
    private boolean triggeredonce = false;

    // Classes
    private Thread thread;

    public static GameManager handler;

    private static ImageGetter imageGetter;
    private static ImageGetter getUpgradeButton;
    private static ImageGetter getPlayerWalkCycle;
    private static ImageGetter getPlayerIdleCycle;
    private static ImageGetter getEnemyBlood;
    private static ImageGetter getEnemySprite;
    private static ImageGetter getBloodScreen;
    private static ImageGetter GameOverScreenImg;
    private static ImageGetter getTutorialDialogWindowBorder;

    //private static ImageGetter TitleScreenImg;
    private static ImageGetter getUIButton;
    private static ImageGetter getGameOverUIButton;
    private static Gun gun;

    private DatabaseConnection databeseConection = new DatabaseConnection();

    private Camera camera;
    private double percent;
    private int index;

    Random r = new Random();
    Thread t1;

    MouseInput mouse;

    /* ------------- Constructor for Game Class -------------- */

    public Game() throws IOException, SQLException {
        System.out.println(ProcessHandle.current().pid());
        handler = new GameManager();
        currentState = checkState = GameState.STUDIO;    //initialize the currentState to STUDIO
        // make the window threw out own window class
        new Window(SCREEN_WIDTH, SCREEN_HEIGHT, "Chad vs. Aliens", this);
        start();
        gun = new Gun();

        levelDecision = String.valueOf(randomNumber(1, 6));
        camera = new Camera(0, 0, this);

        addGuns();
        checkSelectedGun();

        InputStream path = this.getClass().getClassLoader().getResourceAsStream("Levels/xdb_level0" + levelDecision + ".png");
        InputStream pathToTutorial = this.getClass().getClassLoader().getResourceAsStream("Levels/tutorial.png");
        level = ImageIO.read(path);
        tutorialLevel = ImageIO.read(pathToTutorial);

        // from here on, there are mostly images getting loaded
        BufferedImageLoader loader = new BufferedImageLoader();
        spritesheet = loader.loadImage("/Graphics/Spritesheet.png");
        imageGetter = new ImageGetter(spritesheet);

        upgradeButtonImg = loader.loadImage("/Graphics/UpgradeBorder.png");
        getUpgradeButton = new ImageGetter(upgradeButtonImg);

        UIButtonImg = loader.loadImage("/Graphics/UIButton_352x102.png");
        getUIButton = new ImageGetter(UIButtonImg);

        gameOverUIButtonImg = loader.loadImage("/Graphics/GameOverUIButton.png");
        getGameOverUIButton = new ImageGetter(gameOverUIButtonImg);

        bloodScreen = loader.loadImage("/Graphics/Bloodscreen.png");
        getBloodScreen = new ImageGetter(bloodScreen);

        playerWalkCycleImg = loader.loadImage("/Graphics/Animations/Character Running Spritesheet.png");
        getPlayerWalkCycle = new ImageGetter(playerWalkCycleImg);

        playerIdleCycleImg = loader.loadImage("/Graphics/Animations/Character Idle Spritesheet.png");
        getPlayerIdleCycle = new ImageGetter(playerIdleCycleImg);

        enemyCycleImg = loader.loadImage("/Graphics/Animations/Enemy 02 Spritesheet.png");
        getEnemySprite = new ImageGetter(enemyCycleImg);

        enemyBloodImg = loader.loadImage("/Graphics/Animations/Bloodparticle.png");
        getEnemyBlood = new ImageGetter(enemyBloodImg);

        BufferedImage GameOverScreen = loader.loadImage("/Graphics/gameOverPictureV3.png");
        GameOverScreenImg = new ImageGetter(GameOverScreen);
        imgOver = GameOverScreen.getSubimage(1, 1, 860/*720*/, 410/*480*/);

        BufferedImage tutorialDialogWindowBorderImg = loader.loadImage("/Graphics/TutorialBorder.png");
        getTutorialDialogWindowBorder = new ImageGetter(tutorialDialogWindowBorderImg);
        tutorialBoarder = getTutorialDialogWindowBorder.getImage(1, 1, SCREEN_WIDTH - 2, SCREEN_HEIGHT - 2);

        imgStudio = loader.loadImage("/Graphics/StudioImg.png");
        imgTitle = loader.loadImage("/Graphics/Titlescreen.png");

        //Adding Mouse and Keyboard Input
        mouse = new MouseInput(handler, camera, this, imageGetter, gun);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.addMouseWheelListener(mouse);
        KeyInput keys = new KeyInput(handler, this);
        this.addKeyListener(keys);

        // Sets mouse cursor image to a custom image
        // Toolkit can be seen like a real life toolkit, it is a class used for smaller special operations
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor cursorNEW = toolkit.createCustomCursor(loader.loadImage("/Graphics/Mouse Cursor.png")
                , new Point(this.getX(), this.getY()), "mouseCursorImage");
        this.setCursor(cursorNEW);

        floor = imageGetter.getImage(1, 2, 64, 64);
        floorDirt1 = imageGetter.getImage(2, 2, 64, 64);
        floorDirt2 = imageGetter.getImage(3, 2, 64, 64);
        floorDirt3 = imageGetter.getImage(4, 2, 64, 64);
        loadMenu();
        startTimer(2, 3);   //activate the timer, to show the Studio for 3 sec
        this.upgrades = new Upgrades(this); //use upgrades.method for upgrade changes in Game and Player class

        loadBullets();

        loadPlayerSprites();
        loadEnemyDeadSprites();
        loadPlayerIdle();
        loadEnenemySprites();

        FontLoader fontLoader = new FontLoader();

        fillEnemypool();
        fillEnemShadowypool();
        fillGunnerEnemypool();

        fill();

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
            // checks if mouse exits the game window during level and corrects its position
            if (getState() == GameState.LEVEL || getState() == GameState.TUTORIAL) {
                // checks if the mouse is outside the game window
                if (mouse != null)
                    mouse.checkIfExited(MouseInfo.getPointerInfo().getLocation());
            }

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                try {
                    update();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                updates++;
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                if (shouldTime && !paused) {
                    try {
                        timer();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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
    public void update() throws SQLException {
        //ammo = 50;
        //hp = 100;
        if (currentState != checkState) {     //if there was a state change...
            stateChange();
        }
        if ((currentState == GameState.LEVEL || currentState == GameState.TUTORIAL) && !paused) {    //if we are in level and the game is not paused...
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
        calculateReloadingRectangle((int) handler.del);

        checkReloaded();
        checkGunStatus();
        checkSelectedGun();
        updateLockStatus();
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
        g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        //if we are not in the level, render a menu
        if (currentState == GameState.LEVEL || currentState == GameState.TUTORIAL) {
            //translates our screen
            g2d.translate(-camera.getX(), -camera.getY());

            // IMPORTANT: renderBackground(g) has to be called AFTER g2d.translate
            // else the background moves with the camera
            renderBackground(g);

            handler.render(g);

            g2d.translate(camera.getX(), camera.getY());

            renderUi(g);

        } else {
            renderScreen(g);
            handler.enemy.removeAll(handler.enemy);
            //System.out.printlfn("SPAWN" + handler.enemy.size());
        }
        // between this it can be drawn to the screen

        // rendering gets executed in the way it is written top down

        // end of drawing place
        g.dispose();
        bs.show();
    }

    /* ---------- Private functions for game Class ----------- */

    private void renderTutorialBorders(Graphics g) {
        g.drawImage(tutorialBoarder, 60, 235, 900, 300, null);
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.PLAIN, 26));

        if (curentTutorialscore < tutorialTexts.length) {
            g.drawString(tutorialTexts[curentTutorialscore][0], 230, 465);
            g.drawString(tutorialTexts[curentTutorialscore][1], 230, 495);
        } else {
            curentTutorialscore = 0;
        }
    }

    private void stateChange() throws SQLException {
        //System.out.println("is " + currentState + " equal to " + checkState + "?");
        previousState = checkState;      //save the state before the state change
        checkState = currentState;       //the checkState becomes the current state, to again detect a state change
        //System.out.println(previousState + " -> " + currentState + ", check current against: " + checkState);
        if (currentState == GameState.MAIN_MENU) {
            loaded = false;
        }
        if (!loaded) {   //if nothing is loaded...
            handler.clearHandler(); //clear everything in the handler
            handler.hideBullets();
        } else {
            handler.clearObjects(ID.UIButton);      //clear the handler from all buttons, when a level is loaded
        }
        if (currentState == GameState.LEVEL || currentState == GameState.TUTORIAL) {   //if we have changed to LEVEL...
            if (!loaded) {                   //...if no level is loaded, load the level
                Enemy.waves = 1;//reset Enemy waves
                Enemy.enemysAlive = 0;
                Enemy.maxHp = 100;
                hp = 100;                   //reset player specific values
                ammo = 50;                  // max hp = 100, max ammo = 50, max shield = 40, max armor = ...
                shield = 0;
                armor = 0;
                camera.shake = false;   //camera should not shake
                switch (currentState) {
                    case LEVEL -> {
                        inTutorial = false;
                        loadLevel(level); //load the level
                        for (Gun gun : Gun.guns) {
                            if (gun.getType() != GunType.Pistol) {
                                gun.setLocked(true);
                            }
                        }
                        setGunToPistolAgain();
                    }
                    case TUTORIAL -> {
                        loadLevel(tutorialLevel);
                        inTutorial = true;
                        inFirstTutorialZone = false;
                        inSecondTutorialZone = false;
                        inThirdTutorialZone = false;
                        /*for (Gun g : Gun.guns) {
                            g.setLocked(false);
                        }*/
                    }
                }
            }
            paused = false;  //level is running and not paused (when coming from e.g. PAUSE_MENU or UPGRADE_MENU, where a level is already loaded)
        } else {
            loadMenu();                     //load the menu of currentState
        }
        //System.out.println(currentState + ": " + handler.object.size());
    }

    /***
     * A switch case functions for determining what screen to render
     * @param g Graphics object
     */
    private void renderScreen(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        switch (currentState) {
            case STUDIO -> {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                g.drawImage(imgStudio, SCREEN_WIDTH / 2 - 280, SCREEN_HEIGHT / 3, null);
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Cyberpunk", Font.PLAIN, 70));
                int stringx = g.getFontMetrics(new Font("Cyberpunk", Font.PLAIN, 70)).stringWidth("presents");
                g.drawString("presents", SCREEN_WIDTH / 2 - stringx / 2 - 60, SCREEN_HEIGHT * 2 / 3);
            }
            case TITLE -> {
                /*g.setColor(Color.BLACK);
                g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);*/
                g.drawImage(imgTitle, 0, 0, null);
            }
            case MAIN_MENU -> {
            }
            case TUTORIAL -> {
            }
            case HIGH_SCORES -> {
                g.setColor(Color.black);
                g.setFont(new Font("Masked Hero Demo", Font.PLAIN, 36));
                int x = SCREEN_WIDTH / 2 - g.getFontMetrics(new Font("Masked Hero Demo", Font.PLAIN, 36)).stringWidth("Highscores from Database") / 2;
                g.drawString("Highscores from Database", x, 100);
                g.setFont(new Font("Arial Black", Font.PLAIN, 40));
                if (DatabaseConnection.finishedFillingArray) {
                    g.setColor(Color.black);
                    g.drawString(DatabaseConnection.scoresArray[0], 300, 210);
                    g.drawString(DatabaseConnection.scoresArray[1], 300, 250);
                    g.drawString(DatabaseConnection.scoresArray[2], 300, 290);
                    g.drawString(DatabaseConnection.scoresArray[3], 300, 330);
                    g.drawString(DatabaseConnection.scoresArray[4], 300, 370);
                }
            }
            case OPTIONS -> {}
            case PAUSE_MENU -> {}
            case UPGRADE_MENU -> {}
            case CREDITS -> {
                g.setColor(Color.black);
                g.setFont(new Font("Arial Black", Font.PLAIN, 40));
                //System.out.println(g.getFontMetrics(new Font("Arial Black", Font.PLAIN, 40)).getHeight());
                g.drawString("Credits go here brrr", SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
            }
            case GAME_OVER -> {
                g.drawImage(imgOver, 1, 1, null);
                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font("DEBUG FREE TRIAL", Font.PLAIN, 75));
                g.drawString("Waves", SCREEN_WIDTH / 4 - 80, SCREEN_HEIGHT / 4);
                g.drawString("Score", SCREEN_WIDTH * 3 / 4 - 65, SCREEN_HEIGHT / 4);
                //g.drawString("your Score is" + lastScore, 300, SCREEN_HEIGHT - 65);
                g.setFont(new Font("DEBUG FREE TRIAL", Font.PLAIN, 175));
                g.drawString(String.valueOf(Enemy.waves - 1), SCREEN_WIDTH / 4, SCREEN_HEIGHT * 7 / 16);
                g.drawString(String.valueOf(lastScore), SCREEN_WIDTH * 3 / 4, SCREEN_HEIGHT * 7 / 16);
            }
        }
        //handler.render(g, ID.UIButton);
        if (!loaded) {
            handler.render(g);
        } else {
            handler.render(g, ID.UIButton);
        }
    }

    /***
     * just the instructions on how to render the UI
     * @param g Graphics Object
     */
    private void renderUi(Graphics g) {
        // if the player takes damage, the blood screen gets blended in for a short period of time
        if (takesDamage) {
            g.drawImage(bloodScreen, 1, 1, null);
        }

        g.setColor(Color.gray);
        g.fillRect(5, 5, 200, 16); //hp
        g.fillRect(5, 30, 200, 16); //ammo
        g.fillRect(5, 80, 200, 8); //reload
        if (shield > 0) {
            g.fillRect(5, 55, 200, 16); //shield

            g.setColor(Color.blue);
            g.drawString("SHIELD: " + shield + "/40", 210, 67);
            g.fillRect(5, 55, shield * 5, 16);
        }

        g.setColor(Color.cyan);
        g.drawString("AMMO: " + ammo + "/50", 210, 42);
        g.fillRect(5, 30, ammo * 4, 16);

        g.setColor(Color.orange);
        g.drawString("RELOAD: " + (int) percent + "%", 210, 89);
        g.fillRect(5, 80, (int) percent * 2, 8);

        if (handler.del == 0) {
            g.setColor(Color.cyan);
            //g.drawString("MACHINE GUN", 210, 95);
            currGun = imageGetter.getImage(2, 10, 64, 64);
        } else if (handler.del == 200) {
            g.setColor(Color.cyan);
            //g.drawString("PISTOL", 210, 95);
            currGun = imageGetter.getImage(3, 10, 64, 64);
        } else if (handler.del == 1000) {
            g.setColor(Color.cyan);
            //g.drawString("SHOTGUN", 210, 95);
            currGun = imageGetter.getImage(1, 10, 64, 64);
        }

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
        if (shield > 0)
            g.drawRect(5, 55, 200, 16);

        g.setColor(Color.MAGENTA);
        //g.drawString("Sound " + handler.soundv, 930, 17);
        g.drawString("Waves " + Enemy.waves, 930, 40);
        g.drawString("Enemies " + Enemy.enemysAlive, 915, 63);
        g.drawString("Score " + score.showScore(), 915, 76);

        if (shouldTime && timerAction == 1) {    //if the timer is active AND the timerAction corresponds to wave-countdown...
            g.setColor(Color.ORANGE);
            //g.setFont(new Font("/Fonts/Future Blood",Font.PLAIN,80));
            g.setFont(new Font("Masked Hero Demo", Font.PLAIN, 45));
            g.drawString("Next Wave spawns in " + (TimerValue + 1), 50, 250);

        }
        g.drawImage(currGun, 10, 470, null);

        if (currentState == GameState.TUTORIAL) {
            renderTutorialBorders(g);
        }
    }

    /***
     * as the name states loads all kinds of Menus, which is not the level
     */
    private void loadMenu() throws SQLException {
        GameState RETURN = previousState;
        if (previousState == GameState.OPTIONS) {
            menuCount--;
        }
        switch (currentState) {
            case STUDIO -> {
            }
            case TITLE -> {
                /*handler.addObject(new UIButton(SCREEN_WIDTH / 2, (SCREEN_HEIGHT - 25) / 2, 352, 102,
                        "START", GameState.MAIN_MENU, ID.UIButton, this, 1, 0,
                        uiButtonAnGet, 1, 1, 400, (SCREEN_HEIGHT - 25) / 2 + 20, 40));*/
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, (SCREEN_HEIGHT + 350) / 2, 352, 102,
                        "START", GameState.MAIN_MENU, ID.UIButton, this, 1, 0, getUIButton,
                        1, 1, g, 1, 40));
            }
            case MAIN_MENU -> {
                menuCount = 0;
                //JMenu mainMenu = new JMenu("Main Menu");
                //mainMenu.add(new JMenuItem("test"));
                handler.addObject(new UIButton(32, 32, 64, 64, "Title", GameState.TITLE, ID.UIButton,
                        this, 1, 0, imageGetter, 1, 6, g, 1, 0));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, 70, 352, 102, "Level",
                        GameState.LEVEL, ID.UIButton, this, 1, 0, getUIButton, 1, 1,
                        g, 1, 40));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, 195, 352, 102, "Tutorial",
                        GameState.TUTORIAL, ID.UIButton, this, 1, 0, getUIButton, 1, 1,
                        g, 1, 30));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, 320, 352, 102, "Scores",
                        GameState.HIGH_SCORES, ID.UIButton, this, 1, 0, getUIButton, 1,
                        1, g, 1, 35));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, 445, 352, 102, "Credits",
                        GameState.CREDITS, ID.UIButton, this, 1, 0, getUIButton, 1, 1,
                        g, 1, 32));
                handler.addObject(new UIButton(SCREEN_WIDTH - 46, 34, 64, 64, "Options",
                        GameState.OPTIONS, ID.UIButton, this, 1, 0, imageGetter, 2, 6, g,
                        1, 0));

            }
            case CREDITS -> {
                handler.addObject(new UIButton(32, 32, 64, 64, "Return", RETURN,
                        ID.UIButton, this, 1, 0, imageGetter, 1, 6, g, 1, 0));
            }
            case TUTORIAL -> {
                //handler.addObject(new UIButton(10, 10, 64, 64, "Return", RETURN, ID.UIButton, this, an, 0, 0, 40));
                //System.out.println(currentState.toString());
            }
            case HIGH_SCORES -> {
                try {
                    databeseConection.ReadFromDatabase();
                } catch (Exception e) {
                    System.out.println("Could not connect to Database");
                }
                //JOptionPane playerDataInput = new JOptionPane();
                handler.addObject(new UIButton(32, 32, 64, 64, "Return", RETURN,
                        ID.UIButton, this, 1, 0, imageGetter, 1, 6, g, 1, 0));
            }
            case OPTIONS -> {
                menuCount++;
                handler.addObject(new UIButton(32, 32, 64, 64, "Return", RETURN,
                        ID.UIButton, this, 1, 0, imageGetter, 1, 6, g, 1, 0));
                handler.addObject(new UIButton(SCREEN_WIDTH / 3, SCREEN_HEIGHT / 2, 64, 64, "M",
                        GameState.OPTIONS, ID.UIButton, this, 3, 0, imageGetter, 1, 2, g,
                        3, 40));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 64, 64, "K",
                        GameState.OPTIONS, ID.UIButton, this, 3, 0, imageGetter, 1, 2, g,
                        3, 40));
                handler.addObject(new UIButton(SCREEN_WIDTH * 2 / 3, SCREEN_HEIGHT / 2, 64, 64, "L",
                        GameState.OPTIONS, ID.UIButton, this, 3, 0, imageGetter, 1, 2, g,
                        3, 40));
            }
            case PAUSE_MENU -> {
                menuCount = 10;
                handler.addObject(new UIButton(32, 32, 64, 64, "Return", GameState.LEVEL,
                        ID.UIButton, this, 1, 0, imageGetter, 1, 6, g, 1, 0));
                handler.addObject(new UIButton(SCREEN_WIDTH - 46, 34, 64, 64, "Options",
                        GameState.OPTIONS, ID.UIButton, this, 1, 0, imageGetter, 2, 6, g,
                        1, 0));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 352, 102,
                        "Stop Playing", GameState.MAIN_MENU, ID.UIButton, this, 1, 0,
                        getUIButton, 1, 1, g, 1, 20));
                paused = true;//we are in PAUSE_MENU, so set paused true
            }
            case UPGRADE_MENU -> {
                int[] randomUpgrades = upgrades.getUpgrades();
                handler.addObject(new UIButton(SCREEN_WIDTH / 4 + 50, (SCREEN_HEIGHT + 125) / 2, 320, 600,
                        upgrades.drawUpgrades(randomUpgrades[0]), GameState.LEVEL, ID.UIButton, this, 2,
                        randomUpgrades[0], getUpgradeButton, 1, 1, g, 1, 20));
                handler.addObject(new UIButton(SCREEN_WIDTH / 4 - 5, (SCREEN_HEIGHT + 0) / 2, 64, 64,
                        "", GameState.LEVEL, ID.UIButton, this, 2,
                        randomUpgrades[0], imageGetter, randomUpgrades[0], 8, g, 1, 20));

                handler.addObject(new UIButton(SCREEN_WIDTH / 2 + 50, (SCREEN_HEIGHT + 125) / 2, 320, 600,
                        upgrades.drawUpgrades(randomUpgrades[1]), GameState.LEVEL, ID.UIButton, this, 2,
                        randomUpgrades[1], getUpgradeButton, 1, 1, g, 1, 20));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2 - 5, (SCREEN_HEIGHT + 0) / 2, 64, 64,
                        "", GameState.LEVEL, ID.UIButton, this, 2,
                        randomUpgrades[0], imageGetter, randomUpgrades[1], 8, g, 1, 20));

                handler.addObject(new UIButton(SCREEN_WIDTH * 3 / 4 + 50, (SCREEN_HEIGHT + 125) / 2, 320, 600,
                        upgrades.drawUpgrades(randomUpgrades[2]), GameState.LEVEL, ID.UIButton, this, 2,
                        randomUpgrades[2], getUpgradeButton, 1, 1, g, 1, 20));
                handler.addObject(new UIButton(SCREEN_WIDTH * 3 / 4 - 5, (SCREEN_HEIGHT + 0) / 2, 64, 64, "", GameState.LEVEL, ID.UIButton, this, 2,
                        randomUpgrades[0], imageGetter, randomUpgrades[2], 8, g, 1, 20));
                paused = true;      //Pause the game until Player chose an Upgrade
                /*for(int i = 0; i < 3; i++) {
                    System.out.println(i + ": " + randomUpgrades[i] + " " + upgrades.drawUpgrades(randomUpgrades[i]));
                }*/
                Enemy.spawnWaveAfterUpgrades();
            }
            case GAME_OVER -> {
                lastScore = score.showScore();
                score.resetSore();
                handler.backgroundsound.close();
                takesDamage = false;
                //ScoerSaveWindow.frame.setVisible(true);
                //Window.frame.setVisible(false);
                //System.out.println("SOUND CLOSE");
                handler.addObject(new UIButton(SCREEN_WIDTH / 4, SCREEN_HEIGHT * 4 / 5, 352, 102,
                        "Play Again?", GameState.LEVEL, ID.UIButton, this, 1, 0,
                        getUIButton/*getGameOverUIButton*/, 1, 1, g, 1, 20/*27*/));
                handler.addObject(new UIButton(SCREEN_WIDTH * 3 / 4, SCREEN_HEIGHT * 4 / 5, 352, 102,
                        "Stop Playing", GameState.MAIN_MENU, ID.UIButton, this, 1, 0,
                        getUIButton/*getGameOverUIButton*/, 1, 1, g, 1, 20/*27*/));
                startTimer(0, 4);   //after 1 sec, execute timerAction 4 -> enter name and upload score dialogs
            }
        }
    }

    /***
     * The function to maka a playable level out of a Buffered Image by checking the rgb value of each individual pixel
     * of the buffered image.
     * Depending on the r, b or g value of the pixel, a different kind of game object gets instantiated on that spot.
     * For example, a completely red pixel will instantiate a wall tile on that spot
     * @param image The level Png
     */
    private void loadLevel(BufferedImage image) {
        int h = image.getHeight();
        int w = image.getWidth();
        int i = 0;

        for (int xx = 0; xx < w; xx++) {
            for (int yy = 0; yy < h; yy++) {
                int pixel = image.getRGB(xx, yy);
                Color currColor = new Color(pixel, true);

                if (currColor.getRed() == 255 && currColor.getGreen() == 0 && currColor.getBlue() == 0) {
                    // Creates the new blocks which function as the walls
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block, imageGetter, randomNumber(1, 7), 1));
                    wallCords.add(new int[]{xx, yy});
                } else if (currColor.getRed() == 255 && currColor.getGreen() == 255 && currColor.getBlue() == 0) {
                    // Creates the new destroyable blocks which function as the walls
                    //add also more variations like blocks
                    handler.addObject(new DestroyableBoxes(xx * 32, yy * 32, ID.DestroyableBoxes, handler, imageGetter, 3, 3)); // col 3 - 7
                    // wallCords.add(new int[]{xx, yy});
                } else if (currColor.getRed() == 0 && currColor.getGreen() == 0 && currColor.getBlue() == 255) {
                    handler.addObject(new Player(xx * 32, yy * 32, ID.Player, handler, this, camera, imageGetter));
                    PlayerX = xx * 32;
                    PlayerY = yy * 32;
                } else if (currColor.getRed() == 0 && currColor.getGreen() == 255 && currColor.getBlue() == 0) {
                    //handler.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy, handler, imageGetter, score));
                    spawnEnemy(xx * 32, yy * 32);
                }
                /*if (green == 255 && blue == 255) {

                }*/
            }
        }
        loaded = true;
        playBackgroundSound();
        //System.out.println("NEW GAME");
    }

    /**
     * just a counter that gets decreased thanks to our game loop
     */
    private void timer() throws SQLException {
        if (TimerValue > 0) {     //if the time is not over, decrease TimerValue
            TimerValue--;
            // takesDamage has to be set to false here, because else, the blood screen will not disappear
            // as the timerAction is 1 when the new wave spawns and the timer action for removing the blood screen
            // is 5 and will not be invoked until further damage would be taken
            if (timerAction == 1) {
                takesDamage = false;
            }
        } else {   //if the waiting time is over...
            if (shouldTime) {    //...execute the previously set timerAction
                switch (timerAction) {
                    case 1 -> {
                        //currentState = GameState.UPGRADE_MENU;
                        Enemy.Spawner(Enemy.waves, false, r); //Spawn the next wave of enemies
                        //upgrades.addMunition(20);
                        takesDamage = false;
                    }
                    case 2 -> {
                        currentState = GameState.UPGRADE_MENU; //change state to UPGRADE_MENU (because of rendering)
                        takesDamage = false;
                    }
                    case 3 -> currentState = GameState.TITLE;    //change state to TITLE (from STUDIO, few sec wait time)
                    case 4 -> { //enter your name and choose whether to upload your score
                        playerName = JOptionPane.showInputDialog(null, "Please enter your name", null, JOptionPane.INFORMATION_MESSAGE);
                        int reply = JOptionPane.showConfirmDialog(null, "Do you want to upload your score to the cloud?", null, JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            try {
                                databeseConection.insertScoreAndNameIntoDatabase();
                            } catch (Exception e) {
                                System.out.println("could not connect to Database");
                            }
                            System.out.println("YES");
                        } else {
                            System.out.println("NO");
                        }
                        takesDamage = false;
                        System.out.println("over Con");
                    }
                    case 5 -> {
                        takesDamage = false;
                    }
                }
                shouldTime = false;  //deactivate the timer
            }
        }//System.out.println(TimerValue);
    }

    /***
     * Function to start the timer
     * @param secs Seconds to wait before the action
     * @param action which action to make after timer
     */
    public static void startTimer(int secs, int action) {
        TimerValue = secs - 1;
        shouldTime = true;
        timerAction = action;
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
    public static Integer randomNumber(int start, int end) {
        return new Random().ints(start, end).findFirst().getAsInt();
    }

    /*public void changeStateToMenu(){
        currentState = GameState.MAIN_MENU;             Unnötige Funktion? siehe weiter unten setState()
    }*/


    private void fillGunnerEnemypool(){
        for (int i = 0; i < 3 ; i++) {
            enemyGunnerPool.add( new GunnerEnemy(0, 0, ID.GunnerEnemy, handler, imageGetter, score));
        }

    }

    private void fillEnemypool() {
        for (int i = 0; i < 25; i++) {
            enemyPool.add(new Enemy(0, 0, ID.Enemy, handler, imageGetter, score));
        }
    }

    private void fillEnemShadowypool() {
        for (int i = 0; i < 25; i++) {
            enemyShadowPool.add(new EnemyShadow(0, 0, ID.EnemyShadow, imageGetter));
        }
    }

    public static void spawnGunnerEnemy(int x, int y){
        for (GunnerEnemy currEnemy: enemyGunnerPool) {
            if (!currEnemy.isInGame) {
                currEnemy.x = x;
                currEnemy.y = y;
                currEnemy.hp = Enemy.maxHp;
                Enemy.enemysAlive++;
                currEnemy.isInGame = true;
                handler.addObject(currEnemy);
                break;
            }
        }
    }

    /***
     * A function inside the game calls to spawn the enemy's. it is static that i can be called in other classes
     * @param x X value
     * @param y Y value
     */
    public static void spawnEnemy(int x, int y) {
        for (Enemy currEnemy : enemyPool) {
            if (!currEnemy.isInGame) {
                currEnemy.x = x;
                currEnemy.y = y;
                currEnemy.hp = Enemy.maxHp;
                currEnemy.isInGame = true;
                Enemy.enemysAlive++;
                handler.addObject(currEnemy);
                return;
            }
        }
    }


    public static void SpawnGunnerEnemyWithCords(int x, int y) {
        handler.addObject(new GunnerEnemy(x, y, ID.Enemy, handler, imageGetter, score));
    }

    public static void SpawnCreate(int x, int y) {
        handler.addObject(new Crate(x, y, ID.Crate, imageGetter));
    }

    /***
     * for rendering the enemyshadow if enemy dies
     * @param x
     * @param y
     */
    public static void UseEnemyShadow(int x, int y) {
        handler.addObject(new EnemyShadow(x, y, ID.EnemyShadow, imageGetter));
        //enemyShadowPool.add(0,new EnemyShadow(x, y, ID.EnemyShadow, imageGetter));
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

    /***
     *calculate the ReloadingBar
     * @param del
     */
    private void calculateReloadingRectangle(int del) {

        if (handler.reloaded) {
            //System.out.println("reloaded");
            percent = 100;
        } else {
            //System.out.println("not");
            if (percent == 100 && del != 0)
                percent = 0;
            if (percent < 100)
                if (del == 200)
                    percent += 5; // default for 200 del
                else if (del == 1000)
                    percent += 1.6;
        }
    }

    /***
     * check if the weapon is reloaded to shoot
     */
    private void checkReloaded() {
        handler.now = System.currentTimeMillis();
        if (handler.now > handler.wait && handler.ammo == true) {
            handler.reloaded = true;
        } else {
            handler.reloaded = false;
        }
    }

    /***
     * add guns to list
     */
    private void addGuns() {
        gun.addObject(new Gun(), GunType.Pistol, false); // start with pistol
        gun.addObject(new Gun(), GunType.Shotgun, true); // second shotgun
        gun.addObject(new Gun(), GunType.MachineGun, true); //third machine gun  -> weakest to strongest

    }
    private void setGunToPistolAgain() {
        handler.selectedgun = Gun.guns.get(0);
        handler.gunindex = 0;
    }

    /***
     * get the coloumn and row of the spritesheet for the specific gun
     * @return
     */
    public int[] getColRowFromIndex() {
        int[] colrow = new int[3];
        int col;
        int row = 10;
        if (handler.gunindex == 0) {
            col = 7;
        } else if (handler.gunindex == 1) {
            col = 5;
        } else {
            col = 6;
        }
        colrow[0] = col;
        colrow[1] = row;
        return colrow;
    }

    /***
     * change the selected gun  (when you rotate your mousewheel)
     */
    private void checkSelectedGun() {
        handler.selectedgun = Gun.guns.get(handler.gunindex);
    }

    private void checkGunStatus() {
        if (handler.selectedgun.getType() == GunType.Pistol)
            handler.del = 200;
        else if (handler.selectedgun.getType() == GunType.Shotgun)
            handler.del = 1000;
        else //Machine Gun
            handler.del = 0;
    }

    /***
     * in tutorial all weapons are unlocked
     * on different waves you unlock at first shotgun and then the machine gun
     */
    private void updateLockStatus() {
        if (getState() == GameState.TUTORIAL) {
            index = gun.getIndex(GunType.Shotgun);
            gun.manipulteList(index, new Gun(), GunType.Shotgun, false);
            index = gun.getIndex(GunType.MachineGun);
            gun.manipulteList(index, new Gun(), GunType.MachineGun, false);
        } else {
            if (Enemy.waves == 2) { //for test purposes on wave 2
                index = gun.getIndex(GunType.Shotgun);
                gun.manipulteList(index, new Gun(), GunType.Shotgun, false);
            } else if (Enemy.waves == 3) { //for test purposes on wave 3
                index = gun.getIndex(GunType.MachineGun);
                gun.manipulteList(index, new Gun(), GunType.MachineGun, false);
            }
        }
    }

    private void loadBullets() {
        for (int i = 0; i < 50; i++) {
            handler.bullets.add(new Bullet(0, 0, ID.Bullet, handler, imageGetter));
        }
    }

    private void loadPlayerSprites() {
        playerWalkingLeft[0] = getPlayerWalkCycle.getImage32(1, 1, 32, 32);
        playerWalkingLeft[1] = getPlayerWalkCycle.getImage32(2, 1, 32, 32);
        playerWalkingLeft[2] = getPlayerWalkCycle.getImage32(3, 1, 32, 32);
        playerWalkingLeft[3] = getPlayerWalkCycle.getImage32(4, 1, 32, 32);
        playerWalkingLeft[4] = getPlayerWalkCycle.getImage32(5, 1, 32, 32);
        playerWalkingLeft[5] = getPlayerWalkCycle.getImage32(6, 1, 32, 32);
        playerWalkingLeft[6] = getPlayerWalkCycle.getImage32(7, 1, 32, 32);
        playerWalkingLeft[7] = getPlayerWalkCycle.getImage32(8, 1, 32, 32);
        playerWalkingLeft[8] = getPlayerWalkCycle.getImage32(9, 1, 32, 32);
        playerWalkingLeft[9] = getPlayerWalkCycle.getImage32(10, 1, 32, 32);


        for (int i = 0; i < playerWalkingLeft.length; i++) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-playerWalkingLeft[i].getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            playerWalkingRight[i] = op.filter(playerWalkingLeft[i], null);

        }
    }

    private void loadEnemyDeadSprites() {
        enemyDeadShadow[0] = getEnemyBlood.getImage32(1, 1, 32, 32);
        enemyDeadShadow[1] = getEnemyBlood.getImage32(2, 1, 32, 32);
        enemyDeadShadow[2] = getEnemyBlood.getImage32(3, 1, 32, 32);
        enemyDeadShadow[3] = getEnemyBlood.getImage32(4, 1, 32, 32);
        enemyDeadShadow[4] = getEnemyBlood.getImage32(5, 1, 32, 32);
        enemyDeadShadow[5] = getEnemyBlood.getImage32(6, 1, 32, 32);
        enemyDeadShadow[6] = getEnemyBlood.getImage32(7, 1, 32, 32);
    }

    private void loadPlayerIdle() {
        playerIdle[0] = getPlayerIdleCycle.getImage32(1, 1, 32, 32);
        playerIdle[1] = getPlayerIdleCycle.getImage32(2, 1, 32, 32);
        playerIdle[2] = getPlayerIdleCycle.getImage32(3, 1, 32, 32);
        playerIdle[3] = getPlayerIdleCycle.getImage32(4, 1, 32, 32);
    }

    private void loadEnenemySprites() {
        enemy[0] = getEnemySprite.getImage32(1, 1, 32, 32);
        enemy[1] = getEnemySprite.getImage32(2, 1, 32, 32);
        enemy[2] = getEnemySprite.getImage32(3, 1, 32, 32);
        enemy[3] = getEnemySprite.getImage32(4, 1, 32, 32);
        enemy[4] = getEnemySprite.getImage32(5, 1, 32, 32);
        enemy[5] = getEnemySprite.getImage32(6, 1, 32, 32);
        enemy[6] = getEnemySprite.getImage32(7, 1, 32, 32);
        enemy[7] = getEnemySprite.getImage32(8, 1, 32, 32);
        enemy[8] = getEnemySprite.getImage32(9, 1, 32, 32);
        enemy[9] = getEnemySprite.getImage32(10, 1, 32, 32);
        enemy[10] = getEnemySprite.getImage32(11, 1, 32, 32);
        enemy[11] = getEnemySprite.getImage32(12, 1, 32, 32);
        enemy[12] = getEnemySprite.getImage32(13, 1, 32, 32);
    }

    /***
     * play the BackgroundSound
     */
    private void playBackgroundSound() {
        t1 = new Thread(() -> {
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
            } catch (IllegalArgumentException e) {
                //e.printStackTrace();
            }
        });
        t1.start();
    }

    // the main function that runs everything
    public static void main(String[] args) throws IOException {
        try {
            new Game();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}