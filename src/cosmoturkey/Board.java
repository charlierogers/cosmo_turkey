package cosmoturkey;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import image.*;
import button.JGradientButton;
import java.util.ArrayList;
import java.io.*;
import nu.xom.*;
import screen.Dim;

public class Board extends JPanel implements ActionListener {

    private boolean playing;
    private boolean paused;
    private boolean wonGame;
    private Color boardColor;
    private Timer timer;
    private Turkey turkey;
    private ArrayList shot;
    private ArrayList bushes;
    private ArrayList babies;
    private Rectangle safeZone;
    private Rectangle statusBar;
    private Rectangle turkeyRect;
    private Rectangle foodTroughRect;
    private int statusBarHeight;
    private Image tkyImage;
    private Image farmer;
    private Image youWon;
    private Image foodTrough;
    private int lives;
    private int score;
    private int accumulatedScore;
    private int coins;
    private int numOfCoins;
    private int level;
    private String livesString;
    private String babiesString;
    private String levelString;
    private String scoreString;
    private String coinsString;
    private String coinImage;
    private String speedCoinImage;
    private String invincibilityCoinImage;
    private int hitCycle;
    private boolean isHit;
    private boolean hitLastTime;
    private boolean underBush;
    private boolean deadTky;
    private boolean hasFood;
    private int babiesFed;
    private ArrayList coinsArray;
    private ArrayList speedCoinsArray;
    private ArrayList invincibilityCoinsArray;
    private ArrayList highScores;
    private ArrayList highScoresNames;
//    JGradientButton playAgain;
    JGradientButton goToLevel1;
    JGradientButton saveScore;
    JGradientButton continueButton;
    JTextField nameField;
    private boolean removedSaveScore;
    ButtonActions buttonActions;
    ContinueActions continueActions;
    GoBackToLevel1Actions goBackToLevel1Actions;
    ArrowMover arrowMover;
    int screenWidth;
    int screenHeight;
    Dim dim;
    int[][] babyTkyPos = {{5, 100}, {5, 150}, {5, 200}, {5, 250}, {5, 300}};
    private double[] random = new double[10];
    
    String totalScore;

    public Board(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        boardColor = new Color(0, 238, 118);
        setSize(width, height);
        dim = new Dim(screenWidth, screenHeight, 1000, 700);
        reset();
    }
    
    private void reset() {
        
        //set up board
        setBackground(boardColor);
        
        //set up ArrayList() for temp storage of high scores and names
        highScores = new ArrayList();
        highScoresNames = new ArrayList();
        
        readDataFromXML();
        
        if (level < 1) {
            level = 1;
        }
        if (accumulatedScore < 0) {
            accumulatedScore = 0;
        }
        playing = true;
        paused = false;
        turkey = new Turkey(screenWidth, screenHeight);
        
        tkyImage = turkey.getImage();
        lives = 5;
        numOfCoins = 21 - level;
        if (numOfCoins < 0) {
            numOfCoins = level - 21;
        }
        coins = 0;
        score = 0;
        hitCycle = 0;
        babiesFed = 0;
        
        coinImage = "images/coin.gif";
        speedCoinImage = "images/rocket.gif";
        invincibilityCoinImage = "images/shield.gif";
        
        setFocusable(true);
        setDoubleBuffered(true);
        buttonActions = new ButtonActions();
        continueActions = new ContinueActions();
        goBackToLevel1Actions = new GoBackToLevel1Actions();
        arrowMover = new ArrowMover();
        addKeyListener(arrowMover);
        addKeyListener(buttonActions);
        setUpShot();
        setUpBushes();
        setUpBabyTky();
        setUpImages();
        setUpCoins();
        setUpSpeedCoins();
        setUpInvincibilityCoins();
        checkForCoinOverlappingABush();
        
        //set up mini gui
        setLayout(null);
//        playAgain = new JGradientButton("Continue");
//        playAgain.setBounds(dim.adjW(300), dim.adjH(600), dim.adjW(110), dim.adjH(30));
//        playAgain.addActionListener(buttonActions);
//        playAgain.setGradientColors(Color.white, Color.green);
        
        
        //only displayed if you have a high score
        saveScore = new JGradientButton("Save Score");
        saveScore.setBounds(dim.adjW(685), dim.adjH(470), dim.adjW(100), dim.adjH(30));
        saveScore.addActionListener(buttonActions);
        saveScore.setGradientColors(Color.white, Color.green);
        removedSaveScore = false;
        
        //only displayed if you have a high score
        nameField = new JTextField();
        nameField.setBounds(dim.adjW(490), dim.adjH(470), dim.adjW(175), dim.adjH(30));
        nameField.addKeyListener(buttonActions);
        
        continueButton = new JGradientButton("Continue");
        continueButton.setBounds(dim.adjW(350), dim.adjH(600), dim.adjW(110), dim.adjH(30));
        continueButton.addActionListener(buttonActions);
        continueButton.setGradientColors(Color.white, Color.green);
        
        goToLevel1 = new JGradientButton("Go Back To Level 1");
        goToLevel1.setBounds(dim.adjW(500), dim.adjH(600), dim.adjW(200), dim.adjH(30));
        goToLevel1.addActionListener(buttonActions);
        goToLevel1.setGradientColors(Color.white, Color.green);
        
        statusBar = new Rectangle(0, 0, getWidth(), dim.adjH(25));
        statusBarHeight = (int) statusBar.getHeight();
        safeZone = new Rectangle(0, statusBarHeight, dim.adjW(60), getHeight() - statusBarHeight);
        
        //start new timer to call paint method every 15 milliseconds
        timer = new Timer(15, this);
        timer.start();
    }
    

