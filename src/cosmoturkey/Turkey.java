package cosmoturkey;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import image.*;
import java.util.Timer;
import java.util.TimerTask;
import screen.Dim;

public class Turkey {

    private double x;
    private double y;
    private double xChange;
    private double yChange;
    private Image turkeyImage;
    private Image turkeyE;
    private Image turkeyW;
    private Image turkeyWinv;
    private Image turkeyEinv;
    private boolean east;
    private boolean west;
    private Image cookedTky;
    private String turkeyEFile = "images/turkey7east.gif";
    private String turkeyWFile = "images/turkey7west.gif";
    private String cookedTkyFile = "images/turkey5.gif";
    private String turkeyEinvFile = "images/turkey7eastshield.gif";
    private String turkeyWinvFile = "images/turkey7westshield.gif";
    private int turkeyWidth;
    private int turkeyHeight;
    private Toolkit tk;
    private Dimension screen;
    private double turkeyFPS;
    private boolean visible;
    private Timer speedTimer;
    private SpeedTimerTask speedTimerTask;
    private Timer invincibilityTimer;
    private InvincibilityTimerTask invincibilityTimerTask;
    private boolean goingFast;
    private boolean invincible;
    Dim dim;
    //controls
    private boolean rightButton;
    private boolean leftButton;
    private boolean upButton;
    private boolean downButton;

    public Turkey(int width, int height) {
        dim = new Dim(width, height, 1000, 700);
        //get turkey image
        ImageIcon i3 = new ImageIcon(turkeyEFile);
        ImageIcon i4 = new ImageIcon(turkeyWFile);
        ImageIcon i5 = new ImageIcon(turkeyEinvFile);
        ImageIcon i6 = new ImageIcon(turkeyWinvFile);
        //make the image's background transparent
        ImageConverter ic = new ImageConverter();
        ImageTransparency it = new ImageTransparency();
        BufferedImage bi3 = ic.imageToBufferedImage(i3.getImage());
        turkeyE = it.makeColorTransparent(bi3, Color.white);
        turkeyImage = turkeyW;
        BufferedImage bi4 = ic.imageToBufferedImage(i4.getImage());
        turkeyW = it.makeColorTransparent(bi4, Color.white);
        BufferedImage bi5 = ic.imageToBufferedImage(i5.getImage());
        turkeyEinv = it.makeColorTransparent(bi5, Color.white);
        BufferedImage bi6 = ic.imageToBufferedImage(i6.getImage());
        turkeyWinv = it.makeColorTransparent(bi6, Color.white);
        //get cooked turkey image
        ImageIcon ii = new ImageIcon(cookedTkyFile);
        //make the image's background transparent
        BufferedImage biCooked = ic.imageToBufferedImage(ii.getImage());
        cookedTky = it.makeColorTransparent(biCooked, Color.white);
        turkeyWidth = turkeyW.getWidth(null);
        turkeyHeight = turkeyW.getHeight(null);
        putInStartPos();
        east = true;
        west = false;
        
        turkeyFPS = dim.adjWDouble(2);    
        
        visible = true;
        
        goingFast = false;
        invincible = false;
    }

