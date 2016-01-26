package cosmoturkey;

import java.awt.*;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.*;
import image.*;
import java.awt.image.BufferedImage;
import screen.Dim;

public class Coin {
    
    private Image coinImage;
    private int x;
    private int y;
    private boolean isVisible;
    private boolean isDead;
    private Timer appearTimer;
    private Timer disappearTimer;
    private boolean appearTimerRunning;
    private boolean disappearTimerRunning;
    private boolean timersPaused;
    private Appear appear;
    private Disappear disappear;
    Dim dim;
    
    public Coin(String filename, int width, int height) {
        dim = new Dim(width, height, 1000, 700);
        ImageIcon ii = new ImageIcon(filename);
        ImageTransparency it = new ImageTransparency();
        ImageConverter ic = new ImageConverter();
        BufferedImage bi = ic.imageToBufferedImage(ii.getImage());
        coinImage = it.makeColorTransparent(bi, Color.white);
        getNewCoordinates();
        isVisible = false;
        isDead = false;
        appearTimer = new Timer();
        disappearTimer = new Timer();
        appear = new Appear();
        disappear = new Disappear();
        appearTimer.schedule(appear, getRandomAppearTime());
        appearTimerRunning = true;
        disappearTimerRunning = false;
        timersPaused = false;
    }
    
    public void checkForAppear() {
        if (appear.isAppearing()) {
            isVisible = true;
            appear.setAppearing(false);
            appearTimer.cancel();
            appearTimer.purge();
            appearTimerRunning = false;
            disappearTimer.schedule(disappear, 5000);
            disappearTimerRunning = true;
        } else if (disappear.isDisappearing()) {
            setDead();
            disappear.setDisappearing(false);
        }
    }
    
    private long getRandomAppearTime() {
        return (long) Math.floor(Math.random() * 180000 + 5000);
    }
    
    public void getNewCoordinates() {
        x = (int) Math.floor(Math.random() * dim.adjW(900) + dim.adjW(100));
        y = (int) Math.floor(Math.random() * dim.adjH(675) + dim.adjH(100));
    }
    
    public Image getImage() {
        return coinImage;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public Rectangle makeRectangle() {
        return new Rectangle(x, y, coinImage.getWidth(null), coinImage.getHeight(null));
    }
    
    public boolean isVisible() {
        checkForAppear();
        return isVisible;
    }
    
    //see if I can delete this
    public boolean isDead() {
        return isDead;
    }
    
    public void setDead() {
        if (appearTimerRunning) {
            appearTimer.cancel();
            appearTimer.purge();
            appearTimerRunning = false;
        }
        if (disappearTimerRunning) {
            disappearTimer.cancel();
            disappearTimer.purge();
            disappearTimerRunning = false;
        }
        isVisible = false;
        isDead = true;
    }
    
    private class Appear extends TimerTask {
        private boolean isAppearing;
        public Appear() {
            isAppearing = false;
        }

        @Override
        public void run() {
            isAppearing = true;
        }
        
        public boolean isAppearing() {
            return isAppearing;
        }
        
        public void setAppearing(boolean state) {
            isAppearing = state;
        }
    }
    
    private class Disappear extends TimerTask {
        private boolean isDisappearing;
        public Disappear() {
            isDisappearing = false;
        }
        
        @Override
        public void run() {
            isDisappearing = true;
        }
        
        public boolean isDisappearing() {
            return isDisappearing;
        }
        
        public void setDisappearing(boolean state) {
            isDisappearing = state;
        }
    }
}