    private void setUpShot() {
        shot = new ArrayList();
        for (int i = 0; i < level + 4; i++) {
            shot.add(new BlackCloud(getWidth(), getHeight() - statusBarHeight));
        }
    }

    private void setUpBushes() {
        bushes = new ArrayList();
        for (int i = 0; i < 4; i++) {
            bushes.add(new Bush(getWidth(), getHeight() - statusBarHeight));
        }
        checkForBushOverlap();
    }

    private void checkForBushOverlap() {
        Bush b1 = (Bush) bushes.get(0);
        Rectangle b1r = b1.makeRectangle();
        Bush b2 = (Bush) bushes.get(1);
        Rectangle b2r = b2.makeRectangle();
        Bush b3 = (Bush) bushes.get(2);
        Rectangle b3r = b3.makeRectangle();
        Bush b4 = (Bush) bushes.get(3);
        Rectangle b4r = b4.makeRectangle();

        if ((b1r.intersects(b2r)) || (b1r.intersects(b3r)) || (b1r.intersects(b4r))
                || (b2r.intersects(b3r)) || (b2r.intersects(b4r)) || (b3r.intersects(b4r))) {
            setUpBushes();
        }
    }
    
    
   
    private void setUpBabyTky() {
        System.out.println("BabyTky Positions Level " + level);
//        for (int i = 0; i < 5; i++) {
//            System.out.print("(" + babyTkyPos[i][0] + ", " + babyTkyPos[i][1] + ")");
//            babyTkyPos[i][0] = dim.adjW(babyTkyPos[i][0]);
//            babyTkyPos[i][1] = dim.adjH(babyTkyPos[i][1]);
//            System.out.println("      (" + babyTkyPos[i][0] + ", " + babyTkyPos[i][1] + ")");
//        }
        babies = new ArrayList();
        for (int i = 0; i < 5; i++) {
            babies.add(new BabyTky(dim.adjW(babyTkyPos[i][0]), dim.adjH(babyTkyPos[i][1])));
            System.out.println("(" + dim.adjW(babyTkyPos[i][0]) + ", " + dim.adjH(babyTkyPos[i][1]) + ")");
        }
    }
    
    private void setUpCoins() {
        coinsArray = new ArrayList();
        for (int i = 0; i < 20; i++) {
            coinsArray.add(new Coin(coinImage, screenWidth, screenHeight));
        }
    }
    
    private void setUpSpeedCoins() {
        speedCoinsArray = new ArrayList();
        for (int i = 0; i < 20; i++) {
            speedCoinsArray.add(new Coin(speedCoinImage, screenWidth, screenHeight));
        }
    }
    
    private void setUpInvincibilityCoins() {
        invincibilityCoinsArray = new ArrayList();
        for (int i = 0; i < 20; i++) {
            invincibilityCoinsArray.add(new Coin(invincibilityCoinImage, screenWidth, screenHeight));
        }
    }
    
    private void setUpHighScores() {
        int zero = 0;
        for (int i = 0; i < 5; i++) {
            highScores.add(zero);
            highScoresNames.add(" ");
        }
    }
    
