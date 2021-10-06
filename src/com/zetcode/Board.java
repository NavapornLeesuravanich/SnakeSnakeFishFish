package com.zetcode;

import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.Key;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JButton;
import java.util.TimerTask;


public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 600; //width screen
    private final int B_HEIGHT = 600; //height screen
    private final int DOT_SIZE = 10;  //size of icon (px)
    private final int ALL_DOTS = 3600; //how many dots in screen
    private final int RAND_POS = 60;  //rand pos in screen (use in apple)
    private int DELAY = 140; //snake speed

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots; //snake length
    private int score=0;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = false;
    private boolean status = false;
    private boolean normalMode = false;
    private boolean luckMode = false;

    private Timer timer;  //speed snake

    private Image ball;
    private Image apple;
    private Image head;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {  //load image of snake apple and tail

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
    }

    private void updateSpeed(){
        DELAY -= 5; // speed increase when snake eats the apple
        if(DELAY >= 40) {
            timer.setDelay(DELAY);
        }
    }

    private void initGame() {  //initial game kit

        dots = 2; //start with head and tail
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }

        locateApple();


        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {   //render game graphic

        if (inGame && status) {

            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();
            Score(g);
        } else if(inGame == false && status == true) {
            gameOver(g);
        }
        else{
            startMessageDisplay(g);
        }
    }

    private void gameOver(Graphics g) {  //to show score and length of the snake when dead and show text to restart

        String msg = "Game Over!";
        String msg2 = "Score: " + score + " " + "Length: " + dots;
        Font small = new Font("helvetica ",Font.BOLD,14 );
        FontMetrics messageSize = getFontMetrics(small);
        g.setColor(Color.RED);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - messageSize.stringWidth(msg)) / 2, B_HEIGHT / 2);

        g.setColor(Color.WHITE);
        g.drawString(msg2, (B_WIDTH - messageSize.stringWidth(msg2)) / 2, (B_HEIGHT / 2) + 20);

        String restartMessage = " Press Enter to restart";
        g.setColor(Color.YELLOW);
        FontMetrics metrics = getFontMetrics(small);
        g.drawString(restartMessage, (B_WIDTH - metrics.stringWidth(restartMessage)) / 2, (B_HEIGHT / 2) + 40);

        String mainmenu = " Press Spacebar to back to main menu";
        g.setColor(Color.GREEN);
        g.drawString(mainmenu, (B_WIDTH - metrics.stringWidth(mainmenu)) / 2, (B_HEIGHT / 2) + 60);
    }

    private void Score(Graphics g) {  //to show score at the right corner
        Font small = new Font("helvetica", Font.BOLD, 14);
        g.setColor(Color.WHITE);
        g.setFont(small);
        if(normalMode == true){
            g.drawString("Mode: Normal", 490, 25);
        }
        if(luckMode == true){
            g.drawString("Mode: Luck", 490, 25);
        }

        g.drawString("Score: " + score, 490, 45);
      //  g.drawString("Speed: " + timer.getDelay(), 490, 65);
    }



    public void startMessageDisplay(Graphics g) {  //show the start screen when open game first time
        String msg = "Welcome to Snake Game!";
        String msg2 = "Press N to Normal mode game";
        String msg3 = "Press L to Luck mode game";
        String msg4 = "Press R to random mode game";

        Font small = new Font("helvetica ",Font.BOLD,16 );
        FontMetrics messageSize = getFontMetrics(small);

        g.setColor(Color.green);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - messageSize.stringWidth(msg)) / 2, B_HEIGHT / 2 -30);
        g.setColor(Color.WHITE);
        g.drawString(msg2, (B_WIDTH - messageSize.stringWidth(msg2)) / 2, (B_HEIGHT / 2));
        g.drawString(msg3, (B_WIDTH - messageSize.stringWidth(msg2)) / 2, (B_HEIGHT / 2) + 20);
        g.drawString(msg4, (B_WIDTH - messageSize.stringWidth(msg2)) / 2, (B_HEIGHT / 2) + 40);

    }


    private void checkApple() {  //to check that snake eats apple or not
        double randNeg =(Math.random()); //random to decrease scale of snake or not
        double randPosi =(Math.random()); //random to increase(+3) scale of snake or not
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            if(normalMode == true){
                dots++;
            }else {
                if(randNeg < 0.5){
                    dots--; //decrease snake length
                }else{
                    if(randPosi > 0.5){
                        dots++; //increase snake length by 1
                    }else{
                        dots+=3; //increase snake length by 3
                    }
                }
            }
            score++;  //update score
            updateSpeed();  //to update speed of snake
            locateApple();  //to locate new apple
        }
    }

    private void move() {  //move snake

        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() { //check snake is on border or eat yourself or not

        for (int z = dots; z > 0; z--) {  //eat yourself

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        //bottom border
        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }
        //top border
        if (y[0] < 0) {
            inGame = false;
        }
        //right border
        if (x[0] >= B_WIDTH) {
            inGame = false;
        }
        //left border
        if (x[0] < 0) {
            inGame = false;
        }

        //length snake
        if(dots == 0){
            inGame = false;
        }

        //if inGame = false snake will stop
        if (!inGame) {
            timer.stop();
        }

    }

    private void locateApple() {  //random apple

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) { //game loop

        if (inGame) {

            checkApple();
            checkCollision();
            move();

        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {  //listen keypress

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();
            //left
            if (((key == KeyEvent.VK_LEFT) || (key == KeyEvent.VK_A)) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }
            //right
            if (((key == KeyEvent.VK_RIGHT) || (key == KeyEvent.VK_D)) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }
            //up
            if (((key == KeyEvent.VK_UP) || (key == KeyEvent.VK_W)) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            //down
            if (((key == KeyEvent.VK_DOWN) || (key == KeyEvent.VK_S)) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            //game restart
            if(key == KeyEvent.VK_ENTER) {
                if(!inGame) {
                    inGame = true;
                    leftDirection = false;
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;
                    score = 0;
                    DELAY = 140;
                    initBoard();
                }
            }
            //back to menu game
            if(key == KeyEvent.VK_SPACE) {
                if(status == true && inGame == false) {
                    status = false;
                    normalMode = false;
                    luckMode = false;
                    inGame=false;
                    leftDirection = false;
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;
                    score = 0;
                    DELAY = 140;
                    initBoard();
                }

            }

            //Normal mode
            if(key == KeyEvent.VK_N) {
                if(normalMode == false && status == false) {
                    status = true;
                    inGame= true;
                    normalMode = true;
                }
            }
            //luck mode
            if(key == KeyEvent.VK_L) {
                if(luckMode == false && status == false) {
                    status = true;
                    inGame= true;
                    luckMode = true;
                }
            }


            //random mode
            if(key == KeyEvent.VK_R) {
                if(inGame == false && status == false){
                    double randMode = Math.random();
                    if(randMode > 0.5){
                        normalMode = true;
                        luckMode = false;
                    }else{
                        luckMode = true;
                        normalMode = false;
                    }
                    status = true;
                    inGame= true;

                }

            }
        }
    }
}
