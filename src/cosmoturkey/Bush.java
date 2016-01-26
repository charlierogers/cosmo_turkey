package cosmoturkey;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import image.*;

public class Bush {

    private int x;
    private int y;
    private int bushRangeX;
    private int bushRangeY;
    private Image image;
    private Image bushImage;
    private Image explosion;
    private String bushFile = "images/bush2.gif";
    private String explosionFile = "images/explosion.gif";
    private boolean visible;
    private Timer explosionTimer;
    private Timer unExplosionTimer;
    private Explode explode;
    private UnExplode unExplode;
    private boolean exploding;
//    Dim dim;

    public Bush(int winWidth, int winHeight) {
//        dim = new Dim(winWidth, winHeight);
        ImageIcon i = new ImageIcon(bushFile);
        ImageIcon i2 = new ImageIcon(explosionFile);
        ImageConverter ic = new ImageConverter();
        ImageTransparency it = new ImageTransparency();
        BufferedImage bi = ic.imageToBufferedImage(i.getImage());
        bushImage = it.makeColorTransparent(bi, Color.white);
        bi = ic.imageToBufferedImage(i2.getImage());
        explosion = it.makeColorTransparent(bi, Color.white);
        image = bushImage;
        bushRangeY = winHeight - 175;
        bushRangeX = winWidth - 250;
        getRandomStartPosition();
        getRandomExplosionTime();
        exploding = false;
        explosionTimer = new Timer();
        unExplosionTimer = new Timer();
        explode = new Explode();
        unExplode = new UnExplode();
        explosionTimer.schedule(explode, getRandomExplosionTime());
        visible = true;
    }

    public void getRandomStartPosition() {
        x = (int) Math.floor(Math.random() * bushRangeX + 60);
        y = (int) Math.floor(Math.random() * bushRangeY + 60);
    }
    
    public long getRandomExplosionTime() {
        return (long) Math.floor(Math.random() * 30000 + 5000);
    }
    
    public void checkForExplosion() {
        if (explode.isExploding) {
            exploding = true;
            explode.setExploding(false);
            image = explosion;
            explosionTimer.cancel();
            explosionTimer.purge();
            unExplosionTimer = new Timer();
            unExplode = new UnExplode();
            unExplosionTimer.schedule(unExplode, 750);
        } else if (!unExplode.isUnExploding()) {
            exploding = false;
            unExplode.setExploding(true);
            image = bushImage;
            unExplosionTimer.cancel();
            unExplosionTimer.purge();
            explosionTimer = new Timer();
            explode = new Explode();
            explosionTimer.schedule(explode, getRandomExplosionTime());
        }
    }
    
    public boolean isExploding() {
        boolean state = exploding;
        if (exploding) {
            exploding = false;
        }
        return state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return bushImage.getWidth(null);
    }

    public int getHeight() {
        return bushImage.getHeight(null);
    }

    public Image getImage() {     
        checkForExplosion();
        return image;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean vState) {
        visible = vState;
    }

    public Rectangle makeRectangle() {
        return new Rectangle(x, y, bushImage.getWidth(null), bushImage.getHeight(null));
    }
    
    private class Explode extends TimerTask {
        private boolean isExploding;
        
        Explode() {
            isExploding = false;
        }
        
        @Override
        public void run() {
            isExploding = true;
        }
        
        public boolean isExploding() {
            return isExploding;
        }
        
        public void setExploding(boolean explodeState) {
            isExploding = explodeState;
        }
    }
    
    private class UnExplode extends TimerTask {
        private boolean isExploding;
        
        UnExplode() {
            isExploding = true;
        }
        
        @Override
        public void run() {
            isExploding = false;
        }
        
        public boolean isUnExploding() {
            return isExploding;
        }
        
        public void setExploding(boolean explodeState) {
            isExploding = explodeState;
        }
    }
}