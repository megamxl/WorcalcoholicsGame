package Woralcoholics.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

    private  boolean isRunning;
    private Thread thread;
    private GameManager handler;
    private Animations an;

    private BufferedImage level = null;
    private Camera camera;

    private BufferedImage spritesheet = null;
    private BufferedImage floor = null;

    public Game() throws IOException {
        // make the window threw out own window class
        int playerIndex= 0;
        new Window(1000,563, "Workalcholics Work In Progress",this);
        start();

        handler = new GameManager();
        camera = new Camera(0,0);
        // when finished implement the Mouse and Key input
        InputStream path = this.getClass().getClassLoader().getResourceAsStream("test.png");
        level = ImageIO.read(path);

        BufferedImageLoader loader = new BufferedImageLoader();
        spritesheet =loader.loadImage("/Spritesheet.png");
        an = new Animations(spritesheet);

        loadLevel(level);

        for(int i =0; i <handler.object.size(); i++){
            if (handler.object.get(i).getId() == ID.Player){
                playerIndex= i;
                break;
            }
        }
        MouseInput mouse = new MouseInput(handler, camera, this, handler.object.get(playerIndex), an );
        this.addMouseListener(mouse);

        KeyInput keys = new KeyInput(handler);
        this.addKeyListener(keys);

        floor = an.getImage(2,1,32,32);

    }

    // these tow function are responsible to not make more than one window during runtime
    private  void start(){
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private  void stop(){
        isRunning = false;
        try {
            thread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    // this is a well-known game loop also used in minecraft for making no differnece how fast or slow you computer performce
    // so that the calculation are made at equal tzimes no matter the computer
    public void run(){
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        while(isRunning){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if(delta >= 1){
                update();
                updates++;
                delta--;
            }
            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println(updates + " Ticks, Fps " + frames);
                updates = 0;
                frames = 0;
            }

        }
        stop();
    }

    // in every frame check where player is and update camera position
    public void update(){
        for(int i =0; i <handler.object.size(); i++){
            if (handler.object.get(i).getId() == ID.Player){
                camera.update(handler.object.get(i));
            }
        }

        handler.update();
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;
        // between this it can be drawn to the screen

        // rendering gets executed in the way it is written top down
        for (int i = 0; i < 30*72; i+=32) {
            for (int j = 0; j < 30*72; j+=32) {
                g.drawImage(floor,i,j,null);}}

        g2d.translate(-camera.getX(), -camera.getY());

        handler.render(g);

        g2d.translate(camera.getX(), camera.getY());

        // end of drawing place
        g.dispose();
        bs.show();
    }


    private void loadLevel(BufferedImage image) {
        int h = image.getHeight();
        int w = image.getWidth();

        for (int xx = 0; xx < w; xx++) {
            for (int yy = 0; yy < h; yy++) {
                int pixel = image.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 255) {
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Block,an));
                }
                if (blue == 255 && green == 0) {
                    handler.addObject(new Player(xx * 32, yy * 32, ID.Player, handler, this,an));
                }
                if(green == 255){
                    handler.addObject(new Enemy(xx *32, yy*32, ID.Enemy, handler,an));
                }
                /*
                if(green == 255 && blue == 255)
                    handler.addObject(new Create(xx*32, yy*32, ID.Create));

            }*/
            }
        }
        handler.addObject(new GunnerEnemy(250, 250, ID.GunnerEnemy, handler, an)); //Test Gunner
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