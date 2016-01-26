package cosmoturkey;

import javax.swing.*;
import java.awt.*;

public class CosmoTurkeyv2_4_2 extends JFrame {
    
    Dimensioner dimensioner;
    
    public CosmoTurkeyv2_4_2() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("Cosmo Turkey v2.4.2");
        dimensioner = new Dimensioner();
        setSize(dimensioner.width(), dimensioner.height());
        add(new Board(dimensioner.width(), dimensioner.height()));
        setVisible(true);
    }
    
    public static void main(String[] args) {
        CosmoTurkeyv2_4_2 cf = new CosmoTurkeyv2_4_2();
    }
    
    private class Dimensioner {
        
        Dimension screenSize;
        int width;
        int height;
        double screenW;
        double screenH;
        
        public Dimensioner() {
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            screenW = screenSize.getWidth();
            screenH = screenSize.getHeight();
        }
        
        public int width() { 
            return (int) Math.round(screenW * .98);
        }
        
        public int height() {
            return (int) Math.round(screenH * .90);
        }
    }
}

