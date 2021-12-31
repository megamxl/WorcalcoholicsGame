package Woralcoholics.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class ScoerSaveWindow extends JFrame implements ActionListener {

    public static JFrame frame;
    public static String yehbody;
    private JButton ok = new JButton("ok");
    private JTextField playername = new JTextField("");


    public ScoerSaveWindow(int with, int height, String title) {


        frame = new JFrame(title);

        // the size of the window
        frame.setPreferredSize(new Dimension(with, height));
        frame.setMaximumSize(new Dimension(with, height));
        frame.setMinimumSize(new Dimension(with, height));

        //options of the window
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Window.class.getResource("jar.png")));

        ok.addActionListener(this);

        playername.setPreferredSize(new Dimension(500,300));
        playername.setText("Enter your name");

        this.add(ok);
        this.add(playername);
        frame.pack();

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == ok){
            yehbody = playername.getText();
            System.out.println(yehbody);
            frame.setVisible(false);
            //Window.frame.setVisible(true);
        }


    }
}