    private void checkForCoinOverlappingABush() {
        for (int i = 0; i < bushes.size(); i++) {
            Bush bush = (Bush) bushes.get(i);
            for (int j = 0; j < coinsArray.size(); j++) {
                Coin coin = (Coin) coinsArray.get(j);
                if (coin.makeRectangle().intersects(bush.makeRectangle())) {
                    setUpCoins();
                }
            }
            for (int k = 0; k < speedCoinsArray.size(); k++) {
                Coin speedCoin = (Coin) speedCoinsArray.get(k);
                if (speedCoin.makeRectangle().intersects(bush.makeRectangle())) {
                    setUpSpeedCoins();
                }
            }
            for (int l = 0; l < invincibilityCoinsArray.size(); l++) {
                Coin invincibilityCoin = (Coin) invincibilityCoinsArray.get(l);
                if (invincibilityCoin.makeRectangle().intersects(bush.makeRectangle())) {
                    setUpInvincibilityCoins();
                }
            }
        }
    }
    
    private void setUpImages() {
        ImageConverter ic = new ImageConverter();
        ImageTransparency it = new ImageTransparency();
        ImageIcon i1 = new ImageIcon("images/farmercombo.jpg");
        BufferedImage bi = ic.imageToBufferedImage(i1.getImage());
        farmer = it.makeColorTransparent(bi, Color.white);
        ImageIcon i2 = new ImageIcon("images/youwon4.gif");
        youWon = i2.getImage();
        ImageIcon i3 = new ImageIcon("images/foodtrough.gif");
        bi = ic.imageToBufferedImage(i3.getImage());
        foodTrough = it.makeColorTransparent(bi, Color.white);
    }
    
    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

        Graphics2D g = (Graphics2D) graphics;

        g.draw(statusBar);
        livesString = "Lives Left: " + lives;
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(livesString, dim.adjW(5), dim.adjH(18));
        babiesString = "Babies Fed: " + babiesFed;
        g.drawString(babiesString, dim.adjW(150), dim.adjH(18));
        if (playing) {
            scoreString = "Score: " + (score + accumulatedScore);
        } else {
            scoreString = "Score: " + (accumulatedScore);
        }
        g.drawString(scoreString, dim.adjW(310), dim.adjH(18));
        coinsString = "Coins: " + coins;
        g.drawString(coinsString, dim.adjW(440), dim.adjH(18));
        levelString = "Level: " + Integer.toString(level);
        g.drawString(levelString, dim.adjW(550), dim.adjH(18));
        if (hasFood) {
            String foodString = "FOOD";
            g.drawString(foodString, dim.adjW(670), dim.adjH(18));
        }
        
        
        if (playing) {
            
            //draw food trough
            g.drawImage(foodTrough, screenWidth - foodTrough.getWidth(null), dim.adjH(300), this);
            
            //draw baby Tky
            for (int i = 0; i < babies.size(); i++) {
                BabyTky baby = (BabyTky) babies.get(i);
                g.drawImage(baby.getImage(), baby.getX(), baby.getY(), this);
            }

            //draw turkey
            g.drawImage(tkyImage, (int) turkey.getX(), (int) turkey.getY(), this); 
            
            //draw bushes
            for (int i = 0; i < bushes.size(); i++) {
                Bush bush = (Bush) bushes.get(i);
                if (bush.isVisible()) {
                    g.drawImage(bush.getImage(), bush.getX(), bush.getY(), this);
                }
            }

            //draw safe zone
            g.setColor(Color.black);
            g.draw(safeZone);
            
            //draw coins
            for (int i = 0; i < coinsArray.size(); i++) {
                Coin coin = (Coin) coinsArray.get(i);
                if (coin.isVisible()) {
                    g.drawImage(coin.getImage(), coin.getX(), coin.getY(), this);
                }
                if (coin.isDead()) {
                    coinsArray.remove(i);
                }
            }
            
            //draw speed coins
            for (int i = 0; i < speedCoinsArray.size(); i++) {
                Coin speedCoin = (Coin) speedCoinsArray.get(i);
                if (speedCoin.isVisible()) {
                    g.drawImage(speedCoin.getImage(), speedCoin.getX(), speedCoin.getY(), this);
                }
                if (speedCoin.isDead()) {
                    speedCoinsArray.remove(i);
                }
            }
            
            //draw invincibility coins
            for (int i = 0; i < invincibilityCoinsArray.size(); i++) {
                Coin invincibilityCoin = (Coin) invincibilityCoinsArray.get(i);
                if (invincibilityCoin.isVisible()) {
                    g.drawImage(invincibilityCoin.getImage(), invincibilityCoin.getX(), invincibilityCoin.getY(), this);
                }
                if (invincibilityCoin.isDead()) {
                    invincibilityCoinsArray.remove(i);
                }
            }

            //draw black cloud
            for (int i = 0; i < shot.size(); i++) {
                BlackCloud bc = (BlackCloud) shot.get(i);
                g.drawImage(bc.getImage(), bc.getX(), bc.getY(), this);
            }
        } else {
            setBackground(Color.white);
            String levelScore = "Score for This Level: " + score;
            totalScore = "Total Score: " + accumulatedScore;
            if (wonGame) {
                g.drawImage(youWon, dim.adjW(275), dim.adjH(50), this);
                
            } else {
                g.drawImage(farmer, dim.adjW(200), dim.adjH(100), this);
                g.setFont(new Font("Arial", Font.BOLD, 48));
            }
            g.setFont(new Font("Arial", Font.BOLD, 48));
                g.drawString(levelScore, dim.adjW(400), dim.adjH(350));
                g.drawString(totalScore, dim.adjW(400), dim.adjH(425));
                g.drawString(new String("High Scores"), dim.adjW(50), dim.adjH(400));
                int nameX = dim.adjW(50);
                int nameY = dim.adjH(440);
                int scoreX = dim.adjW(250);
                int scoreY = dim.adjH(440);
                for (int i = 0; i < 5; i++) {
                    g.setFont(new Font("Arial", Font.BOLD, 24));
                    g.drawString((String) highScoresNames.get(i), nameX, nameY);
                    g.drawString(Integer.toString((Integer) highScores.get(i)), scoreX, scoreY);
                    nameY += dim.adjH(30);
                    scoreY += dim.adjH(30);
                }
                if (isHighScore() && !removedSaveScore) {
                    g.setFont(new Font("Arial", Font.BOLD, 28));
                    g.drawString(new String("Name: "), dim.adjW(400), dim.adjH(490));
                    add(saveScore);
                    add(nameField);
                }
            
//            add(playAgain);
            add(goToLevel1);
            add(continueButton);
            saveDataToXML();
        }

