package br.com.mvbos.lgj.boxing;

import java.awt.Color;
import java.awt.Graphics2D;
import br.com.mvbos.lgj.base.Elemento;

public class Player extends Elemento {

    private int health = 100;
    private int score = 0;
    private boolean punching = false;
    private int punchCooldown = 0;
    private static final int PUNCH_DELAY = 20;
    private static final int PUNCH_REACH = 15;

    private boolean up, down, left, right;
    private boolean punchKey;
    
    public static final int DIR_RIGHT = 0;
    public static final int DIR_LEFT = 1;
    public static final int DIR_UP = 2;
    public static final int DIR_DOWN = 3;
    
    private int direction = DIR_RIGHT;

    public Player(int px, int py) {
        super(px, py, 30, 30);
        setVel(3);
    }

    @Override
    public void atualiza() {
        if (punchCooldown > 0) {
            punchCooldown--;
            if (punchCooldown < 10) {
                punching = false;
            }
        }

        if (punchKey && punchCooldown == 0) {
            punching = true;
            punchCooldown = PUNCH_DELAY;
        }
        
        if (up) incPy(-getVel());
        if (down) incPy(getVel());
        if (left) incPx(-getVel());
        if (right) incPx(getVel());
    }

    @Override
    public void desenha(Graphics2D g) {
        super.desenha(g);
        drawPunch(g);
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void drawPunch(Graphics2D g) {
         if (punching) {
            g.setColor(Color.RED);
            int px = getPx();
            int py = getPy();
            int w = getLargura();
            int h = getAltura();
            
            switch (direction) {
                case DIR_RIGHT: g.fillRect(px + w, py + 10, PUNCH_REACH, 10); break;
                case DIR_LEFT:  g.fillRect(px - PUNCH_REACH, py + 10, PUNCH_REACH, 10); break;
                case DIR_UP:    g.fillRect(px + 10, py - PUNCH_REACH, 10, PUNCH_REACH); break;
                case DIR_DOWN:  g.fillRect(px + 10, py + h, 10, PUNCH_REACH); break;
            }
        }
    }

    public boolean isPunching() {
        return punching;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) this.health = 0;
    }

    public int getHealth() {
        return health;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    public void setUp(boolean up) { this.up = up; }
    public void setDown(boolean down) { this.down = down; }
    public void setLeft(boolean left) { this.left = left; }
    public void setRight(boolean right) { this.right = right; }
    public void setPunchKey(boolean punchKey) { this.punchKey = punchKey; }
    
    public void reset() {
        health = 100;
        score = 0;
        punching = false;
        punchCooldown = 0;
    }
}
