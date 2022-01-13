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
                // get the Font files as Java object fonts
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Resource/Fonts/Masked Hero.ttf")).deriveFont(12f);
                Font customFont1 = Font.createFont(Font.TRUETYPE_FONT, new File("Resource/Fonts/DebugFreeTrial-MVdYB.otf")).deriveFont(12f);
                Font customFont2 = Font.createFont(Font.TRUETYPE_FONT, new File("Resource/Fonts/Cyberpunk-Regular.ttf")).deriveFont(12f);

                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();     // making a GraphicEnviorment to acceses the systems Fonts

                //register the font in the current program that it can be used with just the name in Rendering
                ge.registerFont(customFont);
                ge.registerFont(customFont1);
                ge.registerFont(customFont2);
                System.out.println("done");
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}