import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class BrickMap {
    public int[][] brickLayout;
    public int brickWidth;
    public int brickHeight;

    public BrickMap(int rows, int columns) {
        brickLayout = new int[rows][columns];
        for (int i = 0; i < brickLayout.length; i++) {
            for (int j = 0; j < brickLayout[0].length; j++) {
                brickLayout[i][j] = 1;
            }
        }
        brickWidth = 540 / columns;
        brickHeight = 150 / rows;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < brickLayout.length; i++) {
            for (int j = 0; j < brickLayout[0].length; j++) {
                if (brickLayout[i][j] > 0) {
                    g.setColor(new Color(0xFFAA33));
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    g.setStroke(new BasicStroke(4));
                    g.setColor(Color.BLACK);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        brickLayout[row][col] = value;
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener {
    private boolean isPlaying = true;
    private int playerScore = 0;
    private int totalBricks = 21;
    private Timer timer;
    private int delay = 8;
    private int playerX = 310;
    private int ballPosX = 120;
    private int ballPosY = 350;
    private int ballXDir = -1;
    private int ballYDir = -2;
    private BrickMap brickMap;

    public GamePanel() {
        brickMap = new BrickMap(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0xFFFFE0));
        g.fillRect(1, 1, 692, 592);
        brickMap.draw((Graphics2D) g);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);
        g.setColor(new Color(0x4169E1));
        g.fillRect(playerX, 550, 100, 12);
        g.setColor(new Color(0xDC143C));
        g.fillOval(ballPosX, ballPosY, 20, 20);
        g.setColor(Color.black);
        g.setFont(new Font("MV Boli", Font.BOLD, 25));
        g.drawString("Score: " + playerScore, 520, 30);
        if (totalBricks <= 0) {
            isPlaying = false;
            ballXDir = 0;
            ballYDir = 0;
            g.setColor(new Color(0XFF6464));
            g.setFont(new Font("MV Boli", Font.BOLD, 30));
            g.drawString("You Won, Score: " + playerScore, 190, 300);
            g.setFont(new Font("MV Boli", Font.BOLD, 20));
            g.drawString("Press Enter to Restart.", 230, 350);
        }
        if (ballPosY > 570) {
            isPlaying = false;
            ballXDir = 0;
            ballYDir = 0;
            g.setColor(Color.BLACK);
            g.setFont(new Font("MV Boli", Font.BOLD, 30));
            g.drawString("Game Over, Score: " + playerScore, 190, 300);
            g.setFont(new Font("MV Boli", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }
        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (isPlaying) {
            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 12))) {
                ballYDir = -ballYDir;
            }
            for (int i = 0; i < brickMap.brickLayout.length; i++) {
                for (int j = 0; j < brickMap.brickLayout[0].length; j++) {
                    if (brickMap.brickLayout[i][j] > 0) {
                        int brickX = j * brickMap.brickWidth + 80;
                        int brickY = i * brickMap.brickHeight + 50;
                        Rectangle brickRect = new Rectangle(brickX, brickY, brickMap.brickWidth, brickMap.brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);
                        if (ballRect.intersects(brickRect)) {
                            brickMap.setBrickValue(0, i, j);
                            totalBricks--;
                            playerScore += 5;
                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballXDir = -ballXDir;
                            } else {
                                ballYDir = -ballYDir;
                            }
                        }
                    }
                }
            }
            ballPosX += ballXDir;
            ballPosY += ballYDir;
            if (ballPosX < 0 || ballPosX > 670)
                ballXDir = -ballXDir;
            if (ballPosY < 0)
                ballYDir = -ballYDir;
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!isPlaying) {
                isPlaying = true;
                ballPosX = 120;
                ballPosY = 350;
                ballXDir = -1;
                ballYDir = -2;
                playerScore = 0;
                totalBricks = 21;
                brickMap = new BrickMap(3, 7);
                repaint();
            }
        }
    }

    public void moveRight() {
        playerX += 50;
    }

    public void moveLeft() {
        playerX -= 50;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

public class BrickBreakerGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        GamePanel gamePanel = new GamePanel();
        frame.setBounds(10, 10, 700, 600);
        frame.setTitle("Brick Breaker");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setVisible(true);
    }
}