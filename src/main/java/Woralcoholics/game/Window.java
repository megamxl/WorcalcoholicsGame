package Woralcoholics.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Window {

    /**
     *  @author Maxlimilian Nowak
     */

    public static JFrame frame =null;

    private BufferedImage icon = null;

    /***
     * The Window creation class
     * @param with Window with
     * @param height Window height
     * @param title Window Title
     * @param game The Class which
     */

    public Window(int with, int height, String title, Game game) {

        frame = new JFrame(title);

        // the size of the window
        frame.setPreferredSize(new Dimension(with, height));
        frame.setMaximumSize(new Dimension(with, height));
        frame.setMinimumSize(new Dimension(with, height));

        //options of the window
        frame.add(game);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            BufferedImageLoader loader = new BufferedImageLoader();
            icon= loader.loadImage("/Graphics/Icon (1).png");
            frame.setIconImage((icon));
        }catch (Exception e){
            System.out.println("Icon was not cloned");
        }
    }
}