package Woralcoholics.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window {
    
    public static JFrame frame =null;
    public static JFrame GameOverFrame =null;

    private static JLabel txtUser;

    private static JLabel l1, l2, l3;
    private static JTextField tf1;
    private static JButton btn1;
    private static JPasswordField p1;

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

        //frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Window.class.getResource("jar.png")));

    }
}