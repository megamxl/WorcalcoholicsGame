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

    private static final long serialVersionUID = 1L;

    final int screenWidth = 1000;
    final int screenHeight = 563;
    protected enum game_state {
        TITLE,
        MAIN_MENU,
        OPTIONS,
        PAUSE_MENU,
        LEVEL
    }
    game_state state;

    private boolean isRunning;
    private Thread thread;
    public static GameManager handler;
    private static Animations an;
    static List<int[]> wallCords = new ArrayList();

    private BufferedImage level = null;
    private Camera camera;

    private BufferedImage spritesheet = null;
    private BufferedImage floor = null;
    private BufferedImage floorDirt1 = null;
    private BufferedImage floorDirt2 = null;
    private BufferedImage floorDirt3 = null;

    public static boolean renderOnlyOneTime= true;

    public int ammo = 50;
    public int hp = 100;

    public Game() throws IOException {
        state = game_state.TITLE;
        // make the window threw out own window class
        int playerIndex = 0;
        new Window(screenWidth, screenHeight, "Workalcholics Work In Progress", this);
        start();

        handler = new GameManager();
        camera = new Camera(0, 0);
        // when finished implement the Mouse and Key input
        InputStream path = this.getClass().getClassLoader().getResourceAsStream("level01.png");
        level = ImageIO.read(path);

        BufferedImageLoader loader = new BufferedImageLoader();
        spritesheet = loader.loadImage("/Spritesheet.png");
        an = new Animations(spritesheet);

        loadLevel(level);

        for (int i = 0; i < handler.object.size(); i++) {
            if (handler.object.get(i).getId() == ID.Player) {
                playerIndex = i;
                break;
            }
        }
        MouseInput mouse = new MouseInput(handler, camera, this, handler.object.get(playerIndex), an);
        this.addMouseListener(mouse);

        KeyInput keys = new KeyInput(handler);
        this.addKeyListener(keys);

        floor = an.getImage(1, 2, 64, 64);
        floorDirt1 = an.getImage(2, 2, 64, 64);
        floorDirt2 = an.getImage(3, 2, 64, 64);
        floorDirt3 = an.getImage(4, 2, 64, 64);

    }

    // these tow function are responsible to not make more than one window during runtime
    private void start() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    // this is a well-known game loop also used in minecraft for making no differnece how fast or slow you computer performce
    // so that the calculation are made at equal tzimes no matter the computer
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

    // in every frame check where player is and update camera position
    public void update() {
        switch(state) {
            case MAIN_MENU -> System.out.println("MAIN MENU");
            case OPTIONS -> System.out.println("OPTIONS");
            case PAUSE_MENU -> System.out.println("PAUSE MENU");
            case LEVEL -> {
                for (int i = 0; i < handler.object.size(); i++) {
                    if (handler.object.get(i).getId() == ID.Player) {
                        camera.update(handler.object.get(i));
                    }
                }
                handler.update();
            }

        }
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        switch (state) {
            case TITLE -> {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, screenWidth, screenHeight);
            }
            case LEVEL -> {
                if(renderOnlyOneTime) {
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
                g2d.translate(-camera.getX(), -camera.getY());

                handler.render(g);

                g2d.translate(camera.getX(), camera.getY());

                renderUi(g);
            }
        }
        // between this it can be drawn to the screen

        // rendering gets executed in the way it is written top down

        // end of drawing place
        g.dispose();
        bs.show();
    }

    public void renderUi(Graphics g){

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
        g.drawString("Enemeys "+ Enemy.enemysAlive,910,40);

    }


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
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block, an/*, 1, 1*/));
                    wallCords.add(new int[]{xx,yy});
/*
                  *//*  *//**//*
                    Randomly selects dirt and highlight textures, which then get added on top of the walls
                    *//*
                    //Dirt
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block, an*//*, randomNumber(2, 5), 1)*//*));
                    wallCords.add(new int[]{xx,yy});
                    //Highlights (the end of randomNumber is 10, so that there is a possibility that no highlight
                    //gets added --> more visual diversity
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block, an*//*, randomNumber(5, 10), 1)*//*));
                    wallCords.add(new int[]{xx,yy});*/
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
    }

    /*
    Returns a random number between inclusive start and exclusive end.
     */
    public Integer randomNumber(int start, int end)
    {
        return new Random().ints(start, end).findFirst().getAsInt();
    }

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