    public void putInStartPos() {
        x = dim.adjW(5);
        y = dim.adjH(30);
        east = true;
    }
    public void move(int winWidth, int winHeight) {
        
        //see if turkey is done going fast
        if (speedTimerTask != null) {
            if (speedTimerTask.goingFast() == false) {
                goingFast = false;
                speedTimerTask.setGoingFast(true);
                speedTimer.purge();
                speedTimer.cancel();
            }
        }
        
        //see if turkey is done being invincible
        if (invincibilityTimerTask != null) {
            if (invincibilityTimerTask.isInvincible() == false) {
                invincible = false;
                invincibilityTimerTask.setInvincible(true);
                invincibilityTimer.purge();
                invincibilityTimer.cancel();
            }
        }
        
        
        if (rightButton && leftButton) {
            xChange = 0;
        }
        if (upButton && downButton) {
            yChange = 0;
        }
        
        if (x <= 0) {
            x = 1;
        }
        if (y <= 25) {
            y = 26;
        }
        if (x >= winWidth - turkeyWidth - 5) {
            x = (winWidth - 1) - turkeyWidth - 5;
        }
        if (y >= (winHeight) - turkeyHeight/2) {
            y = (winHeight) - turkeyHeight/2;
        }

        if (goingFast) {
            x += xChange * 2;
            y += yChange * 2;
        } else {
            x += xChange;
            y += yChange;
        }
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public Image getImage() {
        
        if (east) {
            if (invincible) {
                turkeyImage = turkeyEinv;
            } else { 
                turkeyImage = turkeyE;
            }
        } else if (west) {
            if (invincible) {
                turkeyImage = turkeyWinv;
            } else {
                turkeyImage = turkeyW;
            }
        }
        return turkeyImage;
    }
    
    public Image getCookedImage() {
        return cookedTky;
    }
    
    public Rectangle makeRectangle() {
        Rectangle rect;
        if (east) {
            rect = new Rectangle((int) Math.round(x + .25 * turkeyWidth), (int) Math.round(y + .35 * turkeyHeight), (int) Math.round(.7 * turkeyWidth), (int) Math.round(.45 * turkeyHeight));
        } else {
            rect = new Rectangle((int) Math.round(x + .05 * turkeyWidth), (int) Math.round(y + .35 * turkeyHeight), (int) Math.round(.7 * turkeyWidth), (int) Math.round(.45 * turkeyHeight));
        }
        return rect;
    }
    
    public void setVisible(boolean vState) {
        visible = vState;
    }
    
    public void setGoingFast(boolean state) {
        goingFast = state;
        if (goingFast) {
            speedTimer = new Timer();
            speedTimerTask = new SpeedTimerTask();
            speedTimer.schedule(speedTimerTask, 5000);
        }
    }
    
    public void setInvincible(boolean state) {
        invincible = state;
        if (invincible) {
            invincibilityTimer = new Timer();
            invincibilityTimerTask = new InvincibilityTimerTask();
            invincibilityTimer.schedule(invincibilityTimerTask, 5000);
        }
    }
    
    public boolean isInvincible() {
        return invincible;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    private class SpeedTimerTask extends TimerTask {
        
        private boolean goingFast;
        
        public SpeedTimerTask() {
            goingFast = true;
        }
        
        @Override
        public void run() {
            goingFast = false;
        }
        
        public boolean goingFast() {
            return goingFast;
        }
        
        public void setGoingFast(boolean state) {
            goingFast = state;
        }
    }
    
    public class InvincibilityTimerTask extends TimerTask {
        private boolean invincible;
        
        public InvincibilityTimerTask() {
            invincible = true;
        }
        
        @Override
        public void run() {
            invincible = false;
        }
        
        public void setInvincible(boolean state) {
            invincible = state;
        }
        
        public boolean isInvincible() {
            return invincible;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        char kChar = e.getKeyChar();

        if ((key == KeyEvent.VK_RIGHT) || (kChar == 'd')) {
            xChange = turkeyFPS;
            rightButton = true;
            east = true;
            west = false;
        }
        if ((key == KeyEvent.VK_LEFT) || (kChar == 'a')) {
            xChange = -turkeyFPS;
            leftButton = true;
            west = true;
            east = false;
        }
        if ((key == KeyEvent.VK_DOWN) || (kChar == 's')) {
            yChange = turkeyFPS;
            downButton = true;
        }
        if ((key == KeyEvent.VK_UP) || (kChar == 'w')) {
            yChange = -turkeyFPS;
            upButton = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        char kChar = e.getKeyChar();

        if ((key == KeyEvent.VK_DOWN) || (kChar == 's')) {
            if (upButton) {
                yChange = -turkeyFPS;
            } else {
                yChange = 0;
            }
            downButton = false;
        }
        
        if ((key == KeyEvent.VK_UP) || (kChar == 'w')) {
            if (downButton) {
                yChange = turkeyFPS;
            } else {
                yChange = 0;
            }
            upButton = false;
        }

        if ((key == KeyEvent.VK_LEFT) || (kChar == 'a')) {
            if (rightButton) {
                xChange = turkeyFPS;
                east = true;
                west = false;
            } else {
                xChange = 0;
            }
            leftButton = false;
        }

        if ((key == KeyEvent.VK_RIGHT) || (kChar == 'd')) {
            if (leftButton) {
                xChange = -turkeyFPS;
                west = true;
                east = false;
            } else {
                xChange = 0;
            }
            rightButton = false;
        }
    }
}