package cosmoturkey;

import java.awt.*;
import javax.swing.*;
import image.*;
import java.awt.image.BufferedImage;
import screen.Dim;

public class BlackCloud {
    
    private String blackCloudFile = "images/blackcloud.gif";
    private Image blackCloud;
    private double shotFPS;
    private Toolkit tk;
    private Dimension screen;
    private int x;
    private int y;
    private int direction;
    private boolean visible;
    private int winWidth;
    private int winHeight;
    Dim dim;
    
    BlackCloud(int winWidth, int winHeight) {
        dim = new Dim(winWidth, winHeight, 1000, 700);
        ImageIcon i = new ImageIcon(blackCloudFile);
        ImageConverter ic = new ImageConverter();
        ImageTransparency it = new ImageTransparency();
        BufferedImage bi = ic.imageToBufferedImage(i.getImage());
        blackCloud = it.makeColorTransparent(bi, Color.white);
        this.winWidth = winWidth;
        this.winHeight = winHeight;
        firstPosition();
            shotFPS = dim.adjWDouble(4);
    }

    
    public int getRandomX() {
        int randX = (int) Math.floor(Math.random() * (winWidth - blackCloud.getWidth(null)) + dim.adjW(60));
        return randX;
    }
    
    public int getRandomY() {
        int randY = (int) Math.floor(Math.random() * winHeight + 1);
        return randY;
    }
    
    public void setVisible(boolean vState) {
        visible = vState;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public Image getImage() {
        return blackCloud;
    }
    
    public Rectangle makeRectangle() {
        return new Rectangle((int) Math.round(x + .1 * blackCloud.getWidth(null)), (int) Math.round(y + .2 * blackCloud.getHeight(null)), (int) Math.round(.7 * blackCloud.getWidth(null)), (int) Math.round(.65 * blackCloud.getHeight(null)));
    }
    
    public void putAtTop() {
        x = getRandomX();
        y = dim.adjH(25);
        direction = (int) Math.floor(Math.random() * 3 + 1);
        visible = true;
    }
    
    public void firstPosition() {
        x = getRandomX();
        y = getRandomY();
        direction = (int) Math.floor(Math.random() * 3 + 1);
        visible = true;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void move() {
        if (y > winHeight) {
            putAtTop();
        }
        if (direction == 1) {
            x -= shotFPS;
            y += shotFPS;
        } else if (direction == 2) {
            y += shotFPS;
        } else if (direction == 3) {
            x += shotFPS;
            y += shotFPS;
        }
    }
}