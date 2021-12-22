package Woralcoholics.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
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

    private final double STUDIO_WAIT = System.currentTimeMillis() + 1000;

    protected GameState currentState;
    protected GameState previousState;

    private boolean isRunning;
    protected boolean paused, loaded;
    public static boolean renderOnlyOneTime= true;

    private BufferedImage level = null;
    private BufferedImage spritesheet = null;
    private BufferedImage floor = null;
    private BufferedImage floorDirt1 = null;
    private BufferedImage floorDirt2 = null;
    private BufferedImage floorDirt3 = null;

    public static List<int[]> wallCords = new ArrayList();

    public int ammo = 50;
    public int hp = 100;

    // Classes
    private Thread thread;

    public static GameManager handler;

    private static Animations an;

    private Camera camera;

/* ------------- Constructor for Game Class -------------- */

    public Game() throws IOException {
        currentState = previousState = GameState.STUDIO;
        // make the window threw out own window class
        new Window(SCREEN_WIDTH, SCREEN_HEIGHT, "Workalcoholics Work In Progress", this);
        start();

        handler = new GameManager();
        camera = new Camera(0, 0);
        // when finished implement the Mouse and Key input
        InputStream path = this.getClass().getClassLoader().getResourceAsStream("level01.png");
        level = ImageIO.read(path);

        BufferedImageLoader loader = new BufferedImageLoader();
        spritesheet = loader.loadImage("/Spritesheet.png");
        an = new Animations(spritesheet);

        MouseInput mouse = new MouseInput(handler, camera, this, an);
        this.addMouseListener(mouse);

        KeyInput keys = new KeyInput(handler, this);
        this.addKeyListener(keys);

        floor = an.getImage(1, 2, 64, 64);
        floorDirt1 = an.getImage(2, 2, 64, 64);
        floorDirt2 = an.getImage(3, 2, 64, 64);
        floorDirt3 = an.getImage(4, 2, 64, 64);
        loadMenu();
    }


    @Override
    /***
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
                timer += 1000;
                System.out.println(updates + " Ticks, Fps " + frames);
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
        if(currentState == GameState.STUDIO) {
            double now = System.currentTimeMillis();
            if(now > STUDIO_WAIT) {
                currentState = GameState.TITLE;
            }
        }
        if(currentState != previousState) {     //if there was a state change...
            stateChange();

        }
        if(currentState == GameState.LEVEL && !paused) {    //if we are in level and the game is not paused...
            for (int i = 0; i < handler.object.size(); i++) {
                if (handler.object.get(i).getId() == ID.Player) {
                    camera.update(handler.object.get(i));
                }
            }
            handler.update();
            if(hp == 0) {
                currentState = GameState.GAME_OVER;         //if the player has no HP left, its GAME OVER
            }
        }
    }

    /***
     * The complete Render functions handles UI and the game rendering every frame
     */
    public void render() {
        // prepares the next 3 Frames tho be rendered
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        // assigning the Graphics variables that the BufferStrategy can be used
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        //if we are not in the level, render a menu
        if(currentState != GameState.LEVEL) {
            renderMenu(g);
        }
        else {
            if(renderOnlyOneTime) {
                renderBackground(g);
            }
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
        if(currentState == GameState.LEVEL) {
            if(!loaded) {                   //if no level is loaded, load the level
                Enemy.waves = 1;
                Enemy.enemysAlive = 0;
                loadLevel(level);
                hp = 100;
                ammo = 50;

                //System.out.println(handler.object.size());
            }
            paused = false;                 //level is running and not paused
        }
        else {
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
        g.drawString("Workalcoholics", SCREEN_WIDTH /2, SCREEN_HEIGHT /2);
        g.drawString("presents", SCREEN_WIDTH /2, SCREEN_HEIGHT *3/4);
    }

    /***
     * Instructions to render the title screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderTitle(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("TITLE", SCREEN_WIDTH /2, SCREEN_HEIGHT /2);
        g.drawString("LMB: MAIN MENU", SCREEN_WIDTH /2, SCREEN_HEIGHT *3/4);
    }

    /***
     * Instructions to render the Main Menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderMainMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.drawString("MAIN MENU", SCREEN_WIDTH /2, SCREEN_HEIGHT /2);
        g.drawString("LMB: LEVEL    RMB: OPTIONS", SCREEN_WIDTH /2, SCREEN_HEIGHT *3/4);
    }

    /***
     * Instructions to render the Options screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderOptions(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("OPTIONS", SCREEN_WIDTH /2, SCREEN_HEIGHT /2);
        g.drawString("RMB: MAIN MENU", SCREEN_WIDTH /2, SCREEN_HEIGHT *3/4);
    }

    /***
     * Instructions to render the Pause menu screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderPauseMenu(Graphics g) {
        //g.setColor(new Color(0,0,0,127));
        //g.fillRect(0, 0, screenWidth, screenHeight);
        g.setColor(Color.WHITE);
        g.drawString("PAUSE_MENU", SCREEN_WIDTH /2, SCREEN_HEIGHT /2);
        g.drawString("RMB: LEVEL", SCREEN_WIDTH /2, SCREEN_HEIGHT *3/4);
    }

    /***
     * Instructions to render the Game over screen
     * @param g the current Buffered image as Graphics object
     */
    private void renderGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("GAME OVER", SCREEN_WIDTH /2, SCREEN_HEIGHT /2);
        g.drawString("Press LMB to Start again", SCREEN_WIDTH /2, SCREEN_HEIGHT *3/4);
    }

    /***
     * A switch case functions for determining what screen to render
     * @param g Graphics object
     */
    private void renderMenu(Graphics g) {
        switch(currentState) {
            case STUDIO -> renderStudio(g);
            case TITLE -> renderTitle(g);
            case MAIN_MENU -> renderMainMenu(g);
            case OPTIONS -> renderOptions(g);
            case PAUSE_MENU -> renderPauseMenu(g);
            case GAME_OVER -> renderGameOver(g);
        }
    }

    /***
     * just the instructions on how to render the UI
     * @param g Graphics Object
     */
    private void renderUi(Graphics g){

        g.setColor(Color.gray);
        g.fillRect(5, 5, 200, 16); //hp

        g.fillRect(5, 30, 200, 16); //ammo

        g.setColor(Color.blue);
        g.drawString("AMMO: " + ammo + "/50", 210, 42);
        g.fillRect(5, 30, ammo * 4, 16);

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

        g.setColor(Color.MAGENTA);
        g.drawString("Waves "+ Enemy.waves,930,17);
        g.drawString("Enemies "+ Enemy.enemysAlive,910,40);

    }

    /***
     * as the name states loads all kinds of Menus, which is not the level
     */
    private void loadMenu() {
        //System.out.println(currentState);
        switch(currentState) {
            case STUDIO -> System.out.println("STUDIO");
            case TITLE -> {}
            case MAIN_MENU -> System.out.println("MAIN MENU");
            case OPTIONS -> System.out.println("OPTIONS");
            case PAUSE_MENU -> {
                System.out.println("PAUSE MENU");
                paused = true;                  //we are in PAUSE_MENU, so set paused true
            }
            case GAME_OVER -> {
                System.out.println("GAME OVER");
                loaded = false;                 //the player lost, so the level should unload
            }
        }
        if(!loaded) {   //unload method (clear the object list)
            while(handler.object.size() > 0) {
                System.out.println(handler.object.get(0).getId());
                handler.object.remove(0);
                System.out.println(handler.object.size());
            }

        }
    }

    /***
     * The function to maka a playable level out of a Buffered Image
     * @param image The level Png
     */
    private void loadLevel(BufferedImage image) {
        int h = image.getHeight();
        int w = image.getWidth();
        int i= 0;

        for (int xx = 0; xx < w; xx++) {
            for (int yy = 0; yy < h; yy++) {
                int pixel = image.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 255) {
                    // Creates the new blocks which function as the walls
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block, an, 1, 1));
                    wallCords.add(new int[]{xx,yy});
                }
                if (blue == 255 && green == 0) {
                    handler.addObject(new Player(xx * 32, yy * 32, ID.Player, handler, this, an));
                }
                if (green == 255) {
                    handler.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy, handler, an));
                }
                /*
                if(green == 255 && blue == 255)
                    handler.addObject(new Create(xx*32, yy*32, ID.Create));

            }*/
            }
        }
        //handler.addObject(new GunnerEnemy(500, 500, ID.GunnerEnemy, handler, an)); //Test Gunner
        loaded = true;
    }

    /***
     * This function always renders the background
     * @param g Graphics object
     */
    private void renderBackground(Graphics g){
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
    public Integer randomNumber(int start, int end)
    {
        return new Random().ints(start, end).findFirst().getAsInt();
    }

    /***
     * A function inside the game calls to spawn the enemy's. it is static that i can be called in other classes
     * @param x X value
     * @param y Y value
     */
    public static void SpawnEnemy(int x, int y){
        handler.addObject(new Enemy(x, y, ID.Enemy, handler, an));
    }

    // the main function that runs everything
    public static void main(String[] args) throws IOException {
        try {
            new Game();
        } catch (IOException e) {
            e.printStackTrace();
            new Game();
        }
    }
}