package Woralcoholics.game;

import javax.swing.*;
import java.awt.*;

public class Window {

    public Window(int with, int height, String title, Game game ){

        JFrame frame = new JFrame(title);

        // the size of the window
        frame.setPreferredSize(new Dimension(with, height));
        frame.setMaximumSize(new Dimension(with,height));
        frame.setMinimumSize(new Dimension(with,height));

        //options of the window
        frame.add(game);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
