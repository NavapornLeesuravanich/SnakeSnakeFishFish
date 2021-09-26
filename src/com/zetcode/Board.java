package com.zetcode;

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
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JButton;


public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 600; //width screen
    private final int B_HEIGHT = 600; //height screen
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 3600;
    private final int RAND_POS = 60;
    private int DELAY = 140; //snake speed

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int score=0;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
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

    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
    }

    private void updateTime(){
        DELAY -= 5; // speed increase when snake eats the apple
        if(DELAY >= 40) {
            timer.setDelay(DELAY);
        }
    }

    private void initGame() {

        dots = 2;

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

    private void doDrawing(Graphics g) {

        if (inGame) {

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
        } else {

            gameOver(g);
        }
    }

    private void gameOver(Graphics g) {

        String msg = "Game Over!";
        String msg2 = "Score: " + score + " " + "Length: " + dots;
        Font small = new Font("Comic Sans",Font.BOLD,14 );
        FontMetrics messageSize = getFontMetrics(small);

        g.setColor(Color.RED);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - messageSize.stringWidth(msg)) / 2, B_HEIGHT / 2);
        g.setColor(Color.WHITE);
        g.drawString(msg2, (B_WIDTH - messageSize.stringWidth(msg2)) / 2, (B_HEIGHT / 2) + 20);
        restartMessageDisplay(g);
    }

    private void Score(Graphics g) {
        Font small = new Font("Helvetica", Font.BOLD, 14);
        g.setColor(Color.WHITE);
        g.setFont(small);
        g.drawString("Score: " + score, 520, 25);
        g.drawString("Speed: " + timer.getDelay(), 520, 45);
    }

    public void restartMessageDisplay(Graphics g) {
        String restartMessage = " Press Enter to restart";
        Font small = new Font("Arial", Font.BOLD, 14);
        g.setColor(Color.YELLOW);
        FontMetrics metrics = getFontMetrics(small);
        g.drawString(restartMessage, (B_WIDTH - metrics.stringWidth(restartMessage)) / 2, (B_HEIGHT / 2) + 40);

    }


    private void checkApple() {
        double randNeg =(Math.random());
        double randPosi =(Math.random());
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            if(randNeg < 0.5){
                dots--;
            }else{
                if(randPosi < 0.5){
                    dots++;
                }else{
                    dots+=3;
                }
            }
            score++;
            updateTime();
            locateApple();
        }
    }

    private void move() {

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

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }
        if(dots == 0){
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }

    }

    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();

        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
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
        }
    }
}