        Toolkit.getDefaultToolkit().sync();
        graphics.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused) {
            if (playing) {
                score = babiesFed * 50 + lives * 10 + coins * 20;
            }
            if (lives <= 0) {
                playing = false;
                wonGame = false;
                timer.stop();
            }
            if (babiesFed == 5) {
                playing = false;
                wonGame = true;
                level++;
                score += 100;
                accumulatedScore += score;
                timer.stop();
            }

            checkIntersections();
            
            if (!deadTky) {
                turkey.move(getWidth(), getHeight() - statusBarHeight);
            }
            for (int i = 0; i < shot.size(); i++) {
                BlackCloud bc = (BlackCloud) shot.get(i);
                bc.move();
            }

            //handle directional if not dead
            if (!deadTky) {
                tkyImage = turkey.getImage();
            }

            repaint();
        } else {
            //keep checking coin timers to prevent overflow when un-paused
            for (int i = 0; i < coinsArray.size(); i++) {
                Coin coin = (Coin) coinsArray.get(i);
                coin.checkForAppear();
            }
            for (int i = 0; i < speedCoinsArray.size(); i++) {
                Coin coin = (Coin) speedCoinsArray.get(i);
                coin.checkForAppear();
            }
            for (int i = 0; i < invincibilityCoinsArray.size(); i++) {
                Coin coin = (Coin) invincibilityCoinsArray.get(i);
                coin.checkForAppear();
            }
        }
    }

    public void checkIntersections() {
        turkeyRect = turkey.makeRectangle();
        foodTroughRect = new Rectangle(screenWidth-foodTrough.getWidth(null), dim.adjH(300), foodTrough.getWidth(null), foodTrough.getHeight(null));

        //keep black clouds out of safe zone and food trough
        for (int i = 0; i < shot.size(); i++) {
            BlackCloud bc = (BlackCloud) shot.get(i);
            Rectangle bcRect = bc.makeRectangle();
            if ((bcRect.intersects(safeZone)) || (bcRect.intersects(foodTroughRect))) {
                bc.putAtTop();
            }
        }

        //see if tky is in food trough
        if ((turkeyRect.intersects(foodTroughRect)) && !hasFood) {
            hasFood = true;
        }

        //see if tky is trying to feed babies
        for (int i = 0; i < babies.size(); i++) {
            BabyTky baby = (BabyTky) babies.get(i);
            if (turkeyRect.intersects(baby.makeRectangle()) && hasFood) {
                babiesFed++;
                lives++;
                hasFood = false;
                baby.setVisible(false);
            }
            if (!baby.isVisible()) {
                babies.remove(i);
            }
        }
        
        //see if tky is under bush
        Loop:
        for (int i = 0; i < bushes.size(); i++) {
            Bush bush = (Bush) bushes.get(i);
            if (turkeyRect.intersects(bush.makeRectangle())) {
                underBush = true;
                if (bush.isExploding() && !turkey.isInvincible()) {
                    deadTky = true;
                }
                break Loop;
            } else {
                underBush = false;
            }
        }

        //subtract from lives if tky is hit
        if (!underBush && !deadTky && !turkey.isInvincible()) {
            Loop:
            for (int i = 0; i < shot.size(); i++) {
                BlackCloud bc = (BlackCloud) shot.get(i);
                if (bc.makeRectangle().intersects(turkeyRect)) {
                    isHit = true;
                    hitLastTime = true;
                    break Loop;
                } else {
                    hitLastTime = false;
                }
            }
        }
        if (isHit && !hitLastTime) {
            isHit = false;
            deadTky = true;
        }
        
        //see if turkey is collecting a coin
        for (int i = 0; i < coinsArray.size(); i++) {
            Coin coin = (Coin) coinsArray.get(i);
            if (coin.isVisible() && turkeyRect.intersects(coin.makeRectangle())) {
                coins++;
                coin.setDead();
            }
        }
        
        //see if turkey is collecting a speed coin
        for (int i = 0; i < speedCoinsArray.size(); i++) {
            Coin speedCoin = (Coin) speedCoinsArray.get(i);
            if (speedCoin.isVisible() && turkeyRect.intersects(speedCoin.makeRectangle())) {
                turkey.setGoingFast(true);
                speedCoin.setDead();
            }
        }
        
        //see if turkey is collecting an invincibility coin
        for (int i = 0; i < invincibilityCoinsArray.size(); i++) {
            Coin invincibilityCoin = (Coin) invincibilityCoinsArray.get(i);
            if (invincibilityCoin.isVisible() && turkeyRect.intersects(invincibilityCoin.makeRectangle())) {
                turkey.setInvincible(true);
                invincibilityCoin.setDead();
            }
        }

        //handle dead tky image
        if (deadTky) {
            tkyImage = turkey.getCookedImage();
            hitCycle++;
            if (hitCycle > 50) {
                deadTky = false;
                lives--;
                hasFood = false;
                hitCycle = 0;
                tkyImage = turkey.getImage();
                turkey.putInStartPos();
            }
        }
    }
    
    public boolean isHighScore() {
        boolean isHighScore = false;
        if (highScores.size() < 5) {
            isHighScore = true;
        } else {
            if (accumulatedScore >= (Integer) highScores.get(4)) {
                isHighScore = true;
            } else {
                isHighScore = false;
            }
        }
        return isHighScore;
    }
    
    public void addToHighScores() {
        if (isHighScore()) {
            for (int i = 0; i < highScores.size(); i++) {
                if (accumulatedScore >= (Integer) highScores.get(i)) {
                    highScores.remove(4);
                    highScores.add(i, accumulatedScore);
                    highScoresNames.add(i, nameField.getText());
                    break;
                }
            }
        }
    }
    
    
    public void saveDataToXML() {
        
        double tempScore;
        double tempLevel;
        ArrayList tempHighScoresArray;
        
        for (int i = 0; i < random.length; i++) {
            random[i] = Math.random();
        }
        
        Element root = new Element("root");
        Element scoreElement = new Element("score");
        scoreElement.appendChild(Integer.toString(accumulatedScore));
        root.appendChild(scoreElement);
        Element levelVar = new Element("level");
        levelVar.appendChild(Integer.toString(level));
        root.appendChild(levelVar);
        for (int i = 0; i < 5; i++) {
            Element highScore = new Element("highScore");
            highScore.appendChild(Integer.toString((Integer) highScores.get(i)));
            root.appendChild(highScore);
            Element highScoreName = new Element("highScoreName");
            highScoreName.appendChild((String) highScoresNames.get(i));
            root.appendChild(highScoreName);
        }
        
        Document doc = new Document(root);
        try {
            File data = new File("data.xml");
            FileOutputStream stream = new FileOutputStream(data);
            Serializer serializer = new Serializer(stream, "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(doc);
        } catch(IOException e) {
            
        }
    }
    
    private void readDataFromXML() {
        try {
            File dataFile = new File("data.xml");
            Builder builder = new Builder();
            Document doc = builder.build(dataFile);
            Element root = doc.getRootElement();
            Elements entries = root.getChildElements();
            for (int i = 0; i < entries.size(); i++) {
                Element entry = entries.get(i);
                if (entry.getLocalName().equals("score")) {
                    accumulatedScore = Integer.parseInt(entry.getValue());
                } else if (entry.getLocalName().equals("level")) {
                    level = Integer.parseInt(entry.getValue());
                } else if (entry.getLocalName().equals("highScore")) {
                    highScores.add(Integer.parseInt(entry.getValue()));
                } else if (entry.getLocalName().equals("highScoreName")) {
                    highScoresNames.add(entry.getValue());
                }
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    private class ButtonActions implements ActionListener, KeyListener {
        
        public ButtonActions() {
        }
        
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String command = actionEvent.getActionCommand();
            boolean reset = false;
            if (command.equals("Save Score")) {
                addToHighScores();
                remove(nameField);
                remove(saveScore);
                removedSaveScore = true;
                saveDataToXML();
                repaint();
            }
            
            if (command.equals("Go Back To Level 1")) {
                level = 1;
                accumulatedScore = 0;
                if (!removedSaveScore) {
                    remove(nameField);
                    remove(saveScore);
                    removedSaveScore = true;
                }
                remove(goToLevel1);
                remove(continueButton);
                saveDataToXML();
                grabFocus();
                reset();
            }
            if (command.equals("Continue")) { //continueButton
                    if (!removedSaveScore) {
                        remove(nameField);
                        remove(saveScore);
                        removedSaveScore = true;
                    }
                    remove(goToLevel1);
                    remove(continueButton);
                    saveDataToXML();
                    grabFocus();
                    reset();
            }
            
    }

        @Override
        public void keyTyped(KeyEvent kt) {
            if (kt.getKeyChar() == kt.VK_ENTER && nameField.hasFocus()) {
                addToHighScores();
                remove(nameField);
                remove(saveScore);
                removedSaveScore = true;
                saveDataToXML();
                repaint();
            } else if (kt.getKeyChar() == kt.VK_ENTER && !playing) {
                if (!removedSaveScore) {
                    remove(nameField);
                    remove(saveScore);
                    removedSaveScore = true;
                }
                remove(goToLevel1);
//                gui.remove(playAgain);
                remove(continueButton);
                saveDataToXML();
                reset();
            } 
            
            if (kt.getKeyChar() == KeyEvent.VK_SPACE && playing) {
                if (paused) {
                    paused = false;
                } else {
                    paused = true;
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent kp) {
            //do nothing
        }

        @Override
        public void keyReleased(KeyEvent kr) {
            //do nothing
        }
    }
    
    private class ContinueActions implements ActionListener {
        
        String command;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            command = e.getActionCommand();
            
            
        }
    }
    
    private class GoBackToLevel1Actions implements ActionListener {
        
        String command;
        
        @Override
        public void actionPerformed(ActionEvent e) {
            command = e.getActionCommand();
            
            if (command.equals("Go Back To Level 1")) {
                level = 1;
                accumulatedScore = 0;
                if (!removedSaveScore) {
                    remove(nameField);
                    remove(saveScore);
                    removedSaveScore = true;
                }
                remove(goToLevel1);
                saveDataToXML();
                reset();
            }
        }
    }

    private class ArrowMover extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent arrowKp) {
            turkey.keyPressed(arrowKp);
        }

        @Override
        public void keyReleased(KeyEvent arrowKr) {
            turkey.keyReleased(arrowKr);
        }
    }
}