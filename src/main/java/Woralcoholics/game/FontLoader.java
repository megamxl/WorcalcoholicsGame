package Woralcoholics.game;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 *  @author Maxlimilian Nowak
 */

public class FontLoader {
    public FontLoader() {
            try {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Resource/Fonts/Masked Hero.ttf")).deriveFont(12f);
                Font customFont1 = Font.createFont(Font.TRUETYPE_FONT, new File("Resource/Fonts/DebugFreeTrial-MVdYB.otf")).deriveFont(12f);

                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                //register the font
                ge.registerFont(customFont);
                ge.registerFont(customFont1);
                System.out.println("done");
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
