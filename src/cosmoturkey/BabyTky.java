package cosmoturkey;

import java.awt.*;
import javax.swing.*;
import image.*;
import java.awt.image.*;

public class BabyTky {
    
    private boolean visible;
    private int x;
    private int y;
    private Image babyTky;
    private String tkyImageFile = "images/turkey2.gif";
    
    public BabyTky (int x, int y) {
        this.x = x;
        this.y = y;
        ImageIcon i = new ImageIcon(tkyImageFile);
        ImageConverter ic = new ImageConverter();
        ImageTransparency it = new ImageTransparency();
        BufferedImage bi = ic.imageToBufferedImage(i.getImage());
        babyTky = it.makeColorTransparent(bi, Color.white);
        visible = true;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public Image getImage() {
        
        return babyTky;
    }
    
    public Rectangle makeRectangle() {
        return new Rectangle(x, y, babyTky.getWidth(null), babyTky.getHeight(null));
    }
    
    public void setVisible(boolean vState) {
        visible = vState;
    }
    
    public boolean isVisible() {
        return visible;
    }
}