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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Canvas implements Runnable {
    /* ------------ Local variables for main game ------------ */
    private static final long serialVersionUID = 1L;

    // Variables
    final int SCREEN_WIDTH = 1024;
    final int SCREEN_HEIGHT = 576;
    //public static Window window;

    private static GameState currentState;
    private GameState previousState, checkState;
    private int menuCount;
    public static int lastScore = 0;

    private boolean isRunning;
    protected static boolean paused, loaded;
    public static boolean inTutorial = false;
    public static boolean shouldTime = false;
    public static boolean spawn = false;

    private BufferedImage level = null;
    private BufferedImage tutorialLevel = null;
    private BufferedImage spritesheet = null;
    private BufferedImage playerWalkCycle = null;
    private BufferedImage enemyBlood = null;
    private BufferedImage upgradBoarder = null;
    private BufferedImage tutorialBoarder = null;
    private BufferedImage upgradBoard = null;
    private BufferedImage uiButtonAn = null;
    private BufferedImage floor = null;
    private BufferedImage floorDirt1 = null;
    private BufferedImage floorDirt2 = null;
    private BufferedImage floorDirt3 = null;
    private BufferedImage imgOver = null;
    private BufferedImage imgTitle = null;
    private BufferedImage currGun = null;
    private Upgrades upgrades;

    public static BufferedImage[] playerWalkingLeft = new BufferedImage[10];
    public static BufferedImage[] playerWalkingRight = new BufferedImage[10];
    public static BufferedImage[] enemyDeadShadow = new BufferedImage[10];

    private String[][] tutorialTexts = new String[2][2];

    public static List<int[]> wallCords = new ArrayList();

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
    public static boolean isDead = false;
    private boolean wasstopped = false;
    private boolean triggeredonce = false;


    // Classes
    private Thread thread;

    public static GameManager handler;

    private static ImageGetter imageGetter;
    private static ImageGetter upgradeBoarderGet;
    private static ImageGetter getImagesPlayer;
    private static ImageGetter getImagesEnemy;
    private static ImageGetter GamoverScreenImg;
    private static ImageGetter gettutorialBorder;
    private static ImageGetter TitleScreenImg;
    private static ImageGetter uiButtonAnGet;
    private static Gun gun;

    private DatabeseConection databeseConection = new DatabeseConection();

    private Camera camera;
    private double percent;
    private int index;

    Random r = new Random();
    Thread t1;

    MouseInput mouse;

    /* ------------- Constructor for Game Class -------------- */

    public Game() throws IOException, SQLException {
        handler = new GameManager();
        currentState = checkState = GameState.STUDIO;    //initialize the currentState to STUDIO
        // make the window threw out own window class
        //new ScoerSaveWindow(SCREEN_WIDTH,SCREEN_HEIGHT,"");
        //window =
        new Window(SCREEN_WIDTH, SCREEN_HEIGHT, "Workalcoholics Work In Progress", this);
        start();
        gun = new Gun();

        levelDecision = String.valueOf(randomNumber(1, 6));
        camera = new Camera(0, 0, this);

        addGuns();
        checkSelectedGun();

        // when finished implement the Mouse and Key input
        InputStream path = this.getClass().getClassLoader().getResourceAsStream("Levels/level0" + levelDecision + ".png");
        InputStream pathToTutorial = this.getClass().getClassLoader().getResourceAsStream("Levels/tutorial.png");
        level = ImageIO.read(path);
        tutorialLevel = ImageIO.read(pathToTutorial);

        BufferedImageLoader loader = new BufferedImageLoader();
        spritesheet = loader.loadImage("/Graphics/Spritesheet.png");
        imageGetter = new ImageGetter(spritesheet);

        upgradBoarder = loader.loadImage("/Graphics/UpgradeBorder.png");
        upgradeBoarderGet = new ImageGetter(upgradBoarder);

        uiButtonAn = loader.loadImage("/Graphics/UIButton_352x102 NEW.png");
        uiButtonAnGet = new ImageGetter(uiButtonAn);

        playerWalkCycle = loader.loadImage("/Graphics/Animations/Character Running Spritesheet.png");
        getImagesPlayer = new ImageGetter(playerWalkCycle);

        enemyBlood = loader.loadImage("/Graphics/Animations/Bloodparticle.png");
        getImagesEnemy = new ImageGetter(enemyBlood);

        BufferedImage GamoverScreen = loader.loadImage("/Graphics/gameOverPicture.png");
        GamoverScreenImg = new ImageGetter(GamoverScreen);
        imgOver = GamoverScreen.getSubimage(1, 1, 720, 480);

        BufferedImage tutorial = loader.loadImage("/Graphics/TutorialBorder.png");
        gettutorialBorder = new ImageGetter(tutorial);
        tutorialBoarder = gettutorialBorder.getImage(1, 1, SCREEN_WIDTH - 2, SCREEN_HEIGHT - 2);

        imgTitle = loader.loadImage("/Graphics/Titlescreen.png");


        //Adding Mouse and Keyboard Input
        mouse = new MouseInput(handler, camera, this, imageGetter, gun);
        this.addMouseListener(mouse);
        this.addMouseWheelListener(mouse);
        KeyInput keys = new KeyInput(handler, this);
        this.addKeyListener(keys);

        floor = imageGetter.getImage(1, 2, 64, 64);
        floorDirt1 = imageGetter.getImage(2, 2, 64, 64);
        floorDirt2 = imageGetter.getImage(3, 2, 64, 64);
        floorDirt3 = imageGetter.getImage(4, 2, 64, 64);
        loadMenu();
        //activate the timer, to show the Studio for 1 sec
        TimerValue = 0;
        shouldTime = true;
        timerAction = 3;
        this.upgrades = new Upgrades(this); //use upgrades.method for upgrade changes in Game and Player class

        loadPlayerSprites();
        loadEnemyDeadSprites();


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
            // checks if mouse exits the game window during level and corrects its position
            if (getState() == GameState.LEVEL || getState() == GameState.TUTORIAL) {
                if (mouse != null) mouse.checkIfExited(MouseInfo.getPointerInfo().getLocation());
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
        calculateReloadingRectangle(handler.wait, (int) handler.del);

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
        Graphics g = bs.getDrawGraphics();
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
        g.setFont(new Font("SansSerif", Font.PLAIN, 30));
        //tutorialTexts[0][0]= "dasdadsadadsadadadasdsadadaadasdsdas";
        tutorialTexts[0][0] = "Willkommen zu Workalcoholics Game";
        tutorialTexts[0][1] = "um mehr Text zu sehen drücke Space";

        tutorialTexts[1][0] = "Bewegen kannst du dich durch ";
        tutorialTexts[1][1] = "w = up, s = down, a = links, d = rechts";

        if (curentTutorialscore < tutorialTexts.length) {
            g.drawString(tutorialTexts[curentTutorialscore][0], 262, 465);
            g.drawString(tutorialTexts[curentTutorialscore][1], 262, 495);
        } else {
            curentTutorialscore = 0;
        }
    }


    private void stateChange() throws SQLException {
        //System.out.println("is " + currentState + " equal to " + checkState + "?");
        previousState = checkState;      //save the state before the state change
        checkState = currentState;       //the checkState becomes the current state, to again detect a state change
        //System.out.println(previousState + " -> " + currentState + ", check current against: " + checkState);
        if (!loaded) {   //if nothing is loaded...
            handler.clearHandler(); //clear everything in the handler
        }
        if (currentState == GameState.LEVEL || currentState == GameState.TUTORIAL) {   //if we have changed to LEVEL...
            if (!loaded) {                   //...if no level is loaded, load the level
                Enemy.waves = 1;            //reset Enemy waves
                Enemy.enemysAlive = 0;
                hp = 100;                   //reset player specific values
                ammo = 50;                  // max hp = 100, max ammo = 50, max shield = 40, max armor = ...
                shield = 0;
                armor = 0;
                camera.shake = false;   //camera should not shake
                switch (currentState) {
                    case LEVEL -> loadLevel(level); //load the level
                    case TUTORIAL -> {
                        loadLevel(tutorialLevel);
                        inTutorial = true;
                    }
                }
            }
            handler.clearObjects(ID.UIButton);      //clear the handler from all buttons, when we are in the level
            paused = false;  //level is running and not paused (when coming from e.g. PAUSE_MENU or UPGRADE_MENU, where a level is already loaded)
        } else {
            loadMenu();                     //load the menu of currentState
        }
        System.out.println(currentState + ": " + handler.object.size());
    }

    /***
     * Instructions to render the studio screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderStudio(Graphics g) {
    }

    /***
     * Instructions to render the title screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderTitle(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.drawImage(imgTitle, 0, 0, null);
    }

    /***
     * Instructions to render the Main Menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderMainMenu(Graphics g) {

    }

    /***
     * Instructions to render the Main Menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderTutorial(Graphics g) {
    }

    /***
     * Instructions to render the Main Menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderHighScores(Graphics g) {
        g.setColor(Color.black);
        g.setFont(new Font("Masked Hero Demo", Font.PLAIN, 40));
        g.drawString("Higscores from Databse", 100, 100);
        g.setFont(new Font("Arial Black", Font.PLAIN, 40));
        if (DatabeseConection.finishedFillingArray) {
            g.setColor(Color.black);
            g.drawString(DatabeseConection.scoresArray[0], 300, 210);
            g.drawString(DatabeseConection.scoresArray[1], 300, 250);
            g.drawString(DatabeseConection.scoresArray[2], 300, 290);
            g.drawString(DatabeseConection.scoresArray[3], 300, 330);
            g.drawString(DatabeseConection.scoresArray[4], 300, 370);
        }
    }

    /***
     * Instructions to render the Options screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderOptions(Graphics g) {
    }

    /***
     * Instructions to render the Pause menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderPauseMenu(Graphics g) {

    }

    /***
     * Instructions to render the Upgrade Menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderUpgradeMenu(Graphics g) {
        //g.setColor(Color(0, 0, 0, 127));
        //g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        /*upgradBoard = upgradeBoarderGet.getImage(1, 1, 320, 600);
        g.drawImage(upgradBoard, 137, 30, null);
        g.drawImage(upgradBoard, 377, 30, null);
        g.drawImage(upgradBoard, 617, 30, null);
        g.setColor(Color.WHITE);
        int[] randomUpgrades = upgrades.getUpgrades();
        g.drawString(upgrades.drawUpgrades(randomUpgrades[0]), 300, 315);
        g.drawString(upgrades.drawUpgrades(randomUpgrades[1]), 500, 315);
        g.drawString(upgrades.drawUpgrades(randomUpgrades[2]), 700, 315);*/
    }

    /***
     * Instructions to render the Game over screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderGameOver(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        //g.drawString("GAME OVER", 200, 200);
        g.setFont(new Font("DEBUG FREE TRIAL", Font.PLAIN, 75));
        g.drawImage(imgOver, 1, 1, null);
        g.drawString("your Score is " + lastScore, 300, SCREEN_HEIGHT - 65);
        //g.drawString("Press LMB to Start again", SCREEN_WIDTH / 2, SCREEN_HEIGHT * 3 / 4);
    }

    /***
     * A switch case functions for determining what screen to render
     * @param g Graphics object
     */
    private void renderScreen(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        switch (currentState) {
            case STUDIO -> renderStudio(g);
            case TITLE -> renderTitle(g);
            case MAIN_MENU -> renderMainMenu(g);
            case TUTORIAL -> renderTutorial(g);
            case HIGH_SCORES -> renderHighScores(g);
            case OPTIONS -> renderOptions(g);
            case PAUSE_MENU -> renderPauseMenu(g);
            case UPGRADE_MENU -> renderUpgradeMenu(g);
            case GAME_OVER -> renderGameOver(g);
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
            g.drawString("Next Wave spawns in " + TimerValue, 50, 250);

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
                        "START", GameState.MAIN_MENU, ID.UIButton, this, 1, 0,
                        uiButtonAnGet, 1, 1, 400, (SCREEN_HEIGHT + 350) / 2 + 17, 40));
            }
            case MAIN_MENU -> {
                menuCount = 0;
                //JMenu mainMenu = new JMenu("Main Menu");
                //mainMenu.add(new JMenuItem("test"));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, 70, 352, 102, "Level",
                        GameState.LEVEL, ID.UIButton, this, 1, 0, uiButtonAnGet, 1, 1,
                        410, 90, 40));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, 195, 352, 102, "Tutorial",
                        GameState.TUTORIAL, ID.UIButton, this, 1, 0, uiButtonAnGet, 1, 1,
                        390, 210, 30));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, 320, 352, 102, "Scores",
                        GameState.HIGH_SCORES, ID.UIButton, this, 1, 0, uiButtonAnGet, 1,
                        1, 397, 337, 35));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, 445, 352, 102, "Credits",
                        GameState.CREDITS, ID.UIButton, this, 1, 0, uiButtonAnGet, 1, 1,
                        400, 460, 32));
                handler.addObject(new UIButton(SCREEN_WIDTH - 46, 34, 64, 64,
                        "Options", GameState.OPTIONS, ID.UIButton, this, 1, 0, imageGetter,
                        2, 6, 0, 0, 0));

            }
            case CREDITS -> {
                handler.addObject(new UIButton(32, 32, 64, 64, "Return", RETURN,
                        ID.UIButton, this, 1, 0, imageGetter, 1, 2, 0, 0,
                        40));
            }
            case TUTORIAL -> {
                //handler.addObject(new UIButton(10, 10, 64, 64, "Return", RETURN, ID.UIButton, this, an, 0, 0, 40));
                //System.out.println(currentState.toString());
            }
            case HIGH_SCORES -> {

                databeseConection.ReadFromDatabse();

                //JOptionPane playerDataInput = new JOptionPane();
                handler.addObject(new UIButton(32, 32, 64, 64, "Return", RETURN,
                        ID.UIButton, this, 1, 0, imageGetter, 1, 2, 0, 0,
                        40));
            }
            case OPTIONS -> {
                menuCount++;
                handler.addObject(new UIButton(32, 32, 64, 64, "Return", RETURN,
                        ID.UIButton, this, 1, 0, imageGetter, 1, 6, 0, 0,
                        40));
            }
            case PAUSE_MENU -> {
                menuCount = 10;
                handler.addObject(new UIButton(32, 32, 64, 64, "Return", GameState.LEVEL,
                        ID.UIButton, this, 1, 0, imageGetter, 1, 6, 0, 0,
                        40));
                handler.addObject(new UIButton(96, 32, 64, 64, "Options", GameState.OPTIONS,
                        ID.UIButton, this, 1, 0, imageGetter, 2, 6, 0, 0,
                        40));
                paused = true;                  //we are in PAUSE_MENU, so set paused true
            }
            case UPGRADE_MENU -> {
                int[] randomUpgrades = upgrades.getUpgrades();
                handler.addObject(new UIButton(SCREEN_WIDTH / 4, (SCREEN_HEIGHT + 25) / 2, 320, 600,
                        upgrades.drawUpgrades(randomUpgrades[0]), GameState.LEVEL, ID.UIButton, this, 2,
                        randomUpgrades[0], upgradeBoarderGet, 1, 1, SCREEN_WIDTH / 4,
                        (SCREEN_HEIGHT + 25) / 2, 20));
                handler.addObject(new UIButton(SCREEN_WIDTH / 2, (SCREEN_HEIGHT + 25) / 2, 320, 600,
                        upgrades.drawUpgrades(randomUpgrades[1]), GameState.LEVEL, ID.UIButton, this, 2,
                        randomUpgrades[1], upgradeBoarderGet, 1, 1, SCREEN_WIDTH / 2,
                        (SCREEN_HEIGHT + 25) / 2, 20));
                handler.addObject(new UIButton(SCREEN_WIDTH * 3 / 4, (SCREEN_HEIGHT + 25) / 2, 320, 600,
                        upgrades.drawUpgrades(randomUpgrades[2]), GameState.LEVEL, ID.UIButton, this, 2,
                        randomUpgrades[2], upgradeBoarderGet, 1, 1, SCREEN_WIDTH * 3 / 4,
                        (SCREEN_HEIGHT + 25) / 2, 20));
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
                //ScoerSaveWindow.frame.setVisible(true);
                //Window.frame.setVisible(false);
                //System.out.println("SOUND CLOSE");
                System.out.println(playerName);
                Game.TimerValue = 0;    //5 secs wait time
                Game.shouldTime = true; //activate Timer
                Game.timerAction = 4;   //execute timerAction 4 -> enter name and upload score dialogs
            }
        }
    }


    private void fontLoader() {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Resource/Fonts/Masked Hero.ttf")).deriveFont(12f);
            Font customFont1 = Font.createFont(Font.TRUETYPE_FONT, new File("Resource/Fonts/DebugFreeTrial-MVdYB.otf")).deriveFont(12f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(customFont);
            ge.registerFont(customFont1);
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
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block, imageGetter, randomNumber(1, 7), 1));
                    wallCords.add(new int[]{xx, yy});
                }
                /*
                if (red == 155) {
                    // Creates the new blocks which function as the walls
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block, imageGetter, randomNumber(5, 7), 2));
                    wallCords.add(new int[]{xx, yy});
                }
                 */
                if (blue == 255 && green == 0 && red == 0) {
                    handler.addObject(new Player(xx * 32, yy * 32, ID.Player, handler, this, camera, imageGetter));
                    PlayerX = xx * 32;
                    PlayerY = yy * 32;
                }
                if (green == 255) {
                    handler.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy, handler, imageGetter, score));
                }
                if (green == 255 && blue == 255) {

                }
            }
        }
        handler.addObject(new GunnerEnemy(500, 500, ID.GunnerEnemy, handler, imageGetter, score)); //Test Gunner
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
        } else {   //if the waiting time is over...
            if (shouldTime) {    //...execute the previously set timerAction
                switch (timerAction) {
                    case 1 -> {
                        Enemy.Spawner(Enemy.waves, false, r); //Spawn the next wave of enemies
                        //upgrades.addMunition(20);
                    }
                    case 2 -> currentState = GameState.UPGRADE_MENU; //change state to UPGRADE_MENU (because of rendering)
                    case 3 -> currentState = GameState.TITLE;    //change state to TITLE (from STUDIO, 1 sec wait time)
                    case 4 -> { //enter your name and choose whether to upload your score
                        playerName = JOptionPane.showInputDialog(null, "Please enter your name",
                                null, JOptionPane.INFORMATION_MESSAGE);
                        JOptionPane.showConfirmDialog(null, "Do you want to upload your score to the cloud?",
                                null, JOptionPane.YES_NO_OPTION);
                        databeseConection.insertSoreAndNameInToDatabase();
                        System.out.println("over Con");
                    }
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
    public static Integer randomNumber(int start, int end) {
        return new Random().ints(start, end).findFirst().getAsInt();
    }

    /***
     * A function inside the game calls to spawn the enemy's. it is static that i can be called in other classes
     * @param x X value
     * @param y Y value
     */
    public static void SpawnEnemy(int x, int y) {
        handler.addObject(new Enemy(x, y, ID.Enemy, handler, imageGetter, score));
    }

    public static void SpawnGunnerEnemy() {
        handler.addObject(new GunnerEnemy(500, 500, ID.Enemy, handler, imageGetter, score));
    }

    public static void SpawnCreate(int x, int y) {
        handler.addObject(new Crate(x, y, ID.Create, imageGetter));
    }

    public static void AddEnemyShadow(int x, int y) {
        handler.addObject(new EnemyShadow(x, y, ID.EnemyShadow, imageGetter));
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
     * Function to run backgroundsound
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

    private void calculateReloadingRectangle(double wait, int del) {

        if (handler.reloaded == true) {
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

    private void checkReloaded() {
        handler.now = System.currentTimeMillis();
        if (handler.now > handler.wait && handler.ammo == true) {
            handler.reloaded = true;
        } else {
            handler.reloaded = false;
        }
    }

    private void addGuns() {
        gun.addObject(new Gun(), GunType.Pistol, false); // start with pistol
        gun.addObject(new Gun(), GunType.Shotgun, true); // second shotgun
        gun.addObject(new Gun(), GunType.MachineGun, true); //third machine gun  -> weakest to strongest
        // if crate is collected, set locked to false so it can be displayed and choosen in UI
    }

    private void checkSelectedGun() {
        handler.selectedgun = gun.guns.get(handler.gunindex);
    }

    private void checkGunStatus() {
        if (handler.selectedgun.getType() == GunType.Pistol)
            handler.del = 200;
        else if (handler.selectedgun.getType() == GunType.Shotgun)
            handler.del = 1000;
        else //Machine Gun
            handler.del = 0;
    }

    private void updateLockStatus() {
        if (Enemy.waves == 2) { //for test purposes on wave 2
            index = gun.getIndex(GunType.Shotgun);
            gun.manipulteList(index, new Gun(), GunType.Shotgun, false);
        } else if (Enemy.waves == 3) { //for test purposes on wave 3
            index = gun.getIndex(GunType.MachineGun);
            gun.manipulteList(index, new Gun(), GunType.MachineGun, false);
        }
    }


    private void loadPlayerSprites() {
        playerWalkingLeft[0] = getImagesPlayer.getImage32(1, 1, 32, 32);
        playerWalkingLeft[1] = getImagesPlayer.getImage32(2, 1, 32, 32);
        playerWalkingLeft[2] = getImagesPlayer.getImage32(3, 1, 32, 32);
        playerWalkingLeft[3] = getImagesPlayer.getImage32(4, 1, 32, 32);
        playerWalkingLeft[4] = getImagesPlayer.getImage32(5, 1, 32, 32);
        playerWalkingLeft[5] = getImagesPlayer.getImage32(6, 1, 32, 32);
        playerWalkingLeft[6] = getImagesPlayer.getImage32(7, 1, 32, 32);
        playerWalkingLeft[7] = getImagesPlayer.getImage32(8, 1, 32, 32);
        playerWalkingLeft[8] = getImagesPlayer.getImage32(9, 1, 32, 32);
        playerWalkingLeft[9] = getImagesPlayer.getImage32(10, 1, 32, 32);


        for (int i = 0; i < playerWalkingLeft.length; i++) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-playerWalkingLeft[i].getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            playerWalkingRight[i] = op.filter(playerWalkingLeft[i], null);

        }
    }

    private void loadEnemyDeadSprites() {
        enemyDeadShadow[0] = getImagesEnemy.getImage32(1, 1, 32, 32);
        enemyDeadShadow[1] = getImagesEnemy.getImage32(2, 1, 32, 32);
        enemyDeadShadow[2] = getImagesEnemy.getImage32(3, 1, 32, 32);
        enemyDeadShadow[3] = getImagesEnemy.getImage32(4, 1, 32, 32);
        enemyDeadShadow[4] = getImagesEnemy.getImage32(5, 1, 32, 32);
        enemyDeadShadow[5] = getImagesEnemy.getImage32(6, 1, 32, 32);
        enemyDeadShadow[6] = getImagesEnemy.getImage32(7, 1, 32, 32);